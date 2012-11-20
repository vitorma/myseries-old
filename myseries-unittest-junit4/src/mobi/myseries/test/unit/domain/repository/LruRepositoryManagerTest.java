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

package mobi.myseries.test.unit.domain.repository;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collection;

import mobi.myseries.domain.repository.ImageStorage;
import mobi.myseries.domain.repository.LruRepositoryManager;

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

    private ImageStorage managedRepository;
    private LruRepositoryManager manager;

    @Before
    public void setUp() {
        this.managedRepository = mock(ImageStorage.class);

        when(this.managedRepository.fetch(intThat(is(not(equalTo(NOT_USED_IMAGE_ID)))))).thenReturn(DEFAULT_IMAGE);
        when(this.managedRepository.fetch(NOT_USED_IMAGE_ID)).thenReturn(null);

        this.manager = new LruRepositoryManager(this.managedRepository, DEFAULT_CACHE_SIZE);
    }
    /* Construction */

    @Test(expected=IllegalArgumentException.class)
    public void constructingItWithANullRepositoryCausesIllegalArgumentException() {
        new LruRepositoryManager(null, DEFAULT_CACHE_SIZE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void constructingItWithZeroImagesCausesIllegalArgumentException() {
        new LruRepositoryManager(mock(ImageStorage.class), 0);
    }

    @Test(expected=IllegalArgumentException.class)
    public void constructingItWithANegativeNumberOfKeptImagesCausesIllegalArgumentException() {
        new LruRepositoryManager(mock(ImageStorage.class), -1);
    }

    /* Saving */

    @Test
    public void anImageSavedOnTheManagerShouldBeForwardedToTheManagedRepository() {
        Bitmap image = DEFAULT_IMAGE;
        int imageId = NOT_USED_IMAGE_ID;

        this.manager.save(imageId, image);

        verify(this.managedRepository).save(imageId, image);
    }

    @Test
    public void anUpdateToASavedImageShouldBeForwardedToTheManagedRepository() {
        Bitmap image = mock(Bitmap.class);
        int imageId = 0;

        this.manager.save(imageId, image);
        this.manager.save(imageId, image);

        verify(this.managedRepository, times(2)).save(imageId, image);
    }

    /* Evicting */

    @Test
    public void noImagesShouldBeEvictedBeforeSavingMoreThanTheNumberOfKeptImages() {
        this.fillWithTheDefaultImage(this.manager, DEFAULT_CACHE_SIZE, ID_OF_THE_FIRST_SAVED_IMAGE);

        verify(this.managedRepository, never()).delete(anyInt());
    }

    @Test
    public void noImagesShouldBeEvictedAfterUpdatingAnAlreadySavedImage() {
        int firstImageId = ID_OF_THE_FIRST_SAVED_IMAGE;
        this.fillWithTheDefaultImage(this.manager, DEFAULT_CACHE_SIZE, firstImageId);

        // update the first image
        this.manager.save(firstImageId, DEFAULT_IMAGE);

        verify(this.managedRepository, never()).delete(anyInt());
    }

    @Test
    public void theOldestImageShouldBeEvictedAfterSavingANewImage() {
        int firstImageId = ID_OF_THE_FIRST_SAVED_IMAGE;
        int lastSavedId = this.fillWithTheDefaultImage(this.manager, DEFAULT_CACHE_SIZE, firstImageId);

        this.manager.save(lastSavedId + 1, DEFAULT_IMAGE);

        verify(this.managedRepository).delete(firstImageId);
    }

    @Test
    public void theLastFetchedImageIsTheLastOneToBeEvicted() {
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
    public void fetchingANonexistentImageShouldNotProduceAnyEviction() {
        this.fillWithTheDefaultImage(this.manager, DEFAULT_CACHE_SIZE, ID_OF_THE_FIRST_SAVED_IMAGE);

        Bitmap fetchedImage = this.manager.fetch(NOT_USED_IMAGE_ID);

        assertThat(fetchedImage, is(nullValue()));  // there is no image for that id
        verify(this.managedRepository, never()).delete(anyInt());
    }
   
    @Test
    public void theDeletedImagesAreNotEvictedLater() {
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

    /* Fetch */

    @Test
    public void theFetchedImagesAreFetchedFromTheManagedRepository() {
        this.manager.fetch(NOT_USED_IMAGE_ID);
        verify(this.managedRepository).fetch(NOT_USED_IMAGE_ID);
    }
    
    /* Delete */

    @Test
    public void theDeletedImagesAreDeletedFromTheManagedRepository() {
        int imageId = NOT_USED_IMAGE_ID;
        this.manager.save(imageId, DEFAULT_IMAGE);

        this.manager.delete(imageId);
        
        verify(this.managedRepository).delete(imageId);
    }

    /* Saved Images */

    @Test
    public void theCollectionOfSavedImagesIsFetchedNoMatterTheLRUPolicy() {
    	Collection<Integer> returnedCollection = new ArrayList<Integer>();
    	when(this.managedRepository.savedImages()).thenReturn(returnedCollection);

    	assertThat(this.manager.savedImages(), sameInstance(returnedCollection));
    	verify(this.managedRepository).savedImages();
    }

    /* Test tools */

    private int fillWithTheDefaultImage(ImageStorage repository, int numberOfImages, int firstImageId) {
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
}
