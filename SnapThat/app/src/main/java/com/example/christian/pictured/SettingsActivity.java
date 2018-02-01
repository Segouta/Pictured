package com.example.christian.pictured;

/*
 * By Christian Bijvoets, Minor Programmeren UvA, January 2018.
 * This is the settings activity. Is currently only contains a about note.
 */

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import static android.view.Gravity.BOTTOM;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Setup views.
        back = findViewById(R.id.backButton);

        // Setup OnClickListeners.
        back.setOnClickListener(this);

        // Set transition animation.
        getWindow().setEnterTransition(new Slide(BOTTOM));
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

    @Override
    public void onClick(View v) {
        // When something is clicked, make it animate and do what the button has to do.
        v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.back_click));
        if (v.equals(back)) {
            this.onBackPressed();
        }
    }
}
