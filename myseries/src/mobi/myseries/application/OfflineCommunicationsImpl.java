package mobi.myseries.application;

import java.io.InputStream;

public class OfflineCommunicationsImpl implements Communications {

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public boolean isConnectedToWiFi() {
        return false;
    }

    @Override
    public InputStream streamFor(String url) throws ConnectionFailedException, NetworkUnavailableException {
        if (isConnected()) {
            throw new ConnectionFailedException();
        } else {
            throw new NetworkUnavailableException();
        }
    }
}