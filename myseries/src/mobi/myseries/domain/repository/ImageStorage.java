package mobi.myseries.domain.repository;

import java.util.Collection;

import android.graphics.Bitmap;

// TODO(gabriel) should this class use another type as id instead of int?
// TODO(gabriel) rename this class to ImageRepository, after removing the coupling with that interface
public interface ImageStorage {
    public void save(int id, Bitmap image);
    public Bitmap fetch(int id);
    public void delete(int id);
    public Collection<Integer> savedImages();
}
