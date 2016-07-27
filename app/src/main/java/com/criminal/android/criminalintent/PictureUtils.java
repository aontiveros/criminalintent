package com.criminal.android.criminalintent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

/**
 * Created by aontivero on 7/26/2016.
 */
public class PictureUtils {

    /**
     * Get the bitmap from the respective file based on the activity size.
     * Normally we can't always be sure of the screen real-state available to us, so
     * we must go based on the activity size.
     * @param path The path to the image
     * @param activity The activity hosting the image to scaled the image by
     * @return The bitmap containing the scaled image based on the activity size
     */
    public static Bitmap getScaledBitmap(String path, Activity activity){
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);

        return getScaledBitmap(path, size.x, size.y);
    }

    /**
     * Get a bitmap image based on the respective file for the following width and height dimensions
     * @param path The location of the image
     * @param destWidth The required width for the image to take
     * @param destHeight The required height for the image to take
     * @return A bitmap containing the scaled image for the respective size.
     */
    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight){
        //Read the dimiension of file from disk
        BitmapFactory.Options options = new BitmapFactory.Options();
        //Just decode bounds. Don't load the full image jsut yet
        options.inJustDecodeBounds = true;
        //get the actual size of the image for now.
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        //Figure out how much to actually scale down by
        int inSampleSize = 1;
        if(srcHeight > destHeight || srcWidth > srcWidth){
            if(srcHeight > destHeight)
                inSampleSize = Math.round(srcHeight / destHeight);
            else
                inSampleSize = Math.round(srcWidth / destWidth);
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        //Read in the scaled down bitmap with the dimensions calculated
        return BitmapFactory.decodeFile(path, options);

    }

}
