package mobi.myseries.application;

import java.io.InputStream;

import mobi.myseries.domain.source.ConnectionFailedException;

public interface Communications {
    public InputStream streamFor(String url) throws ConnectionFailedException;
}
