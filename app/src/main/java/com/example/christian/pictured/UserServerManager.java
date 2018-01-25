package com.example.christian.pictured;

/*
 * Created by Christian on 17-1-2018.
 */

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import static com.android.volley.VolleyLog.TAG;

public class UserServerManager implements ChildEventListener{

    private DatabaseReference mDatabase;
    private PlayActivity parent;


    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public UserServerManager(PlayActivity parent, DatabaseReference mDatabase)
    {
        this.mDatabase = mDatabase;
        this.parent = parent;
        mDatabase.child("users").child(mAuth.getUid()).addChildEventListener(this);
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        parent.setLayout(dataSnapshot.child("users").child(mAuth.getUid()).child("layout").getValue(String.class));
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        // Getting Post failed, log a message
        Log.d(TAG, "onCancelled: ");

    }

}