package mobi.myseries.domain.repository.image;

import java.util.Collection;

import android.graphics.Bitmap;

public interface ImageRepository {

    public void save(int id, Bitmap image) throws ImageRepositoryException;

    public Bitmap fetch(int id) throws ImageRepositoryException;

    public void delete(int id) throws ImageRepositoryException;

    public Collection<Integer> savedImages() throws ImageRepositoryException;

    public Bitmap fetchFromCache(int id) throws ImageRepositoryException;
}
