package mobi.myseries.domain.repository.image;

import java.util.Collection;

import mobi.myseries.application.Log;
import mobi.myseries.shared.Validate;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class LruImageCache implements ImageRepository {

    private final LruCache<Integer, Bitmap> cache;
    private final ImageRepository cachedRepository;

    public LruImageCache(ImageRepository cachedRepository, int maxCacheSizeInKilobytes) {
        Validate.isNonNull(cachedRepository, "cachedRepository");

        Log.d(getClass().getName(), "LRU Cache size (KiB) : " + maxCacheSizeInKilobytes);
        
        this.cache = new LruCache<Integer, Bitmap>(maxCacheSizeInKilobytes) {
        	@Override
            protected int sizeOf(Integer key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
        
        this.cachedRepository = cachedRepository;
    }

    @Override
    public void save(int id, Bitmap image) throws ImageRepositoryException {
        Validate.isNonNull(image, "image");

        this.cachedRepository.save(id, image);
        this.cache.put(id, image);
    }

    @Override
    public Bitmap fetch(int id) throws ImageRepositoryException {
        Bitmap cachedImage = this.cache.get(id);

        if (cachedImage == null) {
            Log.d(getClass().getName(), "Image cache miss: " + id);
            Bitmap fetchedImage = this.cachedRepository.fetch(id);

            if (fetchedImage == null) {
                return null;
            } else {
                this.cache.put(id, fetchedImage);
                return fetchedImage;
            }
        } else {
            Log.d(getClass().getName(), "Image cache hit: " + id);
            return cachedImage;
        }
    }

    @Override
    public void delete(int id) throws ImageRepositoryException {
        this.cache.remove(id);
        this.cachedRepository.delete(id);
    }

    @Override
    public Collection<Integer> savedImages() throws ImageRepositoryException {
        return this.cachedRepository.savedImages();
    }
}
