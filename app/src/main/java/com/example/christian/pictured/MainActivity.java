package com.example.christian.pictured;

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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

// Key 1: 364da92137ce4d0d99397ae2b2c5a29b
// Key 2: fa76fa5671624f87b26cc8e3f61148a7
// Endpoint: https://westcentralus.api.cognitive.microsoft.com/vision/v1.0

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static WifiCheckInterface delegate = null;

    private DatabaseReference mDatabase;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleSignInAccount googleAccount;
    private ConnectivityManager connectivityManager;
    private NetworkRequest.Builder builder;

    private ImageView user, play, social, settings;
    private TextView username, playText, socialText;

    private UserActivity userActivity = new UserActivity();

    private boolean isInFront, buttonsUsable;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        builder = new NetworkRequest.Builder();

        isInFront = true;
        buttonsUsable = true;

        subscribeToPushService();
        setListener();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        username = findViewById(R.id.user_title);
        playText = findViewById(R.id.play_title);
        socialText = findViewById(R.id.social_title);

        user = findViewById(R.id.user);
        play = findViewById(R.id.snap);
        social = findViewById(R.id.social);
        settings = findViewById(R.id.settings);

        user.setOnClickListener(this);
        play.setOnClickListener(this);
        social.setOnClickListener(this);
        settings.setOnClickListener(this);

        googleAccount = GoogleSignIn.getLastSignedInAccount(this);

        if (googleAccount != null) {
            username.setText(googleAccount.getGivenName());
        }

        checkConnectionOnce();
        setConnectionListener();
    }

    private void checkConnectionOnce() {
        if (!isNetworkAvailable()) {
            buttonsUsable(false);
            user.setImageResource((R.drawable.account));
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void setConnectionListener() {
        connectivityManager.registerNetworkCallback(
                builder.build(),
                new ConnectivityManager.NetworkCallback() {

                    @Override
                    public void onAvailable(Network network) {
                        new DownLoadImageTask(user).execute(googleAccount.getPhotoUrl().toString());
                        buttonsUsable(true);
                    }

                    @Override
                    public void onLost(Network network) {
                        buttonsUsable(false);
                        if (!isInFront) {
                            delegate.closeActivity();
                        }
                    }
                }
        );
    }

    private void buttonsUsable(boolean b) {
        setAlphas(b ? 255 : 100);
        buttonsUsable = b;
    }

    private void setAlphas(int opacity) {
        play.setImageAlpha(opacity);
        social.setImageAlpha(opacity);
        playText.setTextColor(Color.argb(opacity, 255, 255, 255));
        socialText.setTextColor(Color.argb(opacity, 255, 255, 255));
    }

    private void subscribeToPushService() {
        FirebaseMessaging.getInstance().subscribeToTopic("news");
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
        isInFront = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isInFront = false;
    }

    private void setListener() {
        // initialize auth listener
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // is signed in, everything alright
                    toaster("Still logged in");
                    username.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                }
                else {
                    // user is not logged in and redirected to login page
                    toaster("You are logged out.");
                    userActivity.signOut();
                    userActivity.goToLoginActivity();
                }
            }
        };
    }

    public void toaster(String message) {
        // toasts string
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
    }

    public void startActivityWithAnimation(View v, Class toStart) {
        v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.icon_click));
        startActivity(new Intent(MainActivity.this, toStart),
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    @Override
    public void onClick(View v) {
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
            toaster("Sorry, there is no internet connection.");
        }
    }
}
