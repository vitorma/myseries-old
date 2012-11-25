package mobi.myseries.domain.repository.image;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import mobi.myseries.shared.Validate;
import android.graphics.Bitmap;
import android.util.SparseArray;

public class ImageRepositoryCache implements ImageRepository {

    private SparseArray<Bitmap> cachedImages;
    private Set<Integer> cachedImagesIds;  // this is here because so far (2012-11-21) there is no easy way to
                                           // get the collection of keys in a SparseArray.

    private ExecutorService threadExecutor;
    private ImageRepository cachedRepository;

    public ImageRepositoryCache(ImageRepository cachedRepository) {
        Validate.isNonNull(cachedRepository, "cachedRepository");

        this.cachedRepository = cachedRepository;
        this.threadExecutor = Executors.newSingleThreadExecutor();
        this.cachedImages = new SparseArray<Bitmap>();
        this.cachedImagesIds = new HashSet<Integer>();

        try {
            this.loadImagesFromCachedRepository();
        } catch (ImageRepositoryException e) {}  // The images cannot be loaded into the cache, but there is nothing we
                                                 // can do about it. The cache has to be constructed anyway.
    }

    private void loadImagesFromCachedRepository() throws ImageRepositoryException {
        for (int image : this.cachedRepository.savedImages()) {
            this.cachedImages.put(image, this.cachedRepository.fetch(image));
        }
    }

    @Override
    public void save(int id, Bitmap image) throws ImageRepositoryException {
        Validate.isNonNull(image, "image");

        this.cachedImages.put(id, image);
        this.cachedImagesIds.add(id);

        this.threadExecutor.execute(this.saveImageInTheCachedRepository(id, image));
    }

    private Runnable saveImageInTheCachedRepository(final int id, final Bitmap image) {
        return new Runnable() {

            @Override
            public void run() {
                try {
                    ImageRepositoryCache.this.cachedRepository.save(id, image);
                } catch (ImageRepositoryException e) {}
            }
        };
    }

    @Override
    public Bitmap fetch(int id) throws ImageRepositoryException {
        return this.cachedImages.get(id);
    }

    @Override
    public void delete(int id) throws ImageRepositoryException {
        this.cachedImages.delete(id);
        this.cachedImagesIds.remove(id);
        this.threadExecutor.execute(this.deleteImageFromTheCachedRepository(id));
    }

    private Runnable deleteImageFromTheCachedRepository(final int id) {
        return new Runnable() {

            @Override
            public void run() {
                try {
                    ImageRepositoryCache.this.cachedRepository.delete(id);
                } catch (ImageRepositoryException e) {}
            }
        };
    }

    @Override
    public Collection<Integer> savedImages() throws ImageRepositoryException {
        return Collections.unmodifiableCollection(this.cachedImagesIds);
    }
}
