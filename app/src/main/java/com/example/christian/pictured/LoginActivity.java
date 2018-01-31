package com.example.christian.pictured;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Calendar;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    // Setup Firebase.
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    // Setup Google functionality.
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Create databasereference to the Firebase.
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Find views.
        findViewById(R.id.sign_in_button).setOnClickListener(this);

        // Google login necessities.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Get authorization for Firebase.
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase.
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately.
                Log.w(TAG, "Google sign in failed", e);
                updateUI(null);
            }
        }
    }


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
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        // This function makes the user log in to firebase with his or her Google account.
        // Show loading dialog.
        showProgressDialog();

        // Login credentials, when completed the sign in, update user interface.
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                        // Hide the progress dialog when done.
                        hideProgressDialog();
                    }
                });
    }

    private void signIn() {
        // This triggers the sign in intent for Google.
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void updateUI(FirebaseUser user) {
        // Hide the progress dialog to make sure it is gone and when logged in correctly, go to MainActivity.
        hideProgressDialog();
        if (user != null) {
            addUserToDb();
            goToMainActivity();
        }
    }

    public void goToMainActivity(){
        // Starts MainActivity with an intent, ends LoginActivity.
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    public void addUserToDb() {
        // Adds user to the database, where personal game information is also stored.
        // Get Firebase user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Check if user already exists in the database.
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the datasnapshot from the firebase and check if the userID is already in it.
                if (!dataSnapshot.child("users").child(user.getUid()).exists()) {
                    // Setup the initial information that needs to be entered in firebase for this user.
                    ArrayList<Long> lastGames = new ArrayList<Long>();
                    lastGames.add((long) 0);
                    Long init = (long) 0;

                    // This is a class containing a few UNIX times and layout state.
                    GameData gameData = new GameData("unopened", init, init, init, init);

                    // Create UserData object to put everything in Firebase at once.
                    UserData data = new UserData(user.getDisplayName(), user.getEmail(), Calendar.getInstance().getTime(), 0, lastGames, gameData);

                    // Store UserData object in Firebase.
                    mDatabase.child("users").child(user.getUid()).setValue(data);

                    // Notify user that he is now in the database.
                    toaster("Welcome, " + GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getGivenName());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addListenerForSingleValueEvent(postListener);
    }

    @Override
    public void onClick(View v) {
        // When clicking on an item, check what view it was that was clicked on.
        if (v.getId() == R.id.sign_in_button) {
            // When clicked on the sign in button, sign in.
            signIn();
        }
    }

    public void toaster(String message) {
        // Simple method that simplifies toasting actions.
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
    }
}