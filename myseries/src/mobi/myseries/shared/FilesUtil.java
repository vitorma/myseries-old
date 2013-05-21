package mobi.myseries.shared;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

public class FilesUtil {

    public static void copy(File sourceFile, File destinationFile) throws IOException {
        FileInputStream sourceInputStream = null;
        FileChannel sourceChannel = null;
        FileOutputStream destinationOutputStream = null;
        FileChannel destinationChannel = null;

        try {
            sourceInputStream = new FileInputStream(sourceFile);
            sourceChannel = sourceInputStream.getChannel();
            destinationOutputStream = new FileOutputStream(destinationFile);
            destinationChannel = destinationOutputStream.getChannel();
            long size = sourceChannel.size();
            sourceChannel.transferTo(0, size, destinationChannel);
        } finally {
            if (sourceInputStream != null) {
                sourceInputStream.close();
            }
            if (sourceChannel != null) {
                sourceChannel.close();
            }
            if (destinationOutputStream != null) {
                destinationOutputStream.close();
            }
            if (destinationChannel != null) {
                destinationChannel.close();
            }
        }
    }

    public static void writeFile(InputStream inputStream, File file) throws IOException {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    public static File ensuredDirectory(String path) {
        File directory = new File(path);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        return directory;
    }
}