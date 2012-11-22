package mobi.myseries.test.unit.domain.repository.image;

import mobi.myseries.domain.repository.image.ExternalStorageImageDirectory;
import mobi.myseries.domain.repository.image.ImageRepository;

public class ExternalStorageImageDirectoryTest extends ImageRepositoryTest {

    @Override
    protected ImageRepository newRepository() {
        return new ExternalStorageImageDirectory(this.getInstrumentation().getContext(),
                                                 "image_repository_test_dir");
    }
}
