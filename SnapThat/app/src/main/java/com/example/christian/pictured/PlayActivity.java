package com.example.christian.pictured;

/*
 * By Christian Bijvoets, Minor Programmeren UvA, January 2018.
 * This is SnapThat's PlayActivity. It contains all game elements and handles basically the whole game.
 */

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

public class PlayActivity extends AppCompatActivity implements View.OnClickListener, WifiCheckInterface {

    // Create global constants and interfaces.
    public static MainActivity delegate = null;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int MAX_HISTORY_LENGTH = 10;

    // Firebase setup.
    private DatabaseReference mDatabase, userDatabase;
    private FirebaseAuth mAuth;

    // Create views.
    private TextView thingText, snapTime;
    private ImageView snapImage, back, stripes, badge, light, openCameraButton;
    private RelativeLayout boxLayout, box;
    private ConstraintLayout cameraButtonLayout, timerLayout;
    private ProgressDialog progressDialog;
    private CountdownView acceptTimer, playTimer;

    // Create animations.
    private Animation fade_in, click_exit, grow, slow_show, fly_in, rotate_right, fade, rotate_camera, snap_show, badge_rotation, box_movement;

    // Create classes.
    private CameraManager myCameraManager;
    private MSVisionManager myMSVisionManager;
    private UserServerManager myUserServerManager;
    private ServerManager myServerManager;

    // Create variables.
    private String description, thing, layout;
    private Long openingTime, pausedTime, scoreTime;
    private ArrayList<Long> lastGames;
    private Integer gamesAmount;
    private String[] messagesArray, messagesArrayPositive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        // Internet connection triggers closing of all activities except MainActivity.
        MainActivity.delegate = this;

        // Set transition style.
        getWindow().setEnterTransition(new Slide(TOP));

        // Setup all animations and views.
        initAnimations();
        initViews();

