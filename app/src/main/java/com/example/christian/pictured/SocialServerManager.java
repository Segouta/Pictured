package com.example.christian.pictured;

/*
 * By Christian Bijvoets, Minor Programmeren UvA, January 2018.
 * This is SnapThat's Social server manager. It retrieves the data for the SocialActivity.
 */

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import static com.android.volley.VolleyLog.TAG;

public class SocialServerManager implements ValueEventListener{

    // Setup database and parent social activity.
    private DatabaseReference mDatabase;
    private SocialActivity parent;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public SocialServerManager(SocialActivity parent, DatabaseReference mDatabase)
    {
        this.mDatabase = mDatabase;
        this.parent = parent;
        mDatabase.addListenerForSingleValueEvent(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        // Retrieve data and set this data in the social acitivity when done.
        DataSnapshot subData = dataSnapshot.child("users").child(mAuth.getUid()).child("gameData");

        Integer gamesAmount = dataSnapshot.child("users").child(mAuth.getUid()).child("gamesAmount").getValue(Integer.class);
        UserData data = dataSnapshot.child("users").child(mAuth.getUid()).getValue(UserData.class);
        Long scoreTime = subData.child("scoreTime").getValue(Long.class);

        parent.setData(gamesAmount, scoreTime, data.lastGames);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        // Getting Post failed, log a message
        Log.d(TAG, "onCancelled: ");
    }
}