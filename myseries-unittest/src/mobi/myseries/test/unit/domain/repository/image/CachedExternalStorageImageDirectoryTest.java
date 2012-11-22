package mobi.myseries.test.unit.domain.repository.image;

import mobi.myseries.domain.repository.image.ExternalStorageImageDirectory;
import mobi.myseries.domain.repository.image.ImageRepositoryCache;
import mobi.myseries.domain.repository.image.ImageRepository;

public class CachedExternalStorageImageDirectoryTest extends ImageRepositoryTest {

    @Override
    protected ImageRepository newRepository() {
        ImageRepository cachedRepository = new ExternalStorageImageDirectory(this.getInstrumentation().getContext(),
                                                                             "lru_managed_image_repository_test_dir");

        return new ImageRepositoryCache(cachedRepository);
    }
}
