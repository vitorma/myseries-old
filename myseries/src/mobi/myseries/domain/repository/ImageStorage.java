package mobi.myseries.domain.repository;

import android.graphics.Bitmap;

// TODO(gabriel) should this class use another type as id instead of int?
public interface ImageStorage {
    public void save(int id, Bitmap image);
    public Bitmap fetch(int id);
    public void delete(int id);
}
