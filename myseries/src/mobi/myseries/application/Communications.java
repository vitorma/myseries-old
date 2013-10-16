package mobi.myseries.application;

import java.io.InputStream;

public interface Communications {
    public boolean isConnected();
    public boolean isConnectedToWiFi();

    public InputStream streamFor(String url) throws ConnectionFailedException;
}
