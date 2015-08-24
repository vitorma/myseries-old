package mobi.myseries.application;

import java.io.InputStream;
import java.util.Map;

public interface Communications {
    public boolean isConnected();
    public boolean isConnectedToWiFi();

    public InputStream streamFor(String url) throws ConnectionFailedException, NetworkUnavailableException;

    public InputStream streamFor(String url, Map<String, String> properties) throws ConnectionFailedException, NetworkUnavailableException;
}
