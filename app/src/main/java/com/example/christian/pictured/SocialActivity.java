package com.example.christian.pictured;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import static android.view.Gravity.BOTTOM;

public class SocialActivity extends FragmentActivity implements View.OnClickListener {

    private ImageView back, share, info;
    private ViewPager pager;

    private String streakToDisplay;

    private static final int NUM_PAGES = 2;
    static final int MAX_HISTORY_LENGTH = 20;

    private PagerAdapter pagerAdapter;
    private DatabaseReference mDatabase;

    private TextView snapStreakView, gamesAmountView, lastGameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);

        back = findViewById(R.id.backButton);
        share = findViewById(R.id.shareButton);
        info = findViewById(R.id.infoButton);
        snapStreakView = findViewById(R.id.snapStreak);
        gamesAmountView = findViewById(R.id.gameAmountText);
        lastGameView = findViewById(R.id.lastGameScore);

        getWindow().setEnterTransition(new Slide(BOTTOM));

        mDatabase = FirebaseDatabase.getInstance().getReference();

        SocialServerManager mySocialServerManager = new SocialServerManager(this, mDatabase);

        // Instantiate a ViewPager and a PagerAdapter.
        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);

        back.setOnClickListener(this);
        info.setOnClickListener(this);
        share.setOnClickListener(this);
    }

    // onResume callback, used to make the nav bar and status bar disappear
    protected void onResume() {
        super.onResume();
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public void onBackPressed() {
        if (pager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            pager.setCurrentItem(pager.getCurrentItem() - 1);
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new FragmentA();
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    public void setData(Integer gamesAmount, Long scoreTime, ArrayList<Long> lastGames) {
        float scoreTimeSeconds = ((float) scoreTime)/1000;
        lastGameView.setText(String.valueOf(scoreTimeSeconds));
        gamesAmountView.setText(String.valueOf(gamesAmount));
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
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "My current SnapStreak is " + streakToDisplay);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void showGameInfo() {
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
