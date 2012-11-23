package mobi.myseries.domain.repository.image;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import mobi.myseries.shared.Validate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;

public class ExternalStorageImageDirectory implements ImageRepository {
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");
    private static final CompressFormat IMAGE_FORMAT = CompressFormat.JPEG;
    private static final String IMAGE_EXTENSION = "." + IMAGE_FORMAT.toString().toLowerCase();
    private static final int COMPRESS_QUALITY = 85;
    private static final Pattern IMAGE_FILE_REGEX_PATTERN = Pattern.compile("^-?\\d+\\" + IMAGE_EXTENSION + "$");
      // The "\\" just before IMAGE_EXTENSION is there because the first character of IMAGE_EXTENSION is a "."
      // which is a meta character in regular expressions.

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

    @Override
    public Collection<Integer> savedImages() {
        String[] files = this.imageFolder().list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                File file = new File(dir, filename);
                return (IMAGE_FILE_REGEX_PATTERN.matcher(filename).matches() &&
                        file.isFile());
            }
        });

        List<Integer> fileNumbers = new ArrayList<Integer>();
        for (String s : files) {
            String numericalPart = s.replace(IMAGE_EXTENSION, "");
            fileNumbers.add(Integer.valueOf(numericalPart));
        }

        return fileNumbers;
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
            throw new ImageDirectoryIoException("create", path, e);
        } catch (IOException e) {
            throw new ImageDirectoryIoException("write/read on", path, e);
        }

        return directory;
    }

    private void saveImageFile(Bitmap image, File file) {
        try {
            FileOutputStream os = new FileOutputStream(file);
            image.compress(IMAGE_FORMAT, COMPRESS_QUALITY, os);
            os.close();
        } catch (IOException e) {
            throw new ImageFileIoException("create", file.toString(), e);
        }
    }

    private boolean isExternalStorageAvaliable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static class ImageFileIoException extends ImageRepositoryException {
        private static final long serialVersionUID = 1L;
        private static final String CAUSE = "Can't %s image file %s";

        public ImageFileIoException() {
            super();
        }

        public ImageFileIoException(String action, String filename) {
            super(String.format(CAUSE, action, filename));
        }

        public ImageFileIoException(String action, String filename, Throwable throwable) {
            super(String.format(CAUSE, action, filename), throwable);
        }

        public ImageFileIoException(Throwable throwable) {
            super(throwable);
        }
    }

    public static class ImageDirectoryIoException extends ImageRepositoryException {
        private static final long serialVersionUID = 1L;
        private static final String CAUSE = "Can't %s directory %s";

        public ImageDirectoryIoException() {
            super();
        }

        public ImageDirectoryIoException(String action, String filename) {
            super(String.format(CAUSE, action, filename));
        }

        public ImageDirectoryIoException(String action, String filename, Throwable throwable) {
            super(String.format(CAUSE, action, filename), throwable);
        }

        public ImageDirectoryIoException(Throwable throwable) {
            super(throwable);
        }
    }
}