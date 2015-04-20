package com.tplab.romupdater;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * @author Rahul Ranjan on 19-04-2015.
 */
public class Utils {

    public static final String INTERNAL_ROM_PATH = "/cache/update.zip";

    // supported file formats
    public static final List<String> FILE_EXTN = Arrays.asList("zip");

    /**
     * Method to read file paths from sdcard & usb drive
     */
    public ArrayList<String> getFilePaths(Context context, String path) {
        ArrayList<String> filePaths = new ArrayList<String>();
        File directory = new File(path);
        if (directory.isDirectory()) {
            File[] listFiles = directory.listFiles();
            if (listFiles!=null && listFiles.length > 0) {
                for (int i = 0; i < listFiles.length; i++) {
                    String filePath = listFiles[i].getAbsolutePath();
                    if (isSupportedFile(filePath)) {
                        filePaths.add(filePath);
                    }
                }
            }
        }
        return filePaths;
    }

    /**
     * Method to check supported file extension
     *
     * @param filePath
     * @return
     */
    private boolean isSupportedFile(String filePath) {
        String ext = filePath.substring((filePath.lastIndexOf(".") + 1),
                filePath.length());
        if (FILE_EXTN.contains(ext.toLowerCase(Locale
                .getDefault())))
            return true;
        else
            return false;
    }

    public void copy(File src) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(INTERNAL_ROM_PATH);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
}
