package com.example.christian.pictured;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Random;

import cn.iwgang.countdownview.CountdownView;

import static android.view.Gravity.TOP;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    DatabaseReference mDatabase;

    private TextView thingText, tagText, descText;

    private Button processButton;
    private ImageView snapImage, back, stripes, badge, light, openCameraButton;
    private RelativeLayout boxLayout, box;
    private ConstraintLayout cameraButtonLayout, timerLayout;
    private ProgressDialog progressDialog;

    private CameraManager myCameraManager;
    private MSVisionManager myMSVisionManager;
    private ServerManager myServerManager;

    Animation zoomin, fadein, click_exit, grow, slow_show, fly_in, rotate_right, fade, rotate_camera;

    CountdownView acceptTimer, playTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        getWindow().setEnterTransition(new Slide(TOP));

        acceptTimer = findViewById(R.id.countdownView);
        playTimer = findViewById(R.id.countupView);

        acceptTimer.start(3*1000);

//        processButton = findViewById(R.id.processButton);
        openCameraButton = findViewById(R.id.openCameraButton);

        thingText = findViewById(R.id.thingText);

        thingText.setVisibility(View.INVISIBLE);
//        tagText = findViewById(R.id.tagText);
//        descText = findViewById(R.id.descText);

        cameraButtonLayout = findViewById(R.id.cameraButtonLayout);
        timerLayout = findViewById(R.id.timerLayout);

        timerLayout.setVisibility(View.INVISIBLE);
        cameraButtonLayout.setVisibility(View.INVISIBLE);

        box = findViewById(R.id.boxTimerLayout);
        boxLayout = findViewById(R.id.boxLayout);
        light = findViewById(R.id.light);
        stripes = findViewById(R.id.loading);
        badge = findViewById(R.id.badge);

        click_exit = AnimationUtils.loadAnimation(this, R.anim.click_box_exit);
        zoomin = AnimationUtils.loadAnimation(this, R.anim.zoomin);
        fadein = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        grow = AnimationUtils.loadAnimation(this, R.anim.cloud_grow);
        slow_show = AnimationUtils.loadAnimation(this, R.anim.slow_show);
        fly_in = AnimationUtils.loadAnimation(this, R.anim.fly_in);
        rotate_right = AnimationUtils.loadAnimation(this, R.anim.rotate_right);
        fade = AnimationUtils.loadAnimation(this, R.anim.fade);
        rotate_camera = AnimationUtils.loadAnimation(this, R.anim.rotate_camera);

        stripes.setAnimation(rotate_right);
        badge.setAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_left));
        box.setAnimation(AnimationUtils.loadAnimation(this, R.anim.zoomin));
        light.setAnimation(fadein);

        //TODO: animatie van counter ook werkend...
//        timer.startAnimation(zoomout);

//        snapImage = findViewById(R.id.snapImage);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        back = findViewById(R.id.backButton);
        back.setOnClickListener(this);

        myCameraManager = new CameraManager(this);
        myServerManager = new ServerManager(this, mDatabase);
        myMSVisionManager = new MSVisionManager(this, myCameraManager);

//        processButton.setOnClickListener(this);
        openCameraButton.setOnClickListener(this);
        box.setOnClickListener(this);

        setLastImageThumbnail();

        acceptTimer.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
            @Override
            public void onEnd(CountdownView cv) {
                timeExpired();
            }
        });
    }

    private void openBox() {
        makeBoxInvisible();
        acceptTimer.stop();
        thingText.setText(myServerManager.getThingText());
        showThingText();
        openCameraButton.setAnimation(rotate_camera);
        cameraButtonLayout.setAnimation(fly_in);
        cameraButtonLayout.setVisibility(View.VISIBLE);
        timerLayout.setAnimation(fade);
        timerLayout.setVisibility(View.VISIBLE);
        playTimer.start(25*60*1000);
    }

    private void showThingText() {
        thingText.setVisibility(View.VISIBLE);
        thingText.setAnimation(slow_show);
    }

    private void makeBoxInvisible() {
        box.setOnClickListener(null);
        light.setAnimation(grow);
        light.setVisibility(View.INVISIBLE);
        boxLayout.setAnimation(click_exit);
        boxLayout.setVisibility(View.INVISIBLE);
    }

    private void timeExpired() {
        makeBoxInvisible();
        thingText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
        String[] messagesArray = this.getResources().getStringArray(R.array.wait_messages);
        thingText.setText(messagesArray[new Random().nextInt(messagesArray.length)]);
        showThingText();
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
        openCameraButton.setAnimation(rotate_camera);

    }

    private void setLastImageThumbnail() {
//        snapImage.setImageBitmap(getResizedBitmap(myCameraManager.getThumbnail(), 200));
    }

    public void visionCheckDone(String description, ArrayList<String> tags) {
//        TextView descText = findViewById(R.id.descText);
//        TextView tagText = findViewById(R.id.tagText);
//        descText.setText(description);
//        tagText.setText(tags.toString());
        progressDialog.dismiss();
    }

    public void getThing(){

    }

    public void newThingArrived() {
//        thingText.setText(myServerManager.getThingText());
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, myCameraManager.getImageFileURI());
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void onActivityResult() {

    }

    @Override
    public void onClick(View v) {
        if(v.equals(box)) {
            openBox();
        }
        else if(v.equals(processButton)) {
            progressDialog = new ProgressDialog(PlayActivity.this);
            progressDialog.setMessage("Analyzing dat snap...");
            progressDialog.show();
            myMSVisionManager.startVisionCheck();
        }
        else if(v.equals(openCameraButton)) {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.camera_click));
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

