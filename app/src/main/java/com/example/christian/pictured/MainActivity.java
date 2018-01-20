package com.example.christian.pictured;


import android.content.Intent;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

// Key 1: 364da92137ce4d0d99397ae2b2c5a29b
// Key 2: fa76fa5671624f87b26cc8e3f61148a7
// Endpoint: https://westcentralus.api.cognitive.microsoft.com/vision/v1.0

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView user, play, social, settings;
    private TextView username;

    UserActivity userActivity = new UserActivity();
    GoogleSignInAccount googleAccount;

    DatabaseReference mDatabase;

    private FirebaseAuth.AuthStateListener mAuthListener;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setListener();

        username = findViewById(R.id.user_title);

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
            startActivity(new Intent(MainActivity.this, UserActivity.class));
        }
        else if (v.equals(play)) {
            startActivity(new Intent(MainActivity.this, PlayActivity.class));
        }
        else if (v.equals(social)) {
            startActivity(new Intent(MainActivity.this, SocialActivity.class));
        }
        else if (v.equals(settings)) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        }
    }
}
