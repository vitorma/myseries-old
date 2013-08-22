package mobi.myseries.domain.repository.image;

import java.util.Collection;

import mobi.myseries.shared.Validate;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class LruImageCache implements ImageRepository {

    private final LruCache<Integer, Bitmap> cache;
    private final ImageRepository cachedRepository;

    public LruImageCache(ImageRepository cachedRepository, int numberOfCachedImages) {
        Validate.isNonNull(cachedRepository, "cachedRepository");

        this.cache = new LruCache<Integer, Bitmap>(numberOfCachedImages);
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
            //Log.d(getClass().getName(), "Image cache miss: " + id);
            Bitmap fetchedImage = this.cachedRepository.fetch(id);

            if (fetchedImage == null) {
                return null;
            } else {
                this.cache.put(id, fetchedImage);
                return fetchedImage;
            }
        } else {
            //Log.d(getClass().getName(), "Image cache hit: " + id);
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
