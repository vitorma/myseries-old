package mobi.myseries.test.unit.domain.repository;

import mobi.myseries.domain.repository.ExternalStorageImageDirectory;
import mobi.myseries.domain.repository.ImageRepositoryCache;
import mobi.myseries.domain.repository.ImageStorage;

public class CachedExternalStorageImageDirectoryTest extends ImageRepositoryTest {

    @Override
    protected ImageStorage newRepository() {
        ImageStorage cachedRepository = new ExternalStorageImageDirectory(this.getInstrumentation().getContext(),
                                                                             "lru_managed_image_repository_test_dir");

        return new ImageRepositoryCache(cachedRepository);
    }
}
