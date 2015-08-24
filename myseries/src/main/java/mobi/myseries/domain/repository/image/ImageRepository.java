package mobi.myseries.domain.repository.image;

import java.util.Collection;

import android.graphics.Bitmap;

public interface ImageRepository {

    public void save(long l, Bitmap image) throws ImageRepositoryException;

    public String fetch(long l) throws ImageRepositoryException;

    public void delete(long l) throws ImageRepositoryException;

    public Collection<Long> savedImages() throws ImageRepositoryException;

    public void clear() throws ImageRepositoryException;
}
