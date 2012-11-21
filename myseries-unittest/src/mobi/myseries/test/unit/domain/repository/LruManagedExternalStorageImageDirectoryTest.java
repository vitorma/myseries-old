package mobi.myseries.test.unit.domain.repository;

import mobi.myseries.domain.repository.ExternalStorageImageDirectory;
import mobi.myseries.domain.repository.ImageStorage;
import mobi.myseries.domain.repository.LruRepositoryManager;

public class LruManagedExternalStorageImageDirectoryTest extends ImageRepositoryTest {

    @Override
    protected ImageStorage newRepository() {
        int numberOfKeptImages = 1;
        ImageStorage managedRepository = new ExternalStorageImageDirectory(this.getInstrumentation().getContext(),
                                                                              "lru_managed_image_repository_test_dir");

        return new LruRepositoryManager(managedRepository, numberOfKeptImages);
    }
}
