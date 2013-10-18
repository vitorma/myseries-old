package mobi.myseries.test.unit.domain.repository.image;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import mobi.myseries.domain.repository.image.ImageRepository;
import mobi.myseries.domain.repository.image.ImageRepositoryException;
import mobi.myseries.testutil.CatchAllExceptionsRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.graphics.Bitmap;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Bitmap.class)
@Ignore("This is just a base class thus should not be run.")
public abstract class ImageCacheTest {

    private static int MAXIMUM_TIME_FOR_ASYNC_OPERATIONS = 1000;  // milliseconds

    protected static int NOT_USED_IMAGE_ID = 0;
    protected static int ID_OF_THE_FIRST_SAVED_IMAGE = 1;
    protected Bitmap DEFAULT_IMAGE = PowerMockito.mock(Bitmap.class);  // This is not static because of a PowerMockito
                                                                       // issue when the tests are run from ant.
    private Bitmap DEFAULT_IMAGE2 = PowerMockito.mock(Bitmap.class);

    protected ImageRepository cachedRepository;
    protected ImageRepository cache;

    // This was supposed to be used with @Rule annotation, but PowerMock doesn't want to work this way.
    // So, there is a call to its setUp() method in @Before and a call to its tearDown() method in @After.
    public CatchAllExceptionsRule noUnhandledExceptions = new CatchAllExceptionsRule();

    protected ImageRepository newRepositoryToBeCached() throws ImageRepositoryException {
        return mock(ImageRepository.class);
    }

    protected abstract ImageRepository newImageCache(ImageRepository cachedRepository);

    @Before
    public void setUp() throws ImageRepositoryException {
        this.cachedRepository = this.newRepositoryToBeCached();
        this.cache = this.newImageCache(this.cachedRepository);

        this.noUnhandledExceptions.setUp();
    }

    @After
    public void tearDown() {
        this.cachedRepository = null;
        this.cache = null;

        this.noUnhandledExceptions.after();
    }

    /* Fetching */

    @Test
    public void fetchingANotSavedImageReturnsNull() throws ImageRepositoryException {
        assertThat(this.cache.fetch(NOT_USED_IMAGE_ID), nullValue());
    }

    @Test
    public void fetchingAnAlreadySavedImageImmediatelyAfterConstructionReturnsTheSavedImage()
            throws ImageRepositoryException {
        Collection<Long> returnedImages = Arrays.asList(1L, 2L, 3L, 4L, 5L);
        int fetchedImageId = 1;

        ImageRepository cachedRepository = mock(ImageRepository.class);
        when(cachedRepository.savedImages()).thenReturn(returnedImages);
        when(cachedRepository.fetch(fetchedImageId)).thenReturn(DEFAULT_IMAGE);

        Bitmap fetchedImage = newImageCache(cachedRepository).fetch(fetchedImageId);

        assertThat(fetchedImage, sameInstance(DEFAULT_IMAGE));
    }

    @Test
    public void fetchingAJustSavedImageReturnsTheSavedImage() throws ImageRepositoryException {
        int imageId = ID_OF_THE_FIRST_SAVED_IMAGE;
        Bitmap savedImage = DEFAULT_IMAGE;

        this.cache.save(imageId, savedImage);

        Bitmap fetchedImage = this.cache.fetch(imageId);

        assertThat(fetchedImage, sameInstance(savedImage));
    }

    @Test
    public void fetchingAJustReplacedImageReturnsTheLastSavedImage() throws ImageRepositoryException {
        int imageId = ID_OF_THE_FIRST_SAVED_IMAGE;
        Bitmap firstSavedImage = DEFAULT_IMAGE;
        Bitmap secondSavedImage = DEFAULT_IMAGE;

        this.cache.save(imageId, firstSavedImage);
        this.cache.save(imageId, secondSavedImage);

        Bitmap fetchedImage = this.cache.fetch(imageId);

        assertThat(fetchedImage, sameInstance(secondSavedImage));
    }

    @Test
    public void fetchingAJustDeletedImageReturnsNull() throws ImageRepositoryException {
        int imageId = ID_OF_THE_FIRST_SAVED_IMAGE;
        Bitmap savedImage = DEFAULT_IMAGE;

        this.cache.save(imageId, savedImage);
        this.cache.delete(imageId);

        assertThat(this.cache.fetch(imageId), nullValue());
    }

    /* Saving */

    @Test(expected=IllegalArgumentException.class)
    public void savingANullImageShouldThrowAnIllegalArgumentException() throws ImageRepositoryException {
        this.cache.save(ID_OF_THE_FIRST_SAVED_IMAGE, null);
    }

    @Test
    public void savingAnImageForTheFirstTimeTouchesTheCachedRepository() throws ImageRepositoryException {
        int imageId = ID_OF_THE_FIRST_SAVED_IMAGE;
        Bitmap savedImage = DEFAULT_IMAGE;

        this.cache.save(imageId, savedImage);

        verify(this.cachedRepository, timeout(MAXIMUM_TIME_FOR_ASYNC_OPERATIONS)).save(imageId, savedImage);
    }

    @Test
    public void savingAnImageForTheSecondTimeTouchesTheCachedRepositoryTwice() throws ImageRepositoryException {
        int imageId = ID_OF_THE_FIRST_SAVED_IMAGE;
        Bitmap firstlySavedImage = DEFAULT_IMAGE;
        Bitmap secondlySavedImage = DEFAULT_IMAGE2;

        this.cache.save(imageId, firstlySavedImage);
        this.cache.save(imageId, secondlySavedImage);

        verify(this.cachedRepository, timeout(MAXIMUM_TIME_FOR_ASYNC_OPERATIONS)).save(imageId, firstlySavedImage);
        verify(this.cachedRepository, timeout(MAXIMUM_TIME_FOR_ASYNC_OPERATIONS)).save(imageId, secondlySavedImage);
    }

    /* Deleting */

    @Test
    public void deletingAnImageMustDeleteItFromTheCachedRepository() throws ImageRepositoryException {
        int deletedImageId = ID_OF_THE_FIRST_SAVED_IMAGE;

        this.cache.delete(deletedImageId);

        verify(this.cachedRepository, timeout(MAXIMUM_TIME_FOR_ASYNC_OPERATIONS)).delete(deletedImageId);
    }

    /* Saved Images */

    @Test
    public void retrievingTheSavedImagesReturnsTheRightIds() throws ImageRepositoryException {
        long imageId1 = ID_OF_THE_FIRST_SAVED_IMAGE;
        long imageId2 = imageId1 + 1;
        long imageId3 = imageId2 + 1;
        Bitmap savedImage = DEFAULT_IMAGE;

        ImageRepository cachedRepository = mock(ImageRepository.class);
        when(cachedRepository.savedImages())
                .thenReturn(Arrays.asList(imageId1, imageId2, imageId3));
        ImageRepository cache = newImageCache(cachedRepository);

        cache.save(imageId1, savedImage);
        cache.save(imageId2, savedImage);
        cache.save(imageId3, savedImage);

        assertThat(cache.savedImages(), hasItems(imageId1, imageId2, imageId3));
        assertThat(cache.savedImages().size(), equalTo(3));
    }    
}
