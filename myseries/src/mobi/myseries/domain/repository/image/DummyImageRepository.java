package mobi.myseries.domain.repository.image;

import java.util.Arrays;
import java.util.Collection;

import android.graphics.Bitmap;

public class DummyImageRepository implements ImageRepository {

    @Override
    public Collection<Long> savedImages() throws ImageRepositoryException {
        return Arrays.asList();
    }

    @Override
    public void save(long l, Bitmap image) throws ImageRepositoryException {}

    @Override
    public Bitmap fetchFromCache(long id) throws ImageRepositoryException {
        return null;
    }

    @Override
    public Bitmap fetch(long l) throws ImageRepositoryException {
        return null;
    }

    @Override
    public void delete(long l) throws ImageRepositoryException {}
}