/*
 *   LruRepositoryManager.java
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

package mobi.myseries.domain.repository.image;

import java.util.Collection;

import mobi.myseries.shared.Validate;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class LruRepositoryManager implements ImageRepository {

    private class ImagesQueue extends LruCache<Long, Long> {

        public ImagesQueue(int numberOfKeptImages) {
            super(numberOfKeptImages);
        }

        @Override
        protected void entryRemoved(boolean evicted, Long key, Long oldValue, Long newValue) {
            if (evicted) {
                try {
                    LruRepositoryManager.this.delete(key);
                } catch (ImageRepositoryException e) {}
            }
        }

        public void put(long id) {
            this.put(id, id);
        }
    }

    ImagesQueue imagesQueue;
    ImageRepository managedRepository;

    public LruRepositoryManager(ImageRepository managedRepository, int numberOfKeptImages) {
        Validate.isNonNull(managedRepository, "managedRepository");
        Validate.isTrue(numberOfKeptImages > 0,
                new IllegalArgumentException("numberOfKeptImages should be greater than zero"));

        this.managedRepository = managedRepository;
        this.imagesQueue = new ImagesQueue(numberOfKeptImages);

        try {
            this.loadPreviouslySavedImages();
        } catch (ImageRepositoryException e) {}  // The images cannot be loaded into the cache there is nothing we can
                                                 // do about it. The cache has to be constructed anyway.
    }

    private void loadPreviouslySavedImages() throws ImageRepositoryException {
        for (long id : this.managedRepository.savedImages()) {
            this.imagesQueue.put(id);
        }
    }

    @Override
    public void save(long id, Bitmap image) throws ImageRepositoryException {
        this.managedRepository.save(id, image);
        this.imagesQueue.put(id);
    }

    @Override
    public Bitmap fetch(long id) throws ImageRepositoryException {
        Bitmap fetchedImage = this.managedRepository.fetch(id);

        if (fetchedImage != null) {
            this.imagesQueue.put(id);
        }

        return fetchedImage;
    }

    @Override
    public void delete(long id) throws ImageRepositoryException {
        this.managedRepository.delete(id);
        this.imagesQueue.remove(id);
    }

    @Override
    public Collection<Long> savedImages() throws ImageRepositoryException {
        return this.managedRepository.savedImages();
    }

    @Override
    public Bitmap fetchFromCache(long id) throws ImageRepositoryException {
        return this.managedRepository.fetchFromCache(id);
    }
}
