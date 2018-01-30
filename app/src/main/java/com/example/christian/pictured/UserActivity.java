package com.example.christian.pictured;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
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

import static android.view.Gravity.TOP;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView back, logOutButton, avatarImage;

    private TextView username;

    GoogleSignInAccount googleAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        back = findViewById(R.id.backButton);
        logOutButton = findViewById(R.id.logOutButton);
        avatarImage = findViewById(R.id.avatarImage);

        username = findViewById(R.id.usernameText);

        back.setOnClickListener(this);
        logOutButton.setOnClickListener(this);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        getWindow().setEnterTransition(new Slide(TOP));

        googleAccount = GoogleSignIn.getLastSignedInAccount(this);

        if (googleAccount != null) {
            username.setText(googleAccount.getGivenName());
        }

        new DownLoadImageTask(avatarImage).execute(googleAccount.getPhotoUrl().toString());
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

    public void signOut() {
        // Firebase sign out
        FirebaseAuth.getInstance().signOut();

        // Google sign out
        GoogleSignIn.getClient(this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()).signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        toaster("Signed Out");
                    }
                });

        goToLoginActivity();
    }

    public void goToLoginActivity(){
        startActivity(new Intent(UserActivity.this, LoginActivity.class));
        finishAffinity();
    }

    public void toaster(String message) {
        // toasts string
        Toast.makeText(UserActivity.this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.back_click));
        if (v.equals(back)) {
            this.onBackPressed();
        }
        else if (v.equals(logOutButton)) {
            signOut();
        }
    }
}
