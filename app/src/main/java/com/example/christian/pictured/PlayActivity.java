package com.example.christian.pictured;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static android.view.Gravity.TOP;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    DatabaseReference mDatabase;

    private TextView thingText, tagText, descText;

    private Button openCameraButton, processButton;
    private ImageView snapImage, back;
    private ProgressDialog progressDialog;

    private CameraManager myCameraManager;
    private MSVisionManager myMSVisionManager;
    private ServerManager myServerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        getWindow().setEnterTransition(new Slide(TOP));

//        processButton = findViewById(R.id.processButton);
//        openCameraButton = findViewById(R.id.openCameraButton);

        thingText = findViewById(R.id.thingText);
//        tagText = findViewById(R.id.tagText);
//        descText = findViewById(R.id.descText);

        snapImage = findViewById(R.id.snapImage);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        back = findViewById(R.id.backButton);
        back.setOnClickListener(this);

        myCameraManager = new CameraManager(this);
        myServerManager = new ServerManager(this, mDatabase);
        myMSVisionManager = new MSVisionManager(this, myCameraManager);

//        processButton.setOnClickListener(this);
//        openCameraButton.setOnClickListener(this);

        setLastImageThumbnail();
    }

    protected void onResume() {
        super.onResume();
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setLastImageThumbnail();

    }

    private void setLastImageThumbnail() {
        snapImage.setImageBitmap(getResizedBitmap(myCameraManager.getThumbnail(), 200));
    }

    public void visionCheckDone(String description, ArrayList<String> tags) {
//        TextView descText = findViewById(R.id.descText);
//        TextView tagText = findViewById(R.id.tagText);
//        descText.setText(description);
//        tagText.setText(tags.toString());
        progressDialog.dismiss();
    }

    public void newThingArrived() {
        thingText.setText(myServerManager.getThingText());
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, myCameraManager.getImageFileURI());
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.equals(processButton)) {
            progressDialog = new ProgressDialog(PlayActivity.this);
            progressDialog.setMessage("Analyzing dat snap...");
            progressDialog.show();
            myMSVisionManager.startVisionCheck();
        }
        else if(v.equals(openCameraButton)) {
            dispatchTakePictureIntent();
        }
        else if (v.equals(back)) {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.back_click));
            this.onBackPressed();
        }
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}

