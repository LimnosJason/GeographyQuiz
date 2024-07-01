package com.example.geographyquiz;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

public class ImageUtils {
    private static final String TAG = "TEST";

    //String constant for the prefix that will be used for image storing
    private static final String IMAGE_PREFIX = "pic_";

    /**
     * Utility method to copy the selected image from the gallery to the application files
     * @param uri The uri where the original image is located (gallery)
     * @param context The Application context
     * @return The new path where the image is stored
     */
    public static String copyImage(Uri uri, Context context) {
        Log.d(TAG, "copyImage: copy started..");
        String fileName = IMAGE_PREFIX + new Date().getTime();
        String path = context.getFilesDir().getAbsolutePath() + File.separator + fileName;
        File destFile = new File(path);
        try {
            destFile.createNewFile();
            InputStream inStream = context.getContentResolver().openInputStream(uri);
            OutputStream out = new FileOutputStream(destFile);
            byte[] buf = new byte[1024];
            int len;
            while ((len = inStream.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            inStream.close();
            return path;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "copyImage: Exception:" + e.getMessage());
            return null;
        }
    }

    /**
     * Utility method to delete an image
     * @param imagePath The path where the image is located
     * @param context The Application Context
     * @return (boolean) true if the delete action is successful
     */
    public static boolean deleteImage(String imagePath, Context context) {
        Log.d(TAG, "deleteImage: delete started.. ");
        if (imagePath != null) {
            File f = new File(imagePath);
            return context.deleteFile(f.getName());
        } else return false;
    }
}