package mobi.myseries.test.unit.domain.repository.image;

import mobi.myseries.domain.repository.image.ImageRepository;
import mobi.myseries.domain.repository.image.ImageRepositoryException;
import mobi.myseries.domain.repository.image.LruImageCache;

import org.junit.Before;
import org.junit.Test;

import android.graphics.Bitmap;

import static org.hamcrest.CoreMatchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

public class LruImageCacheTest extends ImageCacheTest {

    private static final int DEFAULT_CACHE_SIZE = 3;

    private ImageRepository malfunctioningCache;

    @Before
    public void setUpMalfunctioningCache() throws ImageRepositoryException {
        ImageRepository malfunctioningCachedRepository = mock(ImageRepository.class);
        doThrow(new ImageRepositoryException()).when(malfunctioningCachedRepository).delete(anyInt());
        doThrow(new ImageRepositoryException()).when(malfunctioningCachedRepository).save(anyInt(), argThat(any(Bitmap.class)));
        when(malfunctioningCachedRepository.fetch(anyInt())).thenThrow(new ImageRepositoryException());
        when(malfunctioningCachedRepository.savedImages()).thenThrow(new ImageRepositoryException());

        this.malfunctioningCache = new LruImageCache(malfunctioningCachedRepository, DEFAULT_CACHE_SIZE);
    }

    @Override
    protected ImageRepository newImageCache(ImageRepository cachedRepository) {
        return new LruImageCache(cachedRepository, DEFAULT_CACHE_SIZE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void itMustNotAllowNegativeSize() {
        new LruImageCache(cachedRepository, -1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void itMustNotAllowNullCachedRepository() {
        new LruImageCache(null, DEFAULT_CACHE_SIZE);
    }

    /* Malfunctioning cached repository */

    @Test(expected=ImageRepositoryException.class)
    public void exceptionsMustNotBeCaughtByTheLruWhenSaving() throws ImageRepositoryException {
        Bitmap image = DEFAULT_IMAGE;
        int imageId = NOT_USED_IMAGE_ID;

        this.malfunctioningCache.save(imageId, image);
    }

    @Test(expected=ImageRepositoryException.class)
    public void exceptionsMustNotBeCaughtByTheLruWhenFetching() throws ImageRepositoryException {
        this.malfunctioningCache.fetch(NOT_USED_IMAGE_ID);
    }

    @Test(expected=ImageRepositoryException.class)
    public void exceptionsMustNotBeCaughtByTheLruWhenDeleting() throws ImageRepositoryException {
        this.malfunctioningCache.delete(NOT_USED_IMAGE_ID);
    }

    @Test(expected=ImageRepositoryException.class)
    public void exceptionsMustNotBeCaughtByTheLruWhenGettingTheCollectionOfSavedImages()
            throws ImageRepositoryException {
        this.malfunctioningCache.savedImages();
    }

    /* Eviction */

    @Test
    public void anEvictedImageShouldNotBeDeletedFromTheCachedRepository() throws ImageRepositoryException {
        int firstImageId = ID_OF_THE_FIRST_SAVED_IMAGE;
        int lastSavedId = this.fillWithTheDefaultImage(this.cache, DEFAULT_CACHE_SIZE, firstImageId);

        this.cache.save(lastSavedId + 1, DEFAULT_IMAGE);

        verify(this.cachedRepository, never()).delete(anyInt());
    }    

    /* Test tools */

    private int fillWithTheDefaultImage(ImageRepository repository, int numberOfImages, int firstImageId)
            throws ImageRepositoryException {
        assert numberOfImages > 0;
        Bitmap imageToBeSaved = DEFAULT_IMAGE;

        int idOfTheNextImageToBeSaved = firstImageId;
        int idOfTheLastSavedImage = -1;  // We cannot use null nor a valid image id.

        for (int i = 0; i < numberOfImages; ++i) {
            int imageId = idOfTheNextImageToBeSaved++;

            this.cache.save(imageId, imageToBeSaved);
            idOfTheLastSavedImage = imageId;
        }

        return idOfTheLastSavedImage;
    }
}
