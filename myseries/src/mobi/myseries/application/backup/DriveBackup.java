package mobi.myseries.application.backup;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import mobi.myseries.application.App;
import mobi.myseries.application.backup.exception.GoogleDriveCannotCreateFileException;
import mobi.myseries.application.backup.exception.GoogleDriveDownloadException;
import mobi.myseries.application.backup.exception.GoogleDriveException;
import mobi.myseries.application.backup.exception.GoogleDriveFileNotFoundException;
import mobi.myseries.application.backup.exception.GoogleDriveUploadException;
import mobi.myseries.domain.source.ConnectionFailedException;
import mobi.myseries.shared.FilesUtil;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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

    private static final String JSON_MIME_TYPE = "application/json";
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
        if(!isOnline()) {
            throw new ConnectionFailedException();
        }
        this.setupDriveService();
        this.ensuredDriveDirectory();
        this.uploadFileToDrive(backup);
    }

    @Override
    public void downloadBackupToFile(java.io.File backup) throws Exception {
        if(!isOnline()) {
            throw new ConnectionFailedException();
        }
        this.setupDriveService();
        this.checkFoldersAndFiles(backup);
        this.downloadBackupFile(backup);

    }

    private void setupDriveService() {
        GoogleAccountCredential credential = GoogleAccountCredential
                .usingOAuth2(context, DriveScopes.DRIVE);
        credential.setSelectedAccountName(account);
        this.service = new Drive.Builder(AndroidHttp.newCompatibleTransport(),
                new GsonFactory(), credential)
                .setApplicationName(App.getApplicationName()).build();
    }
    

    private void ensuredDriveDirectory() throws GoogleDriveException, GoogleDriveCannotCreateFileException {
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

    private String createFolder(String parentId, String folderName) throws GoogleDriveCannotCreateFileException {
        File body = new File();
        body.setTitle(folderName);
        body.setMimeType(FOLDER_MIME_TYPE);
        body.setParents(Arrays.asList(new ParentReference().setId(parentId)));
        try {
            return service.files().insert(body).execute().getId();
        } catch (IOException e) {
            throw new GoogleDriveCannotCreateFileException();
        }
    }

    private String getFolderId(String parentId, String folderName)
            throws GoogleDriveException {
        return this.getFileId(parentId, folderName, FOLDER_MIME_TYPE);
    }

    private String getFileId(String parentId, String fileName, String mimeType)
            throws GoogleDriveException {
        String query = getFileQuery(fileName, mimeType, false);
        List<ChildReference> queryResult;
        String id = null;
        try {
            queryResult = this.service.children().list(parentId).setQ(query)
                    .execute().getItems();
            if (!queryResult.isEmpty())
                id = this.service.children().list(parentId).setQ(query)
                        .execute().getItems().get(0).getId();
        } catch (IOException e) {
            throw new GoogleDriveException(e);
        }
        return id;
    }

    private boolean fileExists(String parentId, String folderName,
            String mimeType) throws GoogleDriveException {
        String query = getFileQuery(folderName, mimeType, false);
        boolean fileExists;
        try {
            fileExists = !this.service.children().list(parentId).setQ(query)
                    .execute().getItems().isEmpty();
        } catch (IOException e) {
            throw new GoogleDriveException(e);
        }
        return fileExists;
    }

    private boolean folderExists(String parentId, String folderName)
            throws GoogleDriveException {
        return this.fileExists(parentId, folderName, FOLDER_MIME_TYPE);
    }

    private String getFileQuery(String fileName, String mimeType,
            boolean trashed) {
        String query = "title='" + fileName + "' and mimeType='" + mimeType
                + "' and trashed=" + trashed;
        return query;
    }

    private void checkFoldersAndFiles(java.io.File file)
            throws GoogleDriveFileNotFoundException, IOException,
            GoogleDriveException {
        folderId = this.getLastChildFolderId();
        if (folderId == null)
            throw new GoogleDriveFileNotFoundException();

        fileId = this.getFileId(folderId, file.getName(), JSON_MIME_TYPE);
        if (fileId == null) {
            throw new GoogleDriveFileNotFoundException();
        }
    }

    private void downloadBackupFile(java.io.File backup) throws IOException,
            GoogleDriveDownloadException {
        File file = service.files().get(fileId).execute();
        InputStream input = this.downloadFile(service, file);
        FilesUtil.writeFile(input, backup);
    }

    private String getLastChildFolderId() throws GoogleDriveException {
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
            throws IOException, GoogleDriveDownloadException {
        if (file.getDownloadUrl() != null && file.getDownloadUrl().length() > 0) {
            HttpResponse resp = service.getRequestFactory()
                    .buildGetRequest(new GenericUrl(file.getDownloadUrl()))
                    .execute();
            return resp.getContent();
        } else {
            throw new GoogleDriveDownloadException();
        }
    }

    private void uploadFileToDrive(java.io.File file)
            throws GoogleDriveUploadException, GoogleDriveException,
            GoogleDriveCannotCreateFileException {
        File fileUploaded = null;
        FileContent mediaContent = new FileContent(JSON_MIME_TYPE, file);
        File body = new File();
        body.setTitle(file.getName());
        body.setParents(Arrays.asList(new ParentReference().setId(folderId)));

        try {
            if (fileExists(folderId, file.getName(), JSON_MIME_TYPE)) {
                this.fileId = this.getFileId(folderId, file.getName(),
                        JSON_MIME_TYPE);

                fileUploaded = service.files()
                        .update(fileId, body, mediaContent).execute();

            } else {
                fileUploaded = service.files().insert(body, mediaContent)
                        .execute();
            }
        } catch (IOException e) {
            throw new GoogleDriveCannotCreateFileException();
        }
        if (fileUploaded != null) {
            this.fileId = fileUploaded.getId();
        } else {
            throw new GoogleDriveUploadException();
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm =
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
    @Override
    public String name() {
        return "Google Drive";
    }
}
