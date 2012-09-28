package mobi.myseries.domain.repository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import mobi.myseries.shared.Validate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;

public class ExternalStorageImageDirectory implements ImageStorage {
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");
    private static final CompressFormat IMAGE_FORMAT = CompressFormat.JPEG;
    private static final String IMAGE_EXTENSION = "." + IMAGE_FORMAT.toString().toLowerCase();
    private static final int COMPRESS_QUALITY = 85;

    private final String directoryName;
    private final Context context;

    public ExternalStorageImageDirectory(Context context, String directoryName) {
        this.context = context;
        this.directoryName = directoryName;
    }

    @Override
    public void save(int id, Bitmap image) {
        Validate.isNonNull(image, "image");

        if (!this.isExternalStorageAvaliable()) {
            return;
        }

        this.saveImageFile(image, this.fileFor(id));
    }

    @Override
    public Bitmap fetch(int id) {
        if (!this.isExternalStorageAvaliable()) {
            return null;
        }

        return BitmapFactory.decodeFile(this.filePathFor(id));
    }

    @Override
    public void delete(int id) {
        this.fileFor(id).delete();
    }

    private File fileFor(int id) {
        return new File(this.imageFolder(), this.fileNameFor(id));
    }

    private String filePathFor(int id) {
        return this.imageFolder() + FILE_SEPARATOR + this.fileNameFor(id);
    }

    private String fileNameFor(int id) {
        return id + IMAGE_EXTENSION;
    }

    private File imageFolder() {
        return ensuredDirectory(this.rootDirectory().getPath() + FILE_SEPARATOR + this.directoryName);
    }

    private File rootDirectory() {
        return this.context.getExternalFilesDir(null);
    }

    private static File ensuredDirectory(String path) {
        File directory = new File(path);

        try {
            if (!directory.exists()) {
                directory.mkdirs();
                File nomedia = new File(directory, ".nomedia");
                nomedia.createNewFile();
            }
        } catch (SecurityException e) {
            throw new RuntimeException("can't create the given directory: " + path);
        } catch (IOException e) {
            throw new RuntimeException("can't write/read the on given directory: " + path);
        }

        return directory;
    }

    private void saveImageFile(Bitmap image, File file) {
        try {
            FileOutputStream os = new FileOutputStream(file);
            image.compress(IMAGE_FORMAT, COMPRESS_QUALITY, os);
            os.close();
        } catch (IOException e) {
            throw new ImageIoException("create", file.toString());
        }
    }

    private boolean isExternalStorageAvaliable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
}
