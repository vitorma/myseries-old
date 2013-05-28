package mobi.myseries.application.backup;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import mobi.myseries.application.App;
import mobi.myseries.shared.FilesUtil;
import android.content.Context;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.ChildReference;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.ParentReference;

public class DriveBackup implements BackupMode {

    private static final String DATABASE_FILE_NAME = "myseries.db";
    private static final String DATABASE_MIME_TYPE = "application/octet-stream";
    private static final String FOLDER_MIME_TYPE = "application/vnd.google-apps.folder";
    private static final String FOLDER_NAME = "Apps/MySeries";

    private static Context context = App.context();
    private Drive service;
    private String account;

    private String folderId;
    private String fileId;
    private String folderPath;

    public DriveBackup(String account) {
        this.account = account;
        this.folderPath = FOLDER_NAME;
    }

    @Override
    public void backupDB(java.io.File backup) throws Exception {
        this.setupDriveService();
        this.ensuredDriveDirectory();
        this.uploadFileToDrive(backup);
    }

    @Override
    public java.io.File getBackup() throws Exception {
        this.setupDriveService();
        this.checkFoldersAndFiles();
        return this.downloadBackupFile();

    }

    private void setupDriveService() throws Exception {
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(context,
                DriveScopes.DRIVE);
        credential.setSelectedAccountName(account);
        this.service = new Drive.Builder(AndroidHttp.newCompatibleTransport(),
                new GsonFactory(), credential).build();

    }

    private void ensuredDriveDirectory() throws IOException {
        String parentId = "root";
        for (String folderName : folderPath.split("/")) {
            if (folderExists(parentId, folderName)) {
                folderId = this.getFolderId(parentId, folderName);
            } else {
                folderId = this.createFolder(parentId, folderName);
            }
            parentId = folderId;
        }
    }

    private String createFolder(String parentId, String folderName)
            throws IOException {
        File body = new File();
        body.setTitle(folderName);
        body.setMimeType(FOLDER_MIME_TYPE);
        body.setParents(Arrays.asList(new ParentReference().setId(parentId)));
        return service.files().insert(body).execute().getId();
    }

    private String getFolderId(String parentId, String folderName)
            throws IOException {
        return this.getFileId(parentId, folderName, FOLDER_MIME_TYPE);
    }

    private String getFileId(String parentId, String fileName, String mimeType)
            throws IOException {
        String query = getFileQuery(fileName, mimeType, false);
        List<ChildReference> queryResult = this.service.children()
                .list(parentId).setQ(query).execute().getItems();
        String id = null;
        if (!queryResult.isEmpty())
            id = this.service.children().list(parentId).setQ(query).execute()
                    .getItems().get(0).getId();
        return id;
    }

    private boolean fileExists(String parentId, String folderName,
            String mimeType) throws IOException {
        String query = getFileQuery(folderName, mimeType, false);
        boolean fileExists = !this.service.children().list(parentId)
                .setQ(query).execute().getItems().isEmpty();
        return fileExists;
    }

    private boolean folderExists(String parentId, String folderName)
            throws IOException {
        return this.fileExists(parentId, folderName, FOLDER_MIME_TYPE);
    }

    private String getFileQuery(String fileName, String mimeType,
            boolean trashed) {
        String query = "title='" + fileName + "' and mimeType='" + mimeType
                + "' and trashed=" + trashed;
        return query;
    }

    private void checkFoldersAndFiles() throws IOException {
        folderId = this.getLastChildFolderId();
        if (folderId == null)
            throw new IOException();

        fileId = this.getFileId(folderId, DATABASE_FILE_NAME,
                DATABASE_MIME_TYPE);
        if (fileId == null) {
            throw new IOException();
        }
    }

    private java.io.File downloadBackupFile() throws IOException {
        java.io.File cachedFile = null;
        File file = service.files().get(fileId).execute();
        InputStream input = this.downloadFile(service, file);
        cachedFile = new java.io.File(context.getCacheDir(), DATABASE_FILE_NAME);
        FilesUtil.writeFile(input, cachedFile);
        return cachedFile;
    }

    private String getLastChildFolderId() throws IOException {
        String parentId = "root";
        String lastChildId = null;
        for (String folderName : folderPath.split("/")) {
            if (!folderExists(parentId, folderName)) {
                return null;
            }
            lastChildId = this.getFolderId(parentId, folderName);
            parentId = lastChildId;
        }
        return lastChildId;
    }

    private InputStream downloadFile(Drive service, File file)
            throws IOException {
        if (file.getDownloadUrl() != null && file.getDownloadUrl().length() > 0) {
            HttpResponse resp = service.getRequestFactory()
                    .buildGetRequest(new GenericUrl(file.getDownloadUrl()))
                    .execute();
            return resp.getContent();
        } else {
            throw new IOException();
        }
    }

    private void uploadFileToDrive(java.io.File file) throws IOException {
        File fileUploaded = null;
        FileContent mediaContent = new FileContent(DATABASE_MIME_TYPE, file);
        File body = new File();
        body.setTitle(DATABASE_FILE_NAME);
        body.setParents(Arrays.asList(new ParentReference().setId(folderId)));

        if (fileExists(folderId, DATABASE_FILE_NAME, DATABASE_MIME_TYPE)) {
            this.fileId = this.getFileId(folderId, DATABASE_FILE_NAME,
                    DATABASE_MIME_TYPE);
            fileUploaded = service.files().update(fileId, body, mediaContent)
                    .execute();
        } else {
            fileUploaded = service.files().insert(body, mediaContent).execute();
        }

        if (fileUploaded != null) {
            this.fileId = fileUploaded.getId();
        } else {
            throw new IOException();
        }
    }
}
