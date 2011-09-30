package br.edu.ufcg.aweseries.test.integration;

import br.edu.ufcg.aweseries.test.util.StreamFactoryTest;
import br.edu.ufcg.aweseries.thetvdb.stream.StreamFactory;
import br.edu.ufcg.aweseries.thetvdb.stream.TheTVDBStreamFactory;

public class TheTVDBStreamFactoryTest extends StreamFactoryTest {

    private final String apiKey = "6F2B5A871C96FB05";
    private StreamFactory factory = new TheTVDBStreamFactory(apiKey);

    @Override
    protected StreamFactory factory() {
        return this.factory;
    }
}
