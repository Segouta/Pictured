package com.example.christian.pictured;

/*
 * By Christian Bijvoets, Minor Programmeren UvA, January 2018.
 * This class handles the creation of a valid input for the image-tagging service.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class MSVisionManager  {

    private CameraManager camera;
    private PlayActivity parent;

    public MSVisionManager(PlayActivity parent, CameraManager camera) {
        this.camera = camera;
        this.parent = parent;
    }

    public void startVisionCheck() {
        // Get image from storage.
        Bitmap imageFromCamera = BitmapFactory.decodeFile(camera.getCameraFilePath());

        // Convert it to an output stream, compress it a little, convert it to ByteArrayInputStream.
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imageFromCamera.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
        final ByteArrayInputStream imageFromCameraStream = new ByteArrayInputStream(outputStream.toByteArray());

        // Start async task that handles the image tagging api.
        AsyncTask<InputStream, String, String> visionTask = new VisionTask(parent);
        visionTask.execute(imageFromCameraStream);
    }
}