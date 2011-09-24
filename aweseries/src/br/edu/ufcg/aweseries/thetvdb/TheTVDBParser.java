package br.edu.ufcg.aweseries.thetvdb;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class TheTVDBParser<T> implements Parser<T> {

	private InputStream inputStream;

    protected TheTVDBParser(InputStream inputStream) {
    	this.inputStream = inputStream;
    }

    @Deprecated
    protected TheTVDBParser(String url) {
        try {
        	this.inputStream = new URL(url).openConnection().getInputStream();
        } catch (MalformedURLException e) {
        	throw new RuntimeException(e);
        } catch (IOException e) {
        	throw new RuntimeException(e);
        }
    }

    protected InputStream getInputStream() {
    	return this.inputStream;
    }
}
