package mobi.myseries.test.integration.application.image;

import mobi.myseries.application.image.AndroidImageServiceRepository;
import mobi.myseries.application.image.ImageServiceRepository;

public class AndroidImageServiceRepositoryTest extends ImageServiceRepositoryTest {

    @Override
    protected ImageServiceRepository newImageServiceRepository() {
        return new AndroidImageServiceRepository(this.getInstrumentation().getContext());
    }
}
