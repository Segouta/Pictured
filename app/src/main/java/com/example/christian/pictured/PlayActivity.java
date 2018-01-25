package com.example.christian.pictured;

import android.app.ProgressDialog;
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
import java.util.Random;

import cn.iwgang.countdownview.CountdownView;

import static android.view.Gravity.TOP;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    DatabaseReference mDatabase;

    private TextView thingText, tagText, descText, snapTime;
    private static PlayActivity sInstance;
    private Button processButton;
    private ImageView snapImage, back, stripes, badge, light, openCameraButton;
    private RelativeLayout boxLayout, box;
    private ConstraintLayout cameraButtonLayout, timerLayout;
    private ProgressDialog progressDialog;

    private CameraManager myCameraManager;
    private MSVisionManager myMSVisionManager;
    private UserServerManager myUserServerManager;


    FirebaseAuth mAuth;
    String thing;

    Animation zoomin, fadein, click_exit, grow, slow_show, fly_in, rotate_right, fade, rotate_camera, snap_show;

    CountdownView acceptTimer, playTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sInstance = this;
        setContentView(R.layout.activity_play);

        // TODO: hier moet ik dus dat woord ophalen... hoe?
//        setLayout();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            }
        }

        getWindow().setEnterTransition(new Slide(TOP));

        acceptTimer = findViewById(R.id.countdownView);
        playTimer = findViewById(R.id.countupView);



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
        snapTime = findViewById(R.id.snapTime);

        click_exit = AnimationUtils.loadAnimation(this, R.anim.click_box_exit);
        zoomin = AnimationUtils.loadAnimation(this, R.anim.zoomin);
        fadein = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        grow = AnimationUtils.loadAnimation(this, R.anim.cloud_grow);
        slow_show = AnimationUtils.loadAnimation(this, R.anim.slow_show);
        fly_in = AnimationUtils.loadAnimation(this, R.anim.fly_in);
        rotate_right = AnimationUtils.loadAnimation(this, R.anim.rotate_right);
        fade = AnimationUtils.loadAnimation(this, R.anim.fade);
        rotate_camera = AnimationUtils.loadAnimation(this, R.anim.rotate_camera);
        snap_show = AnimationUtils.loadAnimation(this, R.anim.snap_show);

        stripes.setAnimation(rotate_right);
        badge.setAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_left));
        box.setAnimation(AnimationUtils.loadAnimation(this, R.anim.zoomin));
        light.setAnimation(fadein);

        snapImage = findViewById(R.id.snapImage);
        snapImage.setVisibility(View.INVISIBLE);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();

        back = findViewById(R.id.backButton);
        back.setOnClickListener(this);

        myCameraManager = new CameraManager(this);
        myUserServerManager = new UserServerManager(this, mDatabase);
        myMSVisionManager = new MSVisionManager(this, myCameraManager);

        startService(new Intent(PlayActivity.this, ServerManager.class));

//        processButton.setOnClickListener(this);
        openCameraButton.setOnClickListener(this);
        box.setOnClickListener(this);

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

    public static PlayActivity getInstance(){
        return sInstance;
    }

    private void setVisibilities(boolean cam, boolean thing, boolean box, boolean white, boolean playCounter, boolean image) {
        cameraButtonLayout.setVisibility((cam ? View.VISIBLE : View.INVISIBLE));
        thingText.setVisibility((thing ? View.VISIBLE : View.INVISIBLE));
        boxLayout.setVisibility((box ? View.VISIBLE : View.INVISIBLE));
        light.setVisibility((white ? View.VISIBLE : View.INVISIBLE));
        playTimer.setVisibility((playCounter ? View.VISIBLE : View.INVISIBLE));
        snapImage.setVisibility((image ? View.VISIBLE : View.INVISIBLE));
    }

    public void setLayout(String layoutState) {
        if (layoutState.equals("unopened")) {
            setVisibilities(false, false, true, false, false, false);
        } else if (layoutState.equals("opened")) {
            setVisibilities(true, true, false, false, true, false);
        } else if (layoutState.equals("attempted")) {
            setVisibilities(true, true, false, false, true, true);
        } else if (layoutState.equals("found")) {
            setVisibilities(false, true, false, false, true, true);
        } else if (layoutState.equals("expired")) {
            setVisibilities(false, true, false, false, false, false);
        } else if (layoutState.equals("expired_found")) {
            setVisibilities(false, true, false, false, false, false);
        }
    }

    private void storeLayout(String toStore) {
//        mDatabase.child("users").child(mAuth.getUid()).child("layout").setValue(toStore);
    }

    private void openBox() {
        storeLayout("opened");
        acceptTimer.stop();
        makeBoxInvisible();

        thingText.setText(thing);
        showThingText();
        openCameraButton.setAnimation(rotate_camera);
        cameraButtonLayout.setAnimation(fly_in);
        cameraButtonLayout.setVisibility(View.VISIBLE);
        timerLayout.setAnimation(fade);
        timerLayout.setVisibility(View.VISIBLE);
//        playTimer.start(60*1000);
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
        storeLayout("expired");
        makeBoxInvisible();
        thingText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
        String[] messagesArray = this.getResources().getStringArray(R.array.wait_messages);
        thingText.setText(messagesArray[new Random().nextInt(messagesArray.length)]);
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

    public void visionCheckDone(String description, ArrayList<String> tags) {
//        TextView descText = findViewById(R.id.descText);
//        TextView tagText = findViewById(R.id.tagText);
//        descText.setText(description);
//        tagText.setText(tags.toString());
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
//            Long scoreTime = startTime - playTimer.getRemainTime();
//            playTimer.updateShow(scoreTime);


            openCameraButton.clearAnimation();
            cameraButtonLayout.clearAnimation();
            cameraButtonLayout.setVisibility(View.INVISIBLE);
            toaster("goedzo");
            thingText.setTextColor(getResources().getColor(R.color.found));
            snapImage.setColorFilter(getResources().getColor(R.color.found_trans));

            snapTime.setText("Snapped in: ");
            System.out.println("-----------------------------");
//            System.out.println(scoreTime);

//            TODO:WTFFFFF de tijd wil niet displayen stilstaand. doen...

//
//            playTimer.stop();

        } else {
            storeLayout("attempted");
            toaster("sukkel");
            thingText.setTextColor(getResources().getColor(R.color.failed));
            snapImage.setColorFilter(getResources().getColor(R.color.failed_trans));
            playTimer.start(playTimer.getRemainTime());
        }
    }

    public void toaster(String message) {
        // toasts string
        Toast.makeText(PlayActivity.this, message, Toast.LENGTH_LONG).show();
    }

    public void getThing(){

    }

    public void newThingArrived(ServerManager me) {
        //TODO: send notification when not running
        storeLayout("unopened");
//        thing = me.getThingText();

        thingText.setText("laptop");
        thingText.setVisibility(View.VISIBLE);
       // if (playTimer.getRemainTime() > 0) {
            playTimer.start(me.getEndMillis() - new Date().getTime());
       // }
        acceptTimer.start(me.getEndMillis() - new Date().getTime());
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
        else if (v.equals(snapImage)) {

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

