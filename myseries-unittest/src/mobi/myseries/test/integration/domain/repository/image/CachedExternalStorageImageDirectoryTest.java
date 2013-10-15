package mobi.myseries.test.integration.domain.repository.image;

import mobi.myseries.domain.repository.image.ExternalStorageImageDirectory;
import mobi.myseries.domain.repository.image.ImageRepository;
import mobi.myseries.domain.repository.image.LruImageCache;
import mobi.myseries.test.unit.domain.repository.image.ImageRepositoryTest;

public class CachedExternalStorageImageDirectoryTest extends ImageRepositoryTest {

    @Override
    protected ImageRepository newRepository() {
        ImageRepository cachedRepository = new ExternalStorageImageDirectory(this.getInstrumentation().getContext(),
                                                                             "lru_managed_image_repository_test_dir");

        return new LruImageCache(cachedRepository, 1024);
    }
}
