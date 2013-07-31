package mobi.myseries.application.backup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import mobi.myseries.domain.source.ConnectionFailedException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxFileInfo;
import com.dropbox.client2.DropboxAPI.UploadRequest;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.TokenPair;
import com.dropbox.client2.session.Session.AccessType;

public class DropboxHelper {
    final static private AccessType ACCESS_TYPE = AccessType.APP_FOLDER;

    final static private String ACCOUNT_PREFS_NAME = "dropbox";
    final static private String ACCESS_KEY_NAME = "ACCESS_KEY";
    final static private String ACCESS_SECRET_NAME = "ACCESS_SECRET";
    private String APP_KEY;
    private String APP_SECRET;
    private DropboxAPI<AndroidAuthSession> api;
    private Context context;

    public DropboxHelper(Context context, String appKey, String appSecret) {
        this.APP_KEY = appKey;
        this.APP_SECRET = appSecret;
        this.context = context;
        AndroidAuthSession session = buildSession();
        api = new DropboxAPI<AndroidAuthSession>(session);
    }
    
    private AndroidAuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session;

        String[] stored = getKeys();
        if (stored != null) {
            AccessTokenPair accessToken = new AccessTokenPair(stored[0],
                    stored[1]);
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE,
                    accessToken);
        } else {
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
        }

        return session;
    }

    public String[] getKeys() {
        SharedPreferences prefs = getSharedPreferences();
        String key = prefs.getString(ACCESS_KEY_NAME, null);
        String secret = prefs.getString(ACCESS_SECRET_NAME, null);
        if (key != null && secret != null) {
            String[] ret = new String[2];
            ret[0] = key;
            ret[1] = secret;
            return ret;
        } else {
            return null;
        }
    }

    public void storeKeys(String key, String secret) {
        // Save the access key for later
        SharedPreferences prefs = getSharedPreferences();
        Editor edit = prefs.edit();
        edit.putString(ACCESS_KEY_NAME, key);
        edit.putString(ACCESS_SECRET_NAME, secret);
        edit.commit();
    }

    public void clearKeys() {
        SharedPreferences prefs = getSharedPreferences();
        Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
    }

    public void uploadFile(File file, String destinationPath) throws FileNotFoundException, DropboxException, ConnectionFailedException  {
        if(!isOnline())
            throw new ConnectionFailedException();
        FileInputStream inputStream = new FileInputStream(file);
        UploadRequest request = api.putFileOverwriteRequest(destinationPath, inputStream,
                file.length(), null);
        request.upload();
    }

    public DropboxAPI<AndroidAuthSession> getApi() {
        return this.api;
    }

    public File downloadFile(String sourceFilename, File destinationFile ) throws FileNotFoundException, DropboxException, ConnectionFailedException {
        if(!isOnline())
            throw new ConnectionFailedException();
        FileOutputStream outputStream = new FileOutputStream(destinationFile);
        DropboxFileInfo info = api.getFile(sourceFilename, null,
                outputStream, null);
        return destinationFile;
    }
    
    private SharedPreferences getSharedPreferences() {
        SharedPreferences prefs = context.getSharedPreferences(
                ACCOUNT_PREFS_NAME, Context.MODE_PRIVATE);
        return prefs;
    }

    public boolean onResume() {
        AndroidAuthSession session = api.getSession();
        if (session.authenticationSuccessful()) {
            session.finishAuthentication();
            TokenPair tokens = session.getAccessTokenPair();
            this.storeKeys(tokens.key, tokens.secret);
            return true;
        }
        return false;
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

}
