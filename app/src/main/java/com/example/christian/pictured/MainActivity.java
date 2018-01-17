package com.example.christian.pictured;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.auth.api.signin.GoogleSignIn;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.AnalysisResult;
import com.microsoft.projectoxford.vision.contract.Caption;
import com.microsoft.projectoxford.vision.rest.VisionServiceException;

import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import java.io.InputStream;
import java.util.ArrayList;

// Key 1: 364da92137ce4d0d99397ae2b2c5a29b
// Key 2: fa76fa5671624f87b26cc8e3f61148a7
// Endpoint: https://westcentralus.api.cognitive.microsoft.com/vision/v1.0

public class MainActivity extends AppCompatActivity {

    public VisionServiceClient visionServiceClient = new VisionServiceRestClient("364da92137ce4d0d99397ae2b2c5a29b", "https://westcentralus.api.cognitive.microsoft.com/vision/v1.0");



    private TextView usernameText, thingText;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;

    DatabaseReference mDatabase;
    ImageView myImage;
    File imgFile;

    String imageFileName;
    File storageDir;

    private FirebaseAuth.AuthStateListener mAuthListener;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageFileName = "SnapThatCurrent.jpg";
        storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        imgFile = new  File(storageDir + "/" + imageFileName);

        Button processButton = findViewById(R.id.processButton);

        usernameText = findViewById(R.id.usernameText);
        myImage = (ImageView) findViewById(R.id.thumbnail);
        thingText = findViewById(R.id.thingText);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        setListener();
        setThumbnail();
        getThing();

//        Bitmap mBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

        Bitmap mBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.meid);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        processButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncTask<InputStream, String, String> visionTask = new AsyncTask<InputStream, String, String>() {

                    ProgressDialog mDialog = new ProgressDialog(MainActivity.this);

                    @Override
                    protected String doInBackground(InputStream... params) {
                        try {
                            publishProgress("Processing dat Snap...");
                            String[] features = {"Description"};
                            String[] detailes = {};

                            AnalysisResult result = visionServiceClient.analyzeImage(params[0],features,detailes);

                            String strResult = new Gson().toJson(result);
                            return strResult;

                        } catch (Exception e) {
                            return null;
                        }
                    }

                    @Override
                    protected void onPreExecute() {
                        mDialog.show();
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        mDialog.dismiss();

                        AnalysisResult result = new Gson().fromJson(s, AnalysisResult.class);
                        TextView descText = findViewById(R.id.descText);
                        StringBuilder stringBuilder = new StringBuilder();
                        for(Caption caption:result.description.captions) {
                            stringBuilder.append(caption.text);
                        }
                        descText.setText(stringBuilder);
                    }

                    @Override
                    protected void onProgressUpdate(String... values) {
                        mDialog.setMessage(values[0]);
                    }
                };

                visionTask.execute(inputStream);
            }
        });
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
        setThumbnail();
    }

    public void goToLoginActivity(){
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    private void setListener() {

        usernameText.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        // initialize auth listener
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in, everything alright
                    toaster("Still logged in");
                }
                else {
                    // user is not logged in and redirected to splash page
                    toaster("You are logged out.");
                    signOut();
                    goToLoginActivity();
                }
            }
        };
    }

    private void signOut() {
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
                        toaster("signed out");
                    }
                });

        goToLoginActivity();
    }

    public void toaster(String message) {
        // toasts string
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
    }

    public void signOutClicked(View view) {
        toaster("clicked");
        signOut();
    }

    public void setThumbnail() {

        if(imgFile.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            myImage.setImageBitmap(myBitmap);

        }
    }

    public void photo(View view) {
        startActivity(new Intent(MainActivity.this, PhotoActivity.class));
    }

    public void photoOut(View view) {
        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                toaster("errortje");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name

        File image = new File(storageDir, imageFileName);
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void getThing() {


        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
//                 Object thing = dataSnapshot.child("things").getValue();

                long n = dataSnapshot.child("things").getChildrenCount();
//                getRandom(n);

                final ArrayList<String> objects = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.child("things").getChildren()) {
                    String object = child.getValue(String.class);
                    objects.add(object);
                }
                String thing = objects.get(2);
                thingText.setText(thing);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message

            }
        };
        mDatabase.addListenerForSingleValueEvent(postListener);
    }


}
