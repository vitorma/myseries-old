package br.edu.ufcg.aweseries.test.acceptance.util;

import br.edu.ufcg.aweseries.test.util.ChuckSeries;
import br.edu.ufcg.aweseries.test.util.StreamFactoryTest;
import br.edu.ufcg.aweseries.thetvdb.StreamFactory;

public class TestStreamFactoryTest extends StreamFactoryTest {

    private StreamFactory streamFactory = new TestStreamFactory();

    @Override
    protected StreamFactory factory() {
        ChuckSeries.resources = getInstrumentation().getContext().getResources();
        return this.streamFactory;
    }
}
