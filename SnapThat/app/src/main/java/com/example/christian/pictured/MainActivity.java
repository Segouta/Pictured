package com.example.christian.pictured;

/*
 * By Christian Bijvoets, Minor Programmeren UvA, January 2018.
 * This is SnapThat's main activity. It shows 4 simple buttons that lead to different activities.
 */

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    // Wifi checker setup.
    public static WifiCheckInterface delegate = null;

    // Setups for various objects.
    private DatabaseReference mDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleSignInAccount googleAccount;
    private ConnectivityManager connectivityManager;
    private NetworkRequest.Builder builder;

    // Setup views.
    private ImageView user, play, social, settings;
    private TextView username, playText, socialText;

    // Setup activities.
    private UserActivity userActivity = new UserActivity();

    // Setup variables.
    private boolean isInFront, buttonsUsable;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Change activity background (for animation purposes).
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));

        // Initialize connectivityManager and builder.
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        builder = new NetworkRequest.Builder();

        // Initialize values of variables.
        isInFront = true;
        buttonsUsable = true;

        // Setup notification service (FCM) and listen for logout.
        subscribeToPushService();
        setListener();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        googleAccount = GoogleSignIn.getLastSignedInAccount(this);

        // Find views.
        username = findViewById(R.id.user_title);
        playText = findViewById(R.id.play_title);
        socialText = findViewById(R.id.social_title);
        user = findViewById(R.id.user);
        play = findViewById(R.id.snap);
        social = findViewById(R.id.social);
        settings = findViewById(R.id.settings);

        // Set OnClickListeners.
        user.setOnClickListener(this);
        play.setOnClickListener(this);
        social.setOnClickListener(this);
        settings.setOnClickListener(this);

        // Set Google user first name in user button section.
        if (googleAccount != null) {
            username.setText(googleAccount.getGivenName());
        }

        // Start connection-changing listener and check current status.
        checkConnectionOnce();
        setConnectionListener();
    }

    private void checkConnectionOnce() {
        // This method calls internet status check is and updates clickability.
        if (!isNetworkAvailable()) {
            buttonsUsable(false);
            user.setImageResource((R.drawable.account));
        }
    }

    private boolean isNetworkAvailable() {
        // This method checks internet status.
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void setConnectionListener() {
        // This method sets a internet connection change listener.
        connectivityManager.registerNetworkCallback(
                builder.build(),
                new ConnectivityManager.NetworkCallback() {

                    @Override
                    public void onAvailable(Network network) {
                        // When the network is available again, make buttons clickable and get avatar.
                        new DownLoadImageTask(user).execute(googleAccount.getPhotoUrl().toString());
                        buttonsUsable(true);
                    }

                    @Override
                    public void onLost(Network network) {
                        // Network unavailable, set buttons unclickable, go back to MainActivity from current activity via WifiCheckInterface.
                        buttonsUsable(false);
                        if (!isInFront) {
                            delegate.closeActivity();
                        }
                    }
                }
        );
    }

    private void buttonsUsable(boolean b) {
        // Set alpha (opacity) of buttons to activities that are internet dependent.
        setAlphas(b ? 255 : 100);
        buttonsUsable = b;
    }

    private void setAlphas(int opacity) {
        // Set alpha (opacity) of views.
        play.setImageAlpha(opacity);
        social.setImageAlpha(opacity);
        playText.setTextColor(Color.argb(opacity, 255, 255, 255));
        socialText.setTextColor(Color.argb(opacity, 255, 255, 255));
    }

    private void subscribeToPushService() {
        // Subscribe user to correct Firebase notifications.
        FirebaseMessaging.getInstance().subscribeToTopic("news");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // This code makes the nav bar and status bar disappear.
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        // Save isInFront state.
        isInFront = true;
    }

    @Override
    public void onPause() {
        // Save isInFront state.
        super.onPause();
        isInFront = false;
    }

    private void setListener() {
        // Initialize authentication listener.
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in.
                    toaster("Still logged in");
                    username.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                }
                else {
                    // User is not logged in and will be redirected to login page.
                    toaster("You are logged out.");
                    userActivity.signOut();
                    userActivity.goToLoginActivity();
                }
            }
        };
    }

    public void toaster(String message) {
        // Simple method that simplifies toasting actions.
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
    }

    public void startActivityWithAnimation(View v, Class toStart) {
        // Starts click-animation and starts activity.
        v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.icon_click));
        startActivity(new Intent(MainActivity.this, toStart),
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    @Override
    public void onClick(View v) {
        // When clicking a button, determine if that button is clickable and go to correct activity.
        if (v.equals(user)) {
            startActivityWithAnimation(v, UserActivity.class);
        } else if (v.equals(settings)) {
            startActivityWithAnimation(v, SettingsActivity.class);
        } else if (buttonsUsable){
            if (v.equals(play)) {
                startActivityWithAnimation(v, PlayActivity.class);
            }
            else if (v.equals(social)) {
                startActivityWithAnimation(v, SocialActivity.class);
            }
        } else {
            // If a click was on a greyed out button, toast user.
            toaster("Sorry, there is no internet connection.");
        }
    }
}
