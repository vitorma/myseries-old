/*
 *   LruRepositoryManagerTest.java
 *
 *   Copyright 2012 MySeries Team.
 *
 *   This file is part of MySeries.
 *
 *   MySeries is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   MySeries is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with MySeries.  If not, see <http://www.gnu.org/licenses/>.
 */

package mobi.myseries.test.unit.domain.repository.image;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.intThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;

import mobi.myseries.domain.repository.image.ImageRepository;
import mobi.myseries.domain.repository.image.ImageRepositoryException;
import mobi.myseries.domain.repository.image.LruRepositoryManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.graphics.Bitmap;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Bitmap.class)
public class LruRepositoryManagerTest {

    private static int DEFAULT_CACHE_SIZE = 3;

    private static int NOT_USED_IMAGE_ID = 0;
    private static int ID_OF_THE_FIRST_SAVED_IMAGE = 1;
    private Bitmap DEFAULT_IMAGE = PowerMockito.mock(Bitmap.class);  // This is not static because of a PowerMockito
                                                                     // issue when the tests are run from ant.

    private ImageRepository managedRepository;
    private LruRepositoryManager manager;

    private ImageRepository malfunctioningRepository;
    private LruRepositoryManager malfunctioningManager;

    @Before
    public void setUp() throws ImageRepositoryException {
        this.managedRepository = mock(ImageRepository.class);
        when(this.managedRepository.fetch(intThat(is(not(equalTo(NOT_USED_IMAGE_ID)))))).thenReturn(DEFAULT_IMAGE);
        when(this.managedRepository.fetch(NOT_USED_IMAGE_ID)).thenReturn(null);

        this.malfunctioningRepository = mock(ImageRepository.class);
        doThrow(new ImageRepositoryException()).when(this.malfunctioningRepository).delete(anyInt());
        doThrow(new ImageRepositoryException()).when(this.malfunctioningRepository).save(anyInt(), argThat(any(Bitmap.class)));
        when(this.malfunctioningRepository.fetch(anyInt())).thenThrow(new ImageRepositoryException());

        this.manager = new LruRepositoryManager(this.managedRepository, DEFAULT_CACHE_SIZE);
        this.malfunctioningManager = new LruRepositoryManager(this.malfunctioningRepository, DEFAULT_CACHE_SIZE);
    }

    /* Construction */

