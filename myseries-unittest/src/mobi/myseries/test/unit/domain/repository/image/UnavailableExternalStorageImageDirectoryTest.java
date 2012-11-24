package mobi.myseries.test.unit.domain.repository.image;

import static org.mockito.Mockito.*;

import mobi.myseries.domain.repository.image.ExternalStorageImageDirectory;
import mobi.myseries.domain.repository.image.ImageRepository;
import mobi.myseries.domain.repository.image.ImageRepositoryException;
import mobi.myseries.test.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.test.InstrumentationTestCase;

public class UnavailableExternalStorageImageDirectoryTest extends InstrumentationTestCase {

    private static final int SOME_IMAGE_ID = -5;  // repositories must also work with negative ids
    private Bitmap testImage;

    private ImageRepository imageRepository;

    public void setUp() {
        this.testImage = BitmapFactory.decodeResource(
                this.getInstrumentation().getContext().getResources(),
                R.drawable.icon);

        Context contextWithoutExternalStorage =  mock(Context.class);
        when(contextWithoutExternalStorage.getExternalFilesDir(null)).thenReturn(null);

        this.imageRepository = new ExternalStorageImageDirectory(contextWithoutExternalStorage,
                                                                 "unavailable_image_repository_test_dir");
    }

    public void tearDown() {
        this.imageRepository = null;
        this.testImage = null;
    }

    public void testItShouldThrowAnExceptionWhenGettingTheCollectionOfSavedImages() {
        try {
            this.imageRepository.savedImages();
            fail("Should have thrown an ImageRepositoryException");
        } catch (ImageRepositoryException e) {}
    }

    public void testItShouldThrowAnExceptionWhenSaving() {
        try {
            this.imageRepository.save(SOME_IMAGE_ID, this.testImage);
            fail("Should have thrown an ImageRepositoryException");
        } catch (ImageRepositoryException e) {}
    }

    public void testItShouldThrowAnExceptionWhenFetchingAnyImage() {
        try {
            this.imageRepository.fetch(SOME_IMAGE_ID);
            fail("Should have thrown an ImageRepositoryException");
        } catch (ImageRepositoryException e) {}
    }

    public void testItShouldThrowAnExceptionWhenDeletingAnyImage() {
        try {
            this.imageRepository.delete(SOME_IMAGE_ID);
            fail("Should have thrown an ImageRepositoryException");
        } catch (ImageRepositoryException e) {}
    }
}
