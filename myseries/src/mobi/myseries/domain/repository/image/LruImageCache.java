package mobi.myseries.domain.repository.image;

import java.util.Collection;

import mobi.myseries.application.Log;
import mobi.myseries.shared.Validate;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class LruImageCache implements ImageRepository {

    private final LruCache<Long, Bitmap> cache;
    private final ImageRepository cachedRepository;

    public LruImageCache(ImageRepository cachedRepository, int maxCacheSizeInKilobytes) {
        Validate.isNonNull(cachedRepository, "cachedRepository");

        Log.d(getClass().getName(), "LRU Cache size (KiB) : " + maxCacheSizeInKilobytes);

        cache = new LruCache<Long, Bitmap>(maxCacheSizeInKilobytes) {
            @Override
            protected int sizeOf(Long key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };

        this.cachedRepository = cachedRepository;
    }

    @Override
    public void save(long id, Bitmap image) throws ImageRepositoryException {
        Validate.isNonNull(image, "image");

        cachedRepository.save(id, image);
        cache.put(id, image);
    }

    @Override
    public Bitmap fetch(long id) throws ImageRepositoryException {
        Bitmap cachedImage = fetchFromCache(id);

        if (cachedImage == null) {
            Log.d(getClass().getName(), "Image cache miss: " + id);
            Bitmap fetchedImage = cachedRepository.fetch(id);

            if (fetchedImage == null) {
                return null;
            } else {
                cache.put(id, fetchedImage);
                return fetchedImage;
            }
        } else {
            Log.d(getClass().getName(), "Image cache hit: " + id);
            return cachedImage;
        }
    }

    @Override
    public Bitmap fetchFromCache(long id) {
        Bitmap cachedImage = cache.get(id);
        return cachedImage;
    }

    @Override
    public void delete(long id) throws ImageRepositoryException {
        cache.remove(id);
        cachedRepository.delete(id);
    }

    @Override
    public Collection<Long> savedImages() throws ImageRepositoryException {
        return cachedRepository.savedImages();
    }
}
