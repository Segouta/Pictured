package com.example.christian.pictured;

/*
 * Created by Christian on 17-1-2018.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.View;

import java.io.File;
import java.io.IOException;


public class CameraManager {


    private File imgFile;

    private File storageDir;
    private final String IMAGE_FILE_NAME = "SnapThatCurrent.jpg";
    private Activity parent;
    public CameraManager(Activity parent )
    {
        this.parent = parent;

        storageDir = parent.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        imgFile = new  File(storageDir + "/" + IMAGE_FILE_NAME);
    }

    public Bitmap getThumbnail() {
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            return myBitmap;
        }
        return BitmapFactory.decodeResource(parent.getResources(), R.drawable.icon);
    }

    public String getCameraFilePath()
    {
        return imgFile.getAbsolutePath();
    }

    public Uri getImageFileURI() {
        Uri photoURI = FileProvider.getUriForFile(parent,
                "com.example.android.fileprovider",
                imgFile);
        return photoURI;
    }

}
