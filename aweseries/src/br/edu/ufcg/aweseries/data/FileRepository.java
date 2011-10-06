package br.edu.ufcg.aweseries.data;

import java.io.InputStream;

public class FileRepository {

    public void save(String name, InputStream fileContent) {
        if (name == null) {
            throw new IllegalArgumentException("name should not be null");
        }
        if (fileContent == null) {
            throw new IllegalArgumentException("fileContent should not be null");
        }
    }
}
