package com.example.christian.pictured;

import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener {

    static final int REQUEST_TAKE_PHOTO = 1;

    DatabaseReference mDatabase;

    private TextView thingText, tagText, descText;

    private Button openCameraButton, processButton, backButton;
    private ImageView snapImage;
    private ProgressDialog progressDialog;

    private CameraManager myCameraManager;
    private MSVisionManager myMSVisionManager;
    private ServerManager myServerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        processButton = findViewById(R.id.processButton);
        openCameraButton = findViewById(R.id.openCameraButton);

        thingText = findViewById(R.id.thingText);
        tagText = findViewById(R.id.tagText);
        descText = findViewById(R.id.descText);

        snapImage = findViewById(R.id.snapImage);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        myCameraManager = new CameraManager(this);
        myServerManager = new ServerManager(this, mDatabase);
        myMSVisionManager = new MSVisionManager(this, myCameraManager);

        processButton.setOnClickListener(this);
        openCameraButton.setOnClickListener(this);

        setLastImageThumbnail();
    }

    protected void onResume() {
        super.onResume();
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setLastImageThumbnail();

    }

    private void setLastImageThumbnail()
    {
        snapImage.setImageBitmap(myCameraManager.getThumbnail());
    }

    public void visionCheckDone(String description, ArrayList<String> tags)
    {
        TextView descText = findViewById(R.id.descText);
        TextView tagText = findViewById(R.id.tagText);
        descText.setText(description);
        tagText.setText(tags.toString());
        progressDialog.dismiss();
    }

    public void newThingArrived()
    {
        thingText.setText(myServerManager.getThingText());
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, myCameraManager.getImageFileURI());
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.equals(processButton)) {
            progressDialog = new ProgressDialog(PlayActivity.this);
            progressDialog.setMessage("Analyzing dat snap...");
            progressDialog.show();
            myMSVisionManager.startVisionCheck();
        }
        if(v.equals(openCameraButton)) {
            dispatchTakePictureIntent();
        }
    }

}
