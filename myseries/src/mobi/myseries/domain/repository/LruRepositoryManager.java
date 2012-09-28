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

package mobi.myseries.domain.repository;

import mobi.myseries.shared.Validate;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class LruRepositoryManager implements ImageStorage {

    private class ImagesQueue extends LruCache<Integer, Integer> {
    	// TODO(gabriel) put these declarations at the place where the RepositoryManager is going to be instantiated.
        private static final int KiB = 1024;
        private static final int MiB = 1024 * KiB;
        private static final int EPISODE_IMAGE_AVERAGE_SIZE = 14 * KiB;
        private static final int CACHE_SIZE = 1 * MiB;
        private static final int NUMBER_OF_CACHE_ENTRIES = CACHE_SIZE / EPISODE_IMAGE_AVERAGE_SIZE;

        public ImagesQueue(int numberOfKeptImages) {
            super(numberOfKeptImages);
        }

        @Override
        protected void entryRemoved(boolean evicted, Integer key, Integer oldValue, Integer newValue) {
            if (evicted) {
            	LruRepositoryManager.this.delete(key);
            }
        }
    }

	ImagesQueue imagesQueue;
	ImageStorage managedRepository;

    public LruRepositoryManager(ImageStorage managedRepository, int numberOfKeptImages) {
        Validate.isNonNull(managedRepository, "managedRepository");
        Validate.isTrue(numberOfKeptImages > 0,
                new IllegalArgumentException("numberOfKeptImages should be greater than zero"));

        this.managedRepository = managedRepository;
        this.imagesQueue = new ImagesQueue(numberOfKeptImages);
    }

    @Override
    public void save(int id, Bitmap image) {
        this.managedRepository.save(id, image);
        this.imagesQueue.put(id, id);
    }

    @Override
    public Bitmap fetch(int id) {
    	Bitmap fetchedImage = this.managedRepository.fetch(id);

    	if (fetchedImage != null) {
    		this.imagesQueue.put(id, id);
    	}

    	return fetchedImage;
    }

    @Override
    public void delete(int id) {
    	this.managedRepository.delete(id);
    	this.imagesQueue.remove(id);
    }

}
