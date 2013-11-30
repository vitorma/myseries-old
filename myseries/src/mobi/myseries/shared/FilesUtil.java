package mobi.myseries.shared;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class FilesUtil {

    public static void copy(File sourceFile, File destinationFile)
            throws IOException {
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

    public static void writeFile(InputStream inputStream, File file)
            throws IOException {
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

    public static void writeStringToFile(String fileName, String data)
            throws IOException {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(fileName));

        } finally {
            if (out != null) {
                out.write(data);
                out.close();
            }
        }

    }

    public static String readFileAsString(String fileName, String charsetName)
            throws java.io.IOException {
        java.io.InputStream is = new java.io.FileInputStream(fileName);
        try {
            final int bufsize = 4096;
            int available = is.available();
            byte[] data = new byte[available < bufsize ? bufsize : available];
            int used = 0;
            while (true) {
                if (data.length - used < bufsize) {
                    byte[] newData = new byte[data.length << 1];
                    System.arraycopy(data, 0, newData, 0, used);
                    data = newData;
                }
                int got = is.read(data, used, data.length - used);
                if (got <= 0)
                    break;
                used += got;
            }
            return charsetName != null ? new String(data, 0, used, charsetName)
                    : new String(data, 0, used);
        } finally {
            is.close();
        }
    }

    public static File ensuredDirectory(String path) {
        File directory = new File(path);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        return directory;
    }

    public static String[] listFilesOfDirectory(File path, final String fileEndsWith) {
        List<String> fileNames = new ArrayList<String>();
        if (path.exists()) {
            FilenameFilter filter = new FilenameFilter() {
                public boolean accept(File dir, String filename) {
                    File sel = new File(dir, filename);
                    if (!sel.canRead())
                        return false;
                    boolean endsWith = fileEndsWith != null ? filename
                            .toLowerCase().endsWith(fileEndsWith) : true;
                    return endsWith;

                }
            };
            String[] fileNameList = path.list(filter);
            for (String file : fileNameList) {
                fileNames.add(file);
            }
        }
        return (String[]) fileNames.toArray(new String[] {});
    }

}