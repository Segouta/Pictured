package com.example.christian.pictured;

/*
 * By Christian Bijvoets, Minor Programmeren UvA, January 2018.
 * This class manages the camera and saves the image on the right place.
 */

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import java.io.File;

public class CameraManager {

    // Filesnames and locations.
    private File imgFile;
    private File storageDir;
    private final String IMAGE_FILE_NAME = "SnapThatCurrent.jpg";
    private Activity parent;

    public CameraManager(Activity parent ) {
        // Setup the cameramanager.
        this.parent = parent;

        storageDir = parent.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        imgFile = new  File(storageDir + "/" + IMAGE_FILE_NAME);
    }


    public Bitmap getThumbnail() {
        // Return the image from the storage. If it does not exist, return default image.
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            return myBitmap;
        }
        return BitmapFactory.decodeResource(parent.getResources(), R.drawable.new_icon);
    }


    public String getCameraFilePath() {
        // Getting the path to the picture.
        return imgFile.getAbsolutePath();
    }

    public Uri getImageFileURI() {
        // Getting the path as an URI.
        Uri photoURI = FileProvider.getUriForFile(parent,
                "com.example.android.fileprovider",
                imgFile);
        return photoURI;
    }

}
