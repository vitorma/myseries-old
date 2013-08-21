package mobi.myseries.test.unit.domain.repository.image;

import static org.hamcrest.CoreMatchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import mobi.myseries.domain.repository.image.ImageRepository;
import mobi.myseries.domain.repository.image.ImageRepositoryCache;
import mobi.myseries.domain.repository.image.ImageRepositoryException;

import org.junit.Test;

import android.graphics.Bitmap;

public class ImageRepositoryCacheTest extends ImageCacheTest {

    @Override
    protected ImageRepository newImageCache(ImageRepository cachedRepository) {
        return new ImageRepositoryCache(cachedRepository);
    }

    /* Construction */

    @Test(expected=IllegalArgumentException.class)
    public void constructingItWithANullRepositoryCausesIllegalArgumentException() {
        new ImageRepositoryCache(null);
    }

    @Test
    public void itMustBeConstructedEvenIfTheCachedRepositoryIsNotWorking() throws ImageRepositoryException {
        ImageRepository malfunctioningRepository = mock(ImageRepository.class);
        doThrow(new ImageRepositoryException()).when(malfunctioningRepository).delete(anyInt());
        doThrow(new ImageRepositoryException()).when(malfunctioningRepository).save(anyInt(), argThat(any(Bitmap.class)));
        when(malfunctioningRepository.fetch(anyInt())).thenThrow(new ImageRepositoryException());
        when(malfunctioningRepository.savedImages()).thenThrow(new ImageRepositoryException());

        new ImageRepositoryCache(malfunctioningRepository);
    }

    /* Pre-fetching during construction */

    @Test
    public void theAlreadySavedImagesMustBePrefetchedDuringConstruction() throws ImageRepositoryException {
        List<Integer> returnedImages = Arrays.asList(1, 2, 3, 4, 5);

        ImageRepository cachedRepository = mock(ImageRepository.class);
        when(cachedRepository.savedImages()).thenReturn(returnedImages);

        new ImageRepositoryCache(cachedRepository);

        verify(cachedRepository).savedImages();
        for (int image : returnedImages) {
            verify(cachedRepository).fetch(image);
        }
    }

    /* Fetching */

    @Test
    public void fetchingAnAlreadySavedImageImmediatelyAfterConstructionDoesNotTouchTheCachedRepository()
            throws ImageRepositoryException {
        List<Integer> returnedImages = Arrays.asList(1, 2, 3, 4, 5);
        int fetchedImage = 1;

        ImageRepository cachedRepository = mock(ImageRepository.class);
        when(cachedRepository.savedImages()).thenReturn(returnedImages);

        newImageCache(cachedRepository).fetch(fetchedImage);

        verify(cachedRepository, times(1)).fetch(fetchedImage);  // only for prefetching
    }

    /* Saved Images */

    @Test
    public void queryingTheCollectionOfSavedImagesMustNotTouchTheCachedRepository() throws ImageRepositoryException {
        reset(this.cachedRepository);  // to ignore prefetching

        this.cache.savedImages();

        verify(this.cachedRepository, never()).savedImages();
    }
}
