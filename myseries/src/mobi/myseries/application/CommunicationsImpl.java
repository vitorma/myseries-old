package mobi.myseries.application;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import mobi.myseries.domain.source.ConnectionFailedException;
import mobi.myseries.shared.Validate;

public class CommunicationsImpl implements Communications {

    private static final int READ_TIMEOUT = 10000 /* milliseconds */;
    private static final int CONNECT_TIMEOUT = 15000 /* milliseconds */;

    @Override
    public InputStream streamFor(String url) throws ConnectionFailedException {
        Validate.isNonNull(url, "url");
        Validate.isNonBlank(url, "url");

        try {
            HttpURLConnection connection = buildHttpUrlConnection(url);
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
}