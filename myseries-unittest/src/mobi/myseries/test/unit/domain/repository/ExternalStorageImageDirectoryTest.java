package mobi.myseries.test.unit.domain.repository;

import mobi.myseries.domain.repository.ExternalStorageImageDirectory;
import mobi.myseries.domain.repository.ImageStorage;

public class ExternalStorageImageDirectoryTest extends ImageRepositoryTest {

    @Override
    protected ImageStorage newRepository() {
        return new ExternalStorageImageDirectory(this.getInstrumentation().getContext(),
                                                 "image_repository_test_dir");
    }
}
