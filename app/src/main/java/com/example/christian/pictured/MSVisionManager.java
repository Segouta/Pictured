package com.example.christian.pictured;

/*
 * Created by Christian on 17-1-2018.
 */

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.AnalysisResult;
import com.microsoft.projectoxford.vision.contract.Caption;

import org.json.JSONException;
import org.json.JSONObject;

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
        Bitmap imageFromCamera = BitmapFactory.decodeFile(camera.getCameraFilePath());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imageFromCamera.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
        final ByteArrayInputStream imageFromCameraStream = new ByteArrayInputStream(outputStream.toByteArray());

        AsyncTask<InputStream, String, String> visionTask = new VisionTask(parent);
        visionTask.execute(imageFromCameraStream);
    }
}