        // Ask for permissions.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            }
        }

        // Setup message arrays from xml resources.
        messagesArray = this.getResources().getStringArray(R.array.wait_messages);
        messagesArrayPositive = this.getResources().getStringArray(R.array.wait_messages_found);

        // Setup database references and authentication.
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        userDatabase = mDatabase.child("users").child(mAuth.getUid());

        // Setup classes and run the code in them.
        myCameraManager = new CameraManager(this);
        myServerManager = new ServerManager(this, mDatabase);
        myUserServerManager = new UserServerManager(this, mDatabase);
        myMSVisionManager = new MSVisionManager(this, myCameraManager);

        // Set OnClickListeners.
        back.setOnClickListener(this);
        openCameraButton.setOnClickListener(this);
        box.setOnClickListener(this);
        snapImage.setOnClickListener(this);

        // Retrieve last image from storage to show in certain layout versions of this activity.
        setLastImageThumbnail();

        // Set CountdownListener for acceptTimer.
        acceptTimer.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
            @Override
            public void onEnd(CountdownView cv) {
                openingTimeExpired();
            }
        });
    }

    @Override
    public void closeActivity() {
        // When the internet connection is lost, finish this activity.
        finish();
    }

    private void initViews() {
        // Initialize all views.
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
        setVisibilities(false, false, false, false, false, false);
    }

    private void initAnimations() {
        // Initialize all animations.
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
        // This method sets the visibilities for various views.
        cameraButtonLayout.setVisibility((cam ? View.VISIBLE : View.INVISIBLE));
        thingText.setVisibility((thing ? View.VISIBLE : View.INVISIBLE));
        boxLayout.setVisibility((box ? View.VISIBLE : View.INVISIBLE));
        light.setVisibility((white ? View.VISIBLE : View.INVISIBLE));
        playTimer.setVisibility((playCounter ? View.VISIBLE : View.INVISIBLE));
        snapTime.setVisibility((playCounter ? View.VISIBLE : View.INVISIBLE));
        snapImage.setVisibility((image ? View.VISIBLE : View.INVISIBLE));
        if (!white) {
            light.clearAnimation();
        }
    }

    public void setTimeState(Long openingTimeFromDB, Long scoreTimeFromDB, Integer gamesAmountFromDB, ArrayList<Long> lastGamesList) {
        // This method is called from server class. Sets all times from the Firebase.
        lastGames = lastGamesList;
        gamesAmount = gamesAmountFromDB;
        scoreTime = scoreTimeFromDB;
        openingTime = openingTimeFromDB;
        if (scoreTime > 0){
            // This means the present was already opened.
            playTimer.stop();
            playTimer.updateShow(scoreTime);
        }
    }

    public void setLayout(String layoutState) {
        /*
         * This method sets the correct layout stored in Firebase to prevent users from switching device and
         * for example submitting a picture again. This method contains all sorts of setTexts, setColors,
         * setAnimations and setVisibilities.
         */

        // Store layout state to use in different methods.
        layout = layoutState;
        thingText.setTextColor(getResources().getColor(R.color.neutral));
        switch(layoutState) {
            case "unopened":
                setVisibilities(false, false, true, true, false, false);
                userDatabase.child("gameData").child("openingTime").setValue(0);
                userDatabase.child("gameData").child("scoreTime").setValue(0);
                stripes.setAnimation(rotate_right);
                badge.setAnimation(badge_rotation);
                box.setAnimation(box_movement);
                light.setAnimation(fade_in);
                break;
            case "opened":
                setVisibilities(true, true, false, false, true, false);
                openCameraButton.setAnimation(rotate_camera);
                cameraButtonLayout.setAnimation(fly_in);
                timerLayout.setAnimation(fade);
                break;
            case "attempted":
                setVisibilities(true, true, false, false, true, true);
                openCameraButton.setAnimation(rotate_camera);
                cameraButtonLayout.setAnimation(fly_in);
                timerLayout.setAnimation(fade);
                thingText.setTextColor(getResources().getColor(R.color.failed));
                snapImage.setColorFilter(getResources().getColor(R.color.failed_trans));
                if (pausedTime != null) {
                    playTimer.start(playTimer.getRemainTime() - new Date().getTime() + pausedTime);
                }
                break;
            case "found":
                setVisibilities(false, true, false, false, true, true);
                openCameraButton.clearAnimation();
                cameraButtonLayout.clearAnimation();
                cameraButtonLayout.setVisibility(View.INVISIBLE);
                timerLayout.setAnimation(fade);
                thingText.setTextColor(getResources().getColor(R.color.found));
                snapImage.setColorFilter(getResources().getColor(R.color.found_trans));
                playTimer.stop();
                playTimer.updateShow(scoreTime);
                snapTime.setText("Snapped in: ");
                break;
            case "expired":
                setVisibilities(false, true, false, false, false, false);
                cameraButtonLayout.clearAnimation();
                thingText.setText(messagesArray[new Random().nextInt(messagesArray.length)]);
                thingText.setTextColor(getResources().getColor(R.color.neutral));
                thingText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
                break;
            case "expired_found":
                setVisibilities(false, true, false, false, false, false);
                thingText.setText(messagesArrayPositive[new Random().nextInt(messagesArrayPositive.length)]);
                thingText.setTextColor(getResources().getColor(R.color.neutral));
                thingText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
                break;
        }
    }

    public void storeLayout(String toStore) {
        // Store the new layout in the Firebase database.
        userDatabase.child("gameData").child("layout").setValue(toStore);
    }

    private void openBox() {
        // Open the box, show animations to make it visual attractive.
        // Set and store current layout.
        storeLayout("opened");
        setLayout("opened");

        // Save openingTime in Firebase.
        openingTime = new Date().getTime();
        userDatabase.child("gameData").child("openingTime").setValue(openingTime);

        // Hide box in a fancy way with animations.
        makeBoxInvisible();

        // Get the thing, and set the text.
        thingText.setText(thing);
        showThingText();
    }

    public void setThing(String thingFromFirebase, Long endMillis) {
        // Sets the thing retrieved from Firebase, and starts the timers.
        thing = thingFromFirebase;
        thingText.setText(thing);
        playTimer.start(endMillis - new Date().getTime());
        acceptTimer.start(endMillis - new Date().getTime());
    }

    private void showThingText() {
        // Fancy way to show the new thing to find.
        thingText.setVisibility(View.VISIBLE);
        thingText.setAnimation(slow_show);
    }

    private void makeBoxInvisible() {
        // Hides the box in a fancy way, and removes onclicklistener.
        box.setOnClickListener(null);
        light.setAnimation(grow);
        light.setVisibility(View.INVISIBLE);
        boxLayout.setAnimation(click_exit);
        boxLayout.setVisibility(View.INVISIBLE);
    }

    private void openingTimeExpired() {
        // When the openingtime expires, change layout accordingly to current state of layout.
        if(layout.equals("unopened")){
            makeBoxInvisible();
        }
        if (layout.equals("found")) {
            toaster("Time's up! Good job, you did it!");
            storeLayout("expired_found");
            setLayout("expired_found");
        } else {
            toaster("time expired, you did not find the thing!");
            storeLayout("expired");
            setLayout("expired");
        }
        showThingText();
    }

    protected void onResume() {
        // This code makes the nav bar and status bar disappear.
        super.onResume();
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setLastImageThumbnail();

        // Also, make sure the camerabutton still rotates when resuming the activity.
        openCameraButton.setAnimation(rotate_camera);
    }

    private void setLastImageThumbnail() {
        // Get last image from storage and set a thumbnail version of it.
        snapImage.setImageBitmap(getResizedBitmap(myCameraManager.getThumbnail(), 250));
    }

    public void visionCheckDone(String desc, ArrayList<String> tags) {
        // When the async task of the visioncheck is done, this code will run and set variables and animations.
        description = desc;
        progressDialog.dismiss();
        snapImage.setVisibility(View.VISIBLE);
        snapImage.setAnimation(snap_show);

        // Check if the thing is found, if the tags contain the thing the user looks for.
        compareVisionAndObject(tags);
    }

    public void compareVisionAndObject(ArrayList<String> tags) {
        // This method compares the results from vision with the currently asked thing.
        // If it is in the list, the thing is found.
        if (tags.contains(thing)) {
            // Save scoreTime (current time - opening box time).
            scoreTime = new Date().getTime() - openingTime;

            // Add the scoreTime to the recent games list.
            addToLastGames(scoreTime);

            // Save all game info to Firebase.
            userDatabase.child("gamesAmount").setValue(gamesAmount + 1);
            userDatabase.child("gameData").child("thingFoundTime").setValue(new Date().getTime());
            userDatabase.child("gameData").child("scoreTime").setValue(scoreTime);
            mDatabase.child("currentScores").child(scoreTime.toString()).setValue(mAuth.getUid());

            // Store and save new layout.
            storeLayout("found");
            setLayout("found");

            // Toast user that the object was found.
            toaster("Yeah, that is: \"" + thing + "\"");

        } else {
            // When the object is not found, store and set layout state.
            storeLayout("attempted");
            setLayout("attempted");

            // Toast user that the object was not found in this image.
            toaster("We did not find \"" + thing + "\" in this image... Try again!");
        }
    }

    private void addToLastGames(long scoreTime) {
        // Adds the scoreTime to the list of last 10 games in Firebase of this user.
        // When only one 0 was in the list (default), replace it with new score.
        if (lastGames.size() == 1 && lastGames.get(0) == 0) {
            lastGames.set(0, scoreTime);
        }
        // If the list is longer than 10 elements, add new thing and remove last, so update list.
        else if (lastGames.size() > MAX_HISTORY_LENGTH - 1) {
            lastGames.add(scoreTime);
            lastGames = new ArrayList<Long>(lastGames.subList(lastGames.size() - MAX_HISTORY_LENGTH, lastGames.size()));
        }
        // If the list is not too long and neither empty, just add the new score.
        else {
            lastGames.add(scoreTime);
        }
        userDatabase.child("lastGames").setValue(lastGames);
    }

    public void toaster(String message) {
        // Simple method that simplifies toasting actions.
        Toast.makeText(PlayActivity.this, message, Toast.LENGTH_LONG).show();
    }

    private void dispatchTakePictureIntent() {
        // Starts camera app, with no possibility to store image in gallery.
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, myCameraManager.getImageFileURI());
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // When returning from the camera, this method is called.
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            // Pause the timer and get time when pause started.
            playTimer.pause();
            pausedTime = new Date().getTime();

            // Show waiting dialog.
            progressDialog = new ProgressDialog(PlayActivity.this);
            progressDialog.setMessage("Analyzing your snap...");
            progressDialog.show();

            // Start the vision check.
            myMSVisionManager.startVisionCheck();

            // Set new thumbnail.
            setLastImageThumbnail();
        }
    }

    @Override
    public void onClick(View v) {
        // When an object is clicked, this method is called. It starts the correct activity or intent.
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
            // When clicking the thumbnail, the description will be shown if it's analyzed before.
            if (description != null) {
                toaster("Our best guess: " + description + ".");
            }
        }
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        // Returns a resized bitmap.
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

