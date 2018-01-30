package com.example.christian.pictured;


import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.InputStream;
import java.net.URL;

import static android.view.Gravity.BOTTOM;
import static android.view.Gravity.END;
import static android.view.Gravity.TOP;

// Key 1: 364da92137ce4d0d99397ae2b2c5a29b
// Key 2: fa76fa5671624f87b26cc8e3f61148a7
// Endpoint: https://westcentralus.api.cognitive.microsoft.com/vision/v1.0

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView user, play, social, settings;
    private TextView username, playText, socialText;

    private boolean isInFront, buttonsUsable;

    ConnectivityManager connectivityManager;
    NetworkRequest.Builder builder;

    UserActivity userActivity = new UserActivity();

    GoogleSignInAccount googleAccount;

    DatabaseReference mDatabase;

    private FirebaseAuth.AuthStateListener mAuthListener;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getIntent().getExtras() != null) {

            startActivity(new Intent(this, PlayActivity.class));
            for (String key : getIntent().getExtras().keySet()) {
                String value = getIntent().getExtras().getString(key);

                if (key.equals("PlayActivity") && value.equals("True")) {
                    Intent intent = new Intent(this, PlayActivity.class);
                    intent.putExtra("value", value);
                    startActivity(intent);
                    finish();
                }

                //TODO: Hier moet hij meteen naar de playactivity gaan.

            }
        }

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        builder = new NetworkRequest.Builder();

        isInFront = true;
        buttonsUsable = true;

        subscribeToPushService();

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));

        setListener();

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

        mDatabase = FirebaseDatabase.getInstance().getReference();

        googleAccount = GoogleSignIn.getLastSignedInAccount(this);

        if (googleAccount != null) {
            username.setText(googleAccount.getGivenName());
        }

//        new DownLoadImageTask(user).execute(googleAccount.getPhotoUrl().toString());

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
                        if(isInFront) {
                            buttonsUsable(true);
                        } else {
                            buttonsUsable(true);
                        }
                    }

                    @Override
                    public void onLost(Network network) {
                        if(isInFront) {
                            buttonsUsable(false);
                        } else {
                            buttonsUsable(false);
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);

                            //TODO: hier moet alles gesloten worden want dit werkt zo niet.
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
        String token = FirebaseInstanceId.getInstance().getToken();
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
                    // User is signed in, everything alright
                    toaster("Still logged in");
                    username.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                }
                else {
                    // user is not logged in and redirected to splash page
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

    @Override
    public void onClick(View v) {
        if (v.equals(user)) {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.icon_click));
            startActivity(new Intent(MainActivity.this, UserActivity.class),
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else if (v.equals(settings)) {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.icon_click));
            startActivity(new Intent(MainActivity.this, SettingsActivity.class),
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else if (buttonsUsable){
            if (v.equals(play)) {
                v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.icon_click));
                startActivity(new Intent(MainActivity.this, PlayActivity.class),
                        ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            }
            else if (v.equals(social)) {
                v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.icon_click));
                startActivity(new Intent(MainActivity.this, SocialActivity.class),
                        ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            }

        } else {
            toaster("Sorry, there is no internet connection.");
        }
    }
}
