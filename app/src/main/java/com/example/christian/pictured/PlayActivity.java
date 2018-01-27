package com.example.christian.pictured;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.Manifest;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

import cn.iwgang.countdownview.CountdownView;

import static android.view.Gravity.TOP;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    DatabaseReference mDatabase;

    private TextView thingText, tagText, descText, snapTime;
    private Button processButton;
    private ImageView snapImage, back, stripes, badge, light, openCameraButton;
    private RelativeLayout boxLayout, box;
    private ConstraintLayout cameraButtonLayout, timerLayout;
    private ProgressDialog progressDialog;

    private CameraManager myCameraManager;
    private MSVisionManager myMSVisionManager;
    private UserServerManager myUserServerManager;
    private ServerManager myServerManager;

    private FirebaseAuth mAuth;
    private String description, thing, layout;

    private Animation fade_in, click_exit, grow, slow_show, fly_in, rotate_right, fade, rotate_camera, snap_show, badge_rotation, box_movement;

    private CountdownView acceptTimer, playTimer;

    String[] messagesArray, messagesArrayPositive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        // TODO: hier moet ik dus dat woord ophalen... hoe?
//        setLayout();
        initAnimations();
        initViews();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            }
        }

        messagesArray = this.getResources().getStringArray(R.array.wait_messages);
        messagesArrayPositive = this.getResources().getStringArray(R.array.wait_messages_found);

        getWindow().setEnterTransition(new Slide(TOP));

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();

        back.setOnClickListener(this);

        myCameraManager = new CameraManager(this);
        myServerManager = new ServerManager(this, mDatabase);
        myUserServerManager = new UserServerManager(this, mDatabase);
        myMSVisionManager = new MSVisionManager(this, myCameraManager);

        openCameraButton.setOnClickListener(this);
        box.setOnClickListener(this);
        snapImage.setOnClickListener(this);

        setLastImageThumbnail();

        acceptTimer.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
            @Override
            public void onEnd(CountdownView cv) {
                openingTimeExpired();
            }
        });
        playTimer.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
            @Override
            public void onEnd(CountdownView cv) {
                playTimeExpired();
            }
        });
    }

    private void initViews() {
        box = findViewById(R.id.boxTimerLayout);
        boxLayout = findViewById(R.id.boxLayout);
        light = findViewById(R.id.light);
        stripes = findViewById(R.id.loading);
        badge = findViewById(R.id.badge);
        snapTime = findViewById(R.id.snapTime);
        back = findViewById(R.id.backButton);
        snapImage = findViewById(R.id.snapImage);
        cameraButtonLayout = findViewById(R.id.cameraButtonLayout);
        timerLayout = findViewById(R.id.timerLayout);
        acceptTimer = findViewById(R.id.countdownView);
        playTimer = findViewById(R.id.countupView);
        thingText = findViewById(R.id.thingText);
        openCameraButton = findViewById(R.id.openCameraButton);
    }

    private void initAnimations() {
        click_exit = AnimationUtils.loadAnimation(this, R.anim.click_box_exit);
        fade_in = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        grow = AnimationUtils.loadAnimation(this, R.anim.cloud_grow);
        slow_show = AnimationUtils.loadAnimation(this, R.anim.slow_show);
        fly_in = AnimationUtils.loadAnimation(this, R.anim.fly_in);
        rotate_right = AnimationUtils.loadAnimation(this, R.anim.rotate_right);
        fade = AnimationUtils.loadAnimation(this, R.anim.fade);
        rotate_camera = AnimationUtils.loadAnimation(this, R.anim.rotate_camera);
        snap_show = AnimationUtils.loadAnimation(this, R.anim.snap_show);
        badge_rotation = AnimationUtils.loadAnimation(this, R.anim.rotate_left);
        box_movement = AnimationUtils.loadAnimation(this, R.anim.zoomin);
    }

    private void setVisibilities(boolean cam, boolean thing, boolean box, boolean white, boolean playCounter, boolean image) {
        cameraButtonLayout.setVisibility((cam ? View.VISIBLE : View.INVISIBLE));
        thingText.setVisibility((thing ? View.VISIBLE : View.INVISIBLE));
        boxLayout.setVisibility((box ? View.VISIBLE : View.INVISIBLE));
        light.setVisibility((white ? View.VISIBLE : View.INVISIBLE));
        playTimer.setVisibility((playCounter ? View.VISIBLE : View.INVISIBLE));
        snapTime.setVisibility((playCounter ? View.VISIBLE : View.INVISIBLE));
        snapImage.setVisibility((image ? View.VISIBLE : View.INVISIBLE));
    }

    public void compareMillis(Long userEndMillis, Long endMillis) {
        if (!Objects.equals(userEndMillis, endMillis)) {
            storeLayout("unopened");
            mDatabase.child("users").child(mAuth.getUid()).child("lastGame").setValue(endMillis);
            setLayout("unopened");
        }
    }

    public void setLayout(String layoutState) {
//        toaster(layoutState);
        layout = layoutState;
        thingText.setTextColor(getResources().getColor(R.color.neutral));
        switch(layoutState) {
            case "unopened":
                setVisibilities(false, false, true, false, false, false);
                stripes.setAnimation(rotate_right);
                badge.setAnimation(badge_rotation);
                box.setAnimation(box_movement);
                light.setAnimation(fade_in);
                if (acceptTimer.getRemainTime() == 0) {
                    storeLayout("expired");
                    setLayout("expired");
                }
                break;
            case "opened":
                setVisibilities(true, true, false, false, true, false);
                openCameraButton.setAnimation(rotate_camera);
                cameraButtonLayout.setAnimation(fly_in);
                timerLayout.setAnimation(fade);
                break;
            case "attempted":
                setVisibilities(true, true, false, false, true, true);
                thingText.setTextColor(getResources().getColor(R.color.failed));
                snapImage.setColorFilter(getResources().getColor(R.color.failed_trans));
                playTimer.start(playTimer.getRemainTime());
                break;
            case "found":
                setVisibilities(false, true, false, false, true, true);
                //            Long scoreTime = startTime - playTimer.getRemainTime();
//            playTimer.updateShow(scoreTime);
                openCameraButton.clearAnimation();
                cameraButtonLayout.clearAnimation();
                cameraButtonLayout.setVisibility(View.INVISIBLE);
                thingText.setTextColor(getResources().getColor(R.color.found));
                snapImage.setColorFilter(getResources().getColor(R.color.found_trans));

                snapTime.setText("Snapped in: ");
                break;
            case "expired":
                setVisibilities(false, true, false, false, false, false);
                thingText.setText(messagesArray[new Random().nextInt(messagesArray.length)]);
                thingText.setTextColor(getResources().getColor(R.color.neutral));
                thingText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
                light.setVisibility(View.INVISIBLE);
                break;
            case "expired_found":
                setVisibilities(false, true, false, false, false, false);
                thingText.setText(messagesArrayPositive[new Random().nextInt(messagesArrayPositive.length)]);
                thingText.setTextColor(getResources().getColor(R.color.neutral));
                thingText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
                light.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private void storeLayout(String toStore) {
        mDatabase.child("users").child(mAuth.getUid()).child("layout").setValue(toStore);
    }

    private void openBox() {
        storeLayout("opened");
        setLayout("opened");
//        acceptTimer.stop();
        makeBoxInvisible();

        thingText.setText(thing);
        showThingText();

//        playTimer.start(60*1000);
    }

    public void setThing(String thingFromFirebase, Long endMillis) {
        thing = thingFromFirebase;
        thingText.setText(thing);
        playTimer.start(endMillis - new Date().getTime());
        acceptTimer.start(endMillis - new Date().getTime());
//        toaster(thing + endMillis.toString());
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

    private void openingTimeExpired() {
        if (layout.equals("found")) {
            toaster("expired maar wel gevonden");
        } else {
            toaster("expired en niet gevonden");
        }
        storeLayout("expired");
        setLayout("expired");
        makeBoxInvisible();
        showThingText();
    }

    private void playTimeExpired() {
        storeLayout("expired");
        toaster("play time over bitch");
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
        snapImage.setImageBitmap(getResizedBitmap(myCameraManager.getThumbnail(), 250));
    }

    public void visionCheckDone(String desc, ArrayList<String> tags) {
        toaster("Our best guess: " + desc + ".");
        description = desc;
        progressDialog.dismiss();
        snapImage.setVisibility(View.VISIBLE);
        snapImage.setAnimation(snap_show);
        compareVisionAndObject(tags);
    }

    public void compareVisionAndObject(ArrayList<String> tags) {
        if (tags.contains(thing)) {
            mDatabase.child("users").child(mAuth.getUid()).child("history").setValue(1);
            mDatabase.child("users").child(mAuth.getUid()).child("gamesAmount").setValue(1);
            storeLayout("found");
            setLayout("found");
            toaster("Yeah, that is a " + thing + "!");
        } else {
            storeLayout("attempted");
            setLayout("attempted");
            toaster("We did not find a " + thing + " in this image... Try again!");
        }
    }

    public void toaster(String message) {
        // toasts string
        Toast.makeText(PlayActivity.this, message, Toast.LENGTH_LONG).show();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, myCameraManager.getImageFileURI());
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {

            playTimer.pause();

            progressDialog = new ProgressDialog(PlayActivity.this);
            progressDialog.setMessage("Analyzing dat snap...");
            progressDialog.show();
            myMSVisionManager.startVisionCheck();

            setLastImageThumbnail();
        }
    }

    @Override
    public void onClick(View v) {
        if(v.equals(box)) {
            openBox();
        }
        else if(v.equals(openCameraButton)) {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.camera_click));
            dispatchTakePictureIntent();
        }
        else if (v.equals(back)) {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.back_click));
            this.onBackPressed();
        }
        else if (v.equals(snapImage)) {
            if (description != null) {
                toaster("Our best guess: " + description + ".");
            }
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
