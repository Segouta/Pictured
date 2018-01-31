package com.example.christian.pictured;

/*
 * By Christian Bijvoets, Minor Programmeren UvA, January 2018.
 * This is the social activity of SnapThat. From here, you can share your SnapStreak, and you can
 * see how you are doing.
 */

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Locale;

import static android.view.Gravity.BOTTOM;

public class SocialActivity extends FragmentActivity implements View.OnClickListener, WifiCheckInterface {

    // Setup global variables.
    static final int MAX_HISTORY_LENGTH = 10;

    // Initialize views.
    private ImageView back, share, info;
    private TextView snapStreakView, gamesAmountView, lastGameView;

    // Setup connection losing interface.
    public static MainActivity delegate = null;

    // Setup variables.
    private String streakToDisplay;

    // Setup database.
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);

        // Setup connection interface.
        MainActivity.delegate = this;

        // Set views.
        back = findViewById(R.id.backButton);
        share = findViewById(R.id.shareButton);
        info = findViewById(R.id.infoButton);
        snapStreakView = findViewById(R.id.snapStreak);
        gamesAmountView = findViewById(R.id.gameAmountText);
        lastGameView = findViewById(R.id.lastGameScore);

        // Setup transition style.
        getWindow().setEnterTransition(new Slide(BOTTOM));

        // Setup Firebase.
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Setup socialservermanager to get data from Firebase.
        SocialServerManager mySocialServerManager = new SocialServerManager(this, mDatabase);

        // Set OnClickListeners.
        back.setOnClickListener(this);
        info.setOnClickListener(this);
        share.setOnClickListener(this);
    }

    @Override
    public void closeActivity() {
        // This function is called from main activity when internet connection is lost.
        finish();
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
    }

    public void setData(Integer gamesAmount, Long scoreTime, ArrayList<Long> lastGames) {
        // When data needed for this activity is loaded, this method fires.
        // First calculate scoreTime in seconds and set in textviews.
        float scoreTimeSeconds = ((float) scoreTime)/1000;
        lastGameView.setText(String.valueOf(scoreTimeSeconds));
        gamesAmountView.setText(String.valueOf(gamesAmount));

        // This for loop calculates the average and displays this SnapStreak.
        long sum = 0;
        int amount = 0;
        for(long i : lastGames) {
            amount += 1;
            sum += i;
        }
        streakToDisplay = String.format(Locale.US, "%.3f", (float) ((double) sum / (double) amount) / 1000);
        snapStreakView.setText(streakToDisplay);
    }

    private void shareSnaps() {
        // Share your SnapStreak with the world via various social media.
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey, btw my current SnapStreak is " + streakToDisplay +
                "!" + " I doubt you can beat me! Download SnapThat, and let us start the battle of the Snaps!");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void showGameInfo() {
        // When clicking on information, show this information to the user.
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Explanation")
                .setMessage("Your SnapStreak is the average score in seconds of the last " +
                        MAX_HISTORY_LENGTH + " games you played. Keeping this number low " +
                        "requires quickness, but also " + "consistency! Share this if you " +
                        "think you're doing a good job!")
                .setIcon(R.drawable.info)
                .show();
    }

    @Override
    public void onClick(View v) {
        // Start animation when clicking on a clickable item, and do what the button needs to do.
        v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.back_click));
        if (v.equals(back)) {
            this.onBackPressed();
        } else if (v.equals(share)) {
            shareSnaps();
        } else if (v.equals(info)) {
            showGameInfo();
        }
    }
}
