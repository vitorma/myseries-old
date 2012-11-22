package mobi.myseries.test.unit.domain.repository.image;

import mobi.myseries.domain.repository.image.ExternalStorageImageDirectory;
import mobi.myseries.domain.repository.image.ImageRepository;
import mobi.myseries.domain.repository.image.LruRepositoryManager;

public class LruManagedExternalStorageImageDirectoryTest extends ImageRepositoryTest {

    @Override
    protected ImageRepository newRepository() {
        int numberOfKeptImages = 1;
        ImageRepository managedRepository = new ExternalStorageImageDirectory(this.getInstrumentation().getContext(),
                                                                              "lru_managed_image_repository_test_dir");

        return new LruRepositoryManager(managedRepository, numberOfKeptImages);
    }
}
