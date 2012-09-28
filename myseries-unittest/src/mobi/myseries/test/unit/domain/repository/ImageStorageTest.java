package mobi.myseries.test.unit.domain.repository;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import mobi.myseries.domain.repository.ImageStorage;
import mobi.myseries.domain.repository.ExternalStorageImageDirectory;
import mobi.myseries.test.R;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.test.InstrumentationTestCase;

public class ImageStorageTest extends InstrumentationTestCase {
    private static final int NOT_SAVED_IMAGE_ID = 0;

    private Bitmap testImage;

    private ImageStorage imageStorage;

    public void setUp() {
        this.testImage = BitmapFactory.decodeResource(
                this.getInstrumentation().getContext().getResources(),
                R.drawable.icon);

        this.imageStorage = new ExternalStorageImageDirectory(this.getInstrumentation().getContext(), "storage_test_dir");
    }

    public void tearDown() {
        this.imageStorage = null;
    }

    public void testDeletingANotSavedImageDoesNotCauseAnyError() {
        this.imageStorage.delete(NOT_SAVED_IMAGE_ID);
    }

    public void testFetchingANotSavedImageReturnsNull() {
        assertThat(this.imageStorage.fetch(NOT_SAVED_IMAGE_ID), is(nullValue()));
    }

    public void testSavingANullImageThrowsException() {
        try {
            this.imageStorage.save(NOT_SAVED_IMAGE_ID, null);
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    @TargetApi(12)
	public void testFetchingASavedImageReturnsThatImage() {
        int imageId = NOT_SAVED_IMAGE_ID + 1;
        this.imageStorage.save(imageId, this.testImage);

        // TODO(gabriel) find a better way to compare the bitmaps
        assertThat(this.imageStorage.fetch(imageId), not(nullValue()));
    }

    public void testFetchingADeletedImageReturnsNull() {
        int imageId = NOT_SAVED_IMAGE_ID + 1;

        this.imageStorage.save(imageId, this.testImage);
        this.imageStorage.delete(imageId);

        assertThat(this.imageStorage.fetch(imageId), is(nullValue()));
    }
}
