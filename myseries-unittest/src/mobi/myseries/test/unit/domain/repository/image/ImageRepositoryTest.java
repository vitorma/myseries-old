package mobi.myseries.test.unit.domain.repository.image;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import mobi.myseries.domain.repository.image.ImageRepository;
import mobi.myseries.domain.repository.image.ImageRepositoryException;
import mobi.myseries.test.R;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.test.InstrumentationTestCase;

/**
 * The set of tests that any ImageRepository must pass.
 *
 * This should be derived for each significant way of instantiating ImageRepositories, i.e., all the possible leaves of
 * ImageRepository trees and the used trees.
 *
 * @author Gabriel Assis Bezerra <gabriel@myseries.mobi>
 */
public abstract class ImageRepositoryTest extends InstrumentationTestCase {

    private static final int NOT_SAVED_IMAGE_ID = -5;  // repositories must also work with negative ids

    private Bitmap testImage;

    private ImageRepository imageRepository;

    protected abstract ImageRepository newRepository();

    public void setUp() {
        this.testImage = BitmapFactory.decodeResource(
                this.getInstrumentation().getContext().getResources(),
                R.drawable.icon);

        this.imageRepository = this.newRepository();
    }

    public void tearDown() throws ImageRepositoryException {
        for (int imageId : this.imageRepository.savedImages()) {
            this.imageRepository.delete(imageId);
        }
        this.imageRepository = null;
        this.testImage = null;
    }

    public void testDeletingANotSavedImageDoesNotCauseAnyError() throws ImageRepositoryException {
        this.imageRepository.delete(NOT_SAVED_IMAGE_ID);
    }

    public void testFetchingANotSavedImageReturnsNull() throws ImageRepositoryException {
        assertThat(this.imageRepository.fetch(NOT_SAVED_IMAGE_ID), is(nullValue()));
    }

    public void testSavingANullImageThrowsException() throws ImageRepositoryException {
        try {
            this.imageRepository.save(NOT_SAVED_IMAGE_ID, null);
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    public void testFetchingASavedImageReturnsThatImage() throws ImageRepositoryException {
        int imageId = NOT_SAVED_IMAGE_ID + 1;
        this.imageRepository.save(imageId, this.testImage);

        // TODO(gabriel) find a better way to compare the bitmaps
        assertThat(this.imageRepository.fetch(imageId), not(nullValue()));
    }

    public void testFetchingADeletedImageReturnsNull() throws ImageRepositoryException {
        int imageId = NOT_SAVED_IMAGE_ID + 1;
        this.imageRepository.save(imageId, this.testImage);
        this.imageRepository.delete(imageId);

        assertThat(this.imageRepository.fetch(imageId), is(nullValue()));
    }

    public void testAJustSavedImageIsInTheCollectionOfSavedImages() throws ImageRepositoryException {
        int imageId = NOT_SAVED_IMAGE_ID + 1;
        this.imageRepository.save(imageId, this.testImage);

        assertThat(this.imageRepository.savedImages(), containsInAnyOrder(imageId));
    }

    public void testADeletedImageIsNotInTheCollectionOfSavedImagesAnymore() throws ImageRepositoryException {
        int imageId = NOT_SAVED_IMAGE_ID + 1;
        this.imageRepository.save(imageId, this.testImage);
        this.imageRepository.delete(imageId);

        assertThat(this.imageRepository.savedImages(), not(containsInAnyOrder(imageId)));
    }

    public void testTheCollectionOfSavedImagesIsNeverNull() throws ImageRepositoryException {
        assertThat(this.imageRepository.savedImages(), not(nullValue()));
    }

    public void testNotSavedImagesAreNotInTheCollectionOfSavedImages() throws ImageRepositoryException {
        assertThat(this.imageRepository.savedImages(), not(containsInAnyOrder(NOT_SAVED_IMAGE_ID)));
    }

    public void testTheCollectionOfSavedImagesStartsEmpty() throws ImageRepositoryException {
        assertTrue(this.imageRepository.savedImages().isEmpty());
    }
}
