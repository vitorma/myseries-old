package mobi.myseries.test.unit.domain.repository;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import mobi.myseries.domain.repository.ImageStorage;
import mobi.myseries.domain.repository.ExternalStorageImageDirectory;
import mobi.myseries.test.R;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.test.InstrumentationTestCase;

public class ImageRepositoryTest extends InstrumentationTestCase {
    private static final int NOT_SAVED_IMAGE_ID = 0;

    private Bitmap testImage;

    private ImageStorage imageRepository;

    public void setUp() {
        this.testImage = BitmapFactory.decodeResource(
                this.getInstrumentation().getContext().getResources(),
                R.drawable.icon);

        this.imageRepository = new ExternalStorageImageDirectory(this.getInstrumentation().getContext(), "storage_test_dir");
    }

    public void tearDown() {
        this.imageRepository = null;
    }

    public void testDeletingANotSavedImageDoesNotCauseAnyError() {
        this.imageRepository.delete(NOT_SAVED_IMAGE_ID);
    }

    public void testFetchingANotSavedImageReturnsNull() {
        assertThat(this.imageRepository.fetch(NOT_SAVED_IMAGE_ID), is(nullValue()));
    }

    public void testSavingANullImageThrowsException() {
        try {
            this.imageRepository.save(NOT_SAVED_IMAGE_ID, null);
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

	public void testFetchingASavedImageReturnsThatImage() {
        int imageId = NOT_SAVED_IMAGE_ID + 1;
        this.imageRepository.save(imageId, this.testImage);

        // TODO(gabriel) find a better way to compare the bitmaps
        assertThat(this.imageRepository.fetch(imageId), not(nullValue()));
    }

    public void testFetchingADeletedImageReturnsNull() {
        int imageId = NOT_SAVED_IMAGE_ID + 1;

        this.imageRepository.save(imageId, this.testImage);
        this.imageRepository.delete(imageId);

        assertThat(this.imageRepository.fetch(imageId), is(nullValue()));
    }

    // TODO(gabriel) test forbidden use of negative ids
}
