package com.example.christian.pictured;

/*
 * Created by Christian on 17-1-2018.
 */

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import static com.android.volley.VolleyLog.TAG;

public class UserServerManager implements ValueEventListener{

    private DatabaseReference mDatabase;
    private PlayActivity parent;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public UserServerManager(PlayActivity parent, DatabaseReference mDatabase)
    {
        this.mDatabase = mDatabase;
        this.parent = parent;
        mDatabase.addListenerForSingleValueEvent(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        parent.setLayout(dataSnapshot.child("users").child(mAuth.getUid()).child("layout").getValue(String.class));

        Long userEndMillis = dataSnapshot.child("users").child(mAuth.getUid()).child("lastGame").getValue(Long.class);
        Long endMillis = dataSnapshot.child("currentThing").child("endMillis").getValue(Long.class);
        parent.compareMillis(userEndMillis, endMillis);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        // Getting Post failed, log a message
        Log.d(TAG, "onCancelled: ");

    }

}