    @Test(expected=IllegalArgumentException.class)
    public void constructingItWithANullRepositoryCausesIllegalArgumentException() {
        new LruRepositoryManager(null, DEFAULT_CACHE_SIZE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void constructingItWithZeroImagesCausesIllegalArgumentException() {
        new LruRepositoryManager(mock(ImageRepository.class), 0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void constructingItWithANegativeNumberOfKeptImagesCausesIllegalArgumentException() {
        new LruRepositoryManager(mock(ImageRepository.class), -1);
    }

    @Test
    public void itMustBeConstructedEvenIfTheManagedRepositoryIsNotWorking() {
        new LruRepositoryManager(this.malfunctioningRepository, DEFAULT_CACHE_SIZE);
    }

    /* Saving */

    @Test
    public void anImageSavedOnTheManagerShouldBeForwardedToTheManagedRepository() throws ImageRepositoryException {
        Bitmap image = DEFAULT_IMAGE;
        int imageId = NOT_USED_IMAGE_ID;

        this.manager.save(imageId, image);

        verify(this.managedRepository).save(imageId, image);
    }

    @Test
    public void anUpdateToASavedImageShouldBeForwardedToTheManagedRepository() throws ImageRepositoryException {
        Bitmap image = mock(Bitmap.class);
        int imageId = 0;

        this.manager.save(imageId, image);
        this.manager.save(imageId, image);

        verify(this.managedRepository, times(2)).save(imageId, image);
    }

    @Test(expected=ImageRepositoryException.class)
    public void exceptionsMustNotBeCaughtByTheLruWhenSaving() throws ImageRepositoryException {
        Bitmap image = DEFAULT_IMAGE;
        int imageId = NOT_USED_IMAGE_ID;

        this.malfunctioningManager.save(imageId, image);
    }

    /* Evicting */

    @Test
    public void noImagesShouldBeEvictedBeforeSavingMoreThanTheNumberOfKeptImages() throws ImageRepositoryException {
        this.fillWithTheDefaultImage(this.manager, DEFAULT_CACHE_SIZE, ID_OF_THE_FIRST_SAVED_IMAGE);

        verify(this.managedRepository, never()).delete(anyInt());
    }

    @Test
    public void noImagesShouldBeEvictedAfterUpdatingAnAlreadySavedImage() throws ImageRepositoryException {
        int firstImageId = ID_OF_THE_FIRST_SAVED_IMAGE;
        this.fillWithTheDefaultImage(this.manager, DEFAULT_CACHE_SIZE, firstImageId);

        // update the first image
        this.manager.save(firstImageId, DEFAULT_IMAGE);

        verify(this.managedRepository, never()).delete(anyInt());
    }

    @Test
    public void theOldestImageShouldBeEvictedAfterSavingANewImage() throws ImageRepositoryException {
        int firstImageId = ID_OF_THE_FIRST_SAVED_IMAGE;
        int lastSavedId = this.fillWithTheDefaultImage(this.manager, DEFAULT_CACHE_SIZE, firstImageId);

        this.manager.save(lastSavedId + 1, DEFAULT_IMAGE);

        verify(this.managedRepository).delete(firstImageId);
    }

    @Test
    public void noImagesShouldBeEvictedAfterFailingSavingANewImage() throws ImageRepositoryException {
        int firstImageId = ID_OF_THE_FIRST_SAVED_IMAGE;
        int lastSavedId = this.fillWithTheDefaultImage(this.manager, DEFAULT_CACHE_SIZE, firstImageId);

        int idOfImageToBeSaved = lastSavedId + 1;
        Bitmap imageToBeSaved = DEFAULT_IMAGE;

        doThrow(new ImageRepositoryException()).when(this.managedRepository).save(idOfImageToBeSaved, imageToBeSaved);
        try {
            this.manager.save(idOfImageToBeSaved, imageToBeSaved);
        } catch (ImageRepositoryException e) {}

        verify(this.managedRepository, never()).delete(firstImageId);
    }

    @Test
    public void theLastFetchedImageIsTheLastOneToBeEvicted() throws ImageRepositoryException {
        // Given
        int firstImageId = ID_OF_THE_FIRST_SAVED_IMAGE;
        int numberOfSavedImages = DEFAULT_CACHE_SIZE;

        int lastSavedId = this.fillWithTheDefaultImage(this.manager, numberOfSavedImages, firstImageId);

        int fetchedImageId = firstImageId;
        this.manager.fetch(fetchedImageId);
        
        // When-Then
        // evict all the images from the repository - 1
        lastSavedId = this.fillWithTheDefaultImage(this.manager, numberOfSavedImages - 1, lastSavedId + 1);
        verify(this.managedRepository, never()).delete(firstImageId);

        // now the fetched image should be the next to be evicted
        this.manager.save(lastSavedId + 1, DEFAULT_IMAGE);
        verify(this.managedRepository).delete(fetchedImageId);
    }

    @Test
    public void aFailedFetchMustNotChangeTheEvictingOrder() throws ImageRepositoryException {
        int firstImageId = ID_OF_THE_FIRST_SAVED_IMAGE;
        int numberOfSavedImages = DEFAULT_CACHE_SIZE;

        int lastSavedId = this.fillWithTheDefaultImage(this.manager, numberOfSavedImages, firstImageId);

        int fetchedImageId = firstImageId;
        when(this.managedRepository.fetch(fetchedImageId)).thenThrow(new ImageRepositoryException());

        try {
            this.manager.fetch(fetchedImageId);
        } catch (ImageRepositoryException e) {}

        // now the image whose fetch failed should be the next one to be evicted
        verify(this.managedRepository, never()).delete(fetchedImageId);

        this.manager.save(lastSavedId + 1, DEFAULT_IMAGE);
        verify(this.managedRepository).delete(fetchedImageId);
    }

    @Test
    public void itShouldIgnoreTheFailedDeletionOfEvictedImages() throws ImageRepositoryException {
            // this will avoid possible infinite loop problems.
        int firstImageId = ID_OF_THE_FIRST_SAVED_IMAGE;
        int numberOfSavedImages = DEFAULT_CACHE_SIZE;

        int lastSavedId = this.fillWithTheDefaultImage(this.manager, numberOfSavedImages, firstImageId);

        int nextImageToBeEvicted = firstImageId;
        doThrow(new ImageRepositoryException()).when(this.managedRepository).delete(nextImageToBeEvicted);

        this.manager.save(lastSavedId + 1, DEFAULT_IMAGE);

        // no exceptions should be thrown here
    }

    @Test
    public void fetchingANonexistentImageShouldNotProduceAnyEviction() throws ImageRepositoryException {
        this.fillWithTheDefaultImage(this.manager, DEFAULT_CACHE_SIZE, ID_OF_THE_FIRST_SAVED_IMAGE);

        Bitmap fetchedImage = this.manager.fetch(NOT_USED_IMAGE_ID);

        assertThat(fetchedImage, is(nullValue()));  // there is no image for that id
        verify(this.managedRepository, never()).delete(anyInt());
    }

    @Test
    public void theDeletedImagesAreNotEvictedLater() throws ImageRepositoryException {
        // Given
        int firstImageId = ID_OF_THE_FIRST_SAVED_IMAGE;
        int numberOfImagesToBeSaved = DEFAULT_CACHE_SIZE;

        int lastSavedId = this.fillWithTheDefaultImage(this.manager, numberOfImagesToBeSaved, firstImageId);

        this.manager.delete(firstImageId);
        reset(this.managedRepository);  // to forget that the file has been deleted once

        // When
        // this will make the manager evict all the already added images
        this.fillWithTheDefaultImage(this.manager, numberOfImagesToBeSaved, lastSavedId + 1);

        // Then
        // but the deleted one must not be deleted again.
        verify(this.managedRepository, never()).delete(firstImageId);
    }

    @Test
    public void theImagesWhoseDeleteFailedAreEvictedLater() throws ImageRepositoryException {
        // Given
        int firstImageId = ID_OF_THE_FIRST_SAVED_IMAGE;
        int numberOfImagesToBeSaved = DEFAULT_CACHE_SIZE;

        int lastSavedId = this.fillWithTheDefaultImage(this.manager, numberOfImagesToBeSaved, firstImageId);

        int idToBeDeleted = firstImageId;
        doThrow(new ImageRepositoryException()).when(this.managedRepository).delete(idToBeDeleted);

        try {
            this.manager.delete(idToBeDeleted);
        } catch (ImageRepositoryException e) {}

        reset(this.managedRepository);  // to forget that the file has been deleted once
        doNothing().when(this.managedRepository).delete(idToBeDeleted);  // it won't throw the exception anymore

        // When
        // this will make the manager evict all the saved images
        this.fillWithTheDefaultImage(this.manager, numberOfImagesToBeSaved, lastSavedId + 1);

        // Then
        // the one whose delete failed must be deleted again.
        verify(this.managedRepository).delete(firstImageId);
    }

    /* Fetch */

    @Test
    public void theFetchedImagesAreFetchedFromTheManagedRepository() throws ImageRepositoryException {
        this.manager.fetch(NOT_USED_IMAGE_ID);
        verify(this.managedRepository).fetch(NOT_USED_IMAGE_ID);
    }

    @Test(expected=ImageRepositoryException.class)
    public void exceptionsMustNotBeCaughtByTheLruWhenFetching() throws ImageRepositoryException {
        this.malfunctioningManager.fetch(NOT_USED_IMAGE_ID);
    }

    /* Delete */

    @Test
    public void theDeletedImagesAreDeletedFromTheManagedRepository() throws ImageRepositoryException {
        int imageId = NOT_USED_IMAGE_ID;
        this.manager.save(imageId, DEFAULT_IMAGE);

        this.manager.delete(imageId);
        
        verify(this.managedRepository).delete(imageId);
    }

    @Test(expected=ImageRepositoryException.class)
    public void exceptionsMustNotBeCaughtByTheLruWhenDeleting() throws ImageRepositoryException {
        this.malfunctioningManager.delete(NOT_USED_IMAGE_ID);
    }

    /* Saved Images */

    @Test
    public void theCollectionOfSavedImagesIsFetchedNoMatterTheLRUPolicy() throws ImageRepositoryException {
        reset(this.managedRepository);  // dismiss initial loading of entries into the LRU

        Collection<Integer> returnedCollection = new ArrayList<Integer>();
        when(this.managedRepository.savedImages()).thenReturn(returnedCollection);

        assertThat(this.manager.savedImages(), sameInstance(returnedCollection));
        verify(this.managedRepository).savedImages();
    }

    @Test(expected=ImageRepositoryException.class)
    public void exceptionsMustNotBeCaughtByTheLruWhenGettingTheCollectionOfSavedImages()
            throws ImageRepositoryException {
        // this is not in setUp method because it causes other tests to break when trying to change the behaviour of
        // this.malfunctioningRepository.savedImages().
        when(this.malfunctioningRepository.savedImages()).thenThrow(new ImageRepositoryException());

        this.malfunctioningManager.savedImages();
    }

    /* Previously saved images */

    @Test
    public void spareImagesMustBeEvictedAfterInstantiatingAnLRUWithLessKeptImagesThanTheExistingAmount()
            throws ImageRepositoryException {
        int numberOfPreviouslySavedImages = 8;
        Collection<Integer> previouslySavedImages = this.collectionOfIds(numberOfPreviouslySavedImages);

        ImageRepository managedRepository = mock(ImageRepository.class);
        when(managedRepository.savedImages()).thenReturn(previouslySavedImages);

        int numberOfImagesInLRU = numberOfPreviouslySavedImages - 3;  // less than numberOfPreviouslySavedImages
        new LruRepositoryManager(managedRepository, numberOfImagesInLRU);

        verify(managedRepository, times(numberOfPreviouslySavedImages - numberOfImagesInLRU)).delete(anyInt());
    }

    @Test
    public void theConstructionOfAnLRUOnAMalfunctioningRepositoryWithSpareImagesMustNotBreak()
            throws ImageRepositoryException {
        int numberOfPreviouslySavedImages = 8;
        Collection<Integer> previouslySavedImages = this.collectionOfIds(numberOfPreviouslySavedImages);

        ImageRepository managedRepository = this.malfunctioningRepository;
        when(managedRepository.savedImages()).thenReturn(previouslySavedImages);

        doThrow(new ImageRepositoryException()).when(managedRepository).delete(anyInt());

        int numberOfImagesInLRU = numberOfPreviouslySavedImages - 3;  // less than numberOfPreviouslySavedImages
        new LruRepositoryManager(managedRepository, numberOfImagesInLRU);
    }

    @Test
    public void aPreviouslySavedImageMustBeEvictedAfterSavingANewImage() throws ImageRepositoryException {
        int numberOfPreviouslySavedImages = 8;
        Collection<Integer> previouslySavedImages = this.collectionOfIds(numberOfPreviouslySavedImages);

        ImageRepository managedRepository = mock(ImageRepository.class);
        when(managedRepository.savedImages()).thenReturn(previouslySavedImages);

        ImageRepository manager = new LruRepositoryManager(managedRepository, numberOfPreviouslySavedImages);
        manager.save(NOT_USED_IMAGE_ID, DEFAULT_IMAGE);

        verify(managedRepository).delete(anyInt());
    }

    @Test
    public void savingOverAnAlreadySavedImageIdMustNotEvictAnyImages() throws ImageRepositoryException {
        int numberOfPreviouslySavedImages = 8;
        Collection<Integer> previouslySavedImages = this.collectionOfIds(numberOfPreviouslySavedImages);

        ImageRepository managedRepository = mock(ImageRepository.class);
        when(managedRepository.savedImages()).thenReturn(previouslySavedImages);

        ImageRepository manager = new LruRepositoryManager(managedRepository, numberOfPreviouslySavedImages);
        manager.save(previouslySavedImages.iterator().next(), DEFAULT_IMAGE);

        verify(managedRepository, never()).delete(anyInt());
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

            this.manager.save(imageId, imageToBeSaved);
            idOfTheLastSavedImage = imageId;
        }

        return idOfTheLastSavedImage;
    }

    private Collection<Integer> collectionOfIds(int numberOfElements) {
        Collection<Integer> collection = new ArrayList<Integer>();

        for (int i = 0; i < numberOfElements; ++i) {
            collection.add(ID_OF_THE_FIRST_SAVED_IMAGE + i);
        }

        return collection;
    }
}
