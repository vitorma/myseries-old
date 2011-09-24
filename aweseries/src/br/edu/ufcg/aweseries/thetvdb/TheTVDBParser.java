package br.edu.ufcg.aweseries.thetvdb;

import java.io.InputStream;

public abstract class TheTVDBParser<T> implements Parser<T> {

	private InputStream inputStream;

    protected TheTVDBParser(InputStream inputStream) {
    	this.inputStream = inputStream;
    }

    protected InputStream getInputStream() {
    	return this.inputStream;
    }
}
