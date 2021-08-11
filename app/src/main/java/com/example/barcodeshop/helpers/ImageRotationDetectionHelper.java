package com.example.barcodeshop.helpers;

import android.media.ExifInterface;
import android.util.Log;

import java.io.IOException;

public class ImageRotationDetectionHelper {

    public static int getCameraPhotoOrientation(String imageFilePath) {
        int rotate = 0;
        try {
            ExifInterface exif;
            exif = new ExifInterface(imageFilePath);
            String exifOrientation = exif
                    .getAttribute(ExifInterface.TAG_ORIENTATION);
            Log.d("exifOrientation", exifOrientation);
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            Log.d(ImageRotationDetectionHelper.class.getSimpleName(), "orientation :" + orientation);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rotate;
    }
}
