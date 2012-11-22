package mobi.myseries.domain.repository.image;

import java.util.Collection;

import android.graphics.Bitmap;

public interface ImageRepository {
    public void save(int id, Bitmap image);
    public Bitmap fetch(int id);
    public void delete(int id);
    public Collection<Integer> savedImages();
}
