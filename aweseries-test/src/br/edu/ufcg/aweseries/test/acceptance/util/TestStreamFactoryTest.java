package br.edu.ufcg.aweseries.test.acceptance.util;

import br.edu.ufcg.aweseries.test.util.SampleSeries;
import br.edu.ufcg.aweseries.test.util.StreamFactoryTest;
import br.edu.ufcg.aweseries.thetvdb.stream.StreamFactory;

public class TestStreamFactoryTest extends StreamFactoryTest {

    private StreamFactory streamFactory = new TestStreamFactory();

    @Override
    protected StreamFactory factory() {
        SampleSeries.injectInstrumentation(getInstrumentation());
        return this.streamFactory;
    }
}
