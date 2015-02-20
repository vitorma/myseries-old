package mobi.myseries.application;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.*;

import mobi.myseries.shared.Strings;
import mobi.myseries.shared.Validate;

public class CommunicationsImpl implements Communications {

    private static final int READ_TIMEOUT = 10000 /* milliseconds */;
    private static final int CONNECT_TIMEOUT = 15000 /* milliseconds */;

    private final ConnectivityManager androidConnectivityManager;

    public CommunicationsImpl(Context context) {
        this.androidConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    public boolean isConnected() {
        NetworkInfo activeNetwork = activeNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    @Override
    public boolean isConnectedToWiFi() {
        NetworkInfo activeNetwork = activeNetworkInfo();
        return activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
    }

    private NetworkInfo activeNetworkInfo() {
        return androidConnectivityManager.getActiveNetworkInfo();
    }

    //TODO remove this method if all connections need headers
    @Override
    public InputStream streamFor(String url) throws ConnectionFailedException, NetworkUnavailableException {
        Validate.isNonNull(url, "url");
        Validate.isNonBlank(url, "url");

        if (!this.isConnected()) {
            throw new NetworkUnavailableException();
        }

        try {
            HttpURLConnection connection = buildHttpUrlConnection(url);
            connection.connect();

            return connection.getInputStream();
        } catch (IOException e) {
            throw new ConnectionFailedException(e);
        }
    }

    @Override
    public InputStream streamFor(String url, Map<String, String> properties) throws ConnectionFailedException, NetworkUnavailableException {
        Validate.isNonNull(url, "url");
        Validate.isNonBlank(url, "url");
        Validate.isNonNull(properties, "properties");

        if (!this.isConnected()) {
            throw new NetworkUnavailableException();
        }

        try {
            HttpURLConnection connection = buildHttpUrlConnection(url, properties);
            connection.connect();

            return connection.getInputStream();
        } catch (IOException e) {
            throw new ConnectionFailedException(e);
        }
    }

    private HttpURLConnection buildHttpUrlConnection(String urlString) throws MalformedURLException, IOException {
        //AndroidUtils.disableConnectionReuseIfNecessary();

        URL url = new URL(urlString);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(READ_TIMEOUT);
        conn.setConnectTimeout(CONNECT_TIMEOUT);
        conn.setDoInput(true);
        conn.setRequestMethod("GET");
        return conn;
    }

    private HttpURLConnection buildHttpUrlConnection(String urlString, Map<String,String> properties) throws MalformedURLException, IOException {
        HttpURLConnection conn = this.buildHttpUrlConnection(urlString);
        for(String property : properties.keySet()) {
            conn.setRequestProperty(property, properties.get(property));
        }
        return conn;
    }
}