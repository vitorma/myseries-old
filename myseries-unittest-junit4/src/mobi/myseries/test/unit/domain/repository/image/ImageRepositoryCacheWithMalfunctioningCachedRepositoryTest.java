package mobi.myseries.test.unit.domain.repository.image;

import static org.hamcrest.CoreMatchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import mobi.myseries.domain.repository.image.ImageRepository;
import mobi.myseries.domain.repository.image.ImageRepositoryException;
import android.graphics.Bitmap;

public class ImageRepositoryCacheWithMalfunctioningCachedRepositoryTest extends ImageRepositoryCacheTest {

    // The ImageRepositoryCache should work as if no problem has happened underneath it. Trying to deal with such
    // problems may lead to strange behaviour in the program such as the images being reachable and then starting being
    // unreachable. It would happen, for example, if we delete the image from the cache in order to make it resemble
    // the cached repository when the its save operation throws an exception.
    //
    // Hence, the tests for an ImageRepositoryCache with a malfunctioning cached repository should be the same as the
    // tests for an ImageRepositoryCache with a functioning cached repository. 

    @Override
    protected ImageRepository newRepositoryToBeCached() throws ImageRepositoryException {
        ImageRepository malfunctioningRepository = mock(ImageRepository.class);
        doThrow(new ImageRepositoryException()).when(malfunctioningRepository).delete(anyInt());
        doThrow(new ImageRepositoryException()).when(malfunctioningRepository).save(anyInt(), argThat(any(Bitmap.class)));
        when(malfunctioningRepository.fetch(anyInt())).thenThrow(new ImageRepositoryException());
        when(malfunctioningRepository.savedImages()).thenThrow(new ImageRepositoryException());

        return malfunctioningRepository;
    }
}
