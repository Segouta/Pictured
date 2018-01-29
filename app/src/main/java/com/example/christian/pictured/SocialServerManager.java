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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.android.volley.VolleyLog.TAG;

public class SocialServerManager implements ValueEventListener{

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
        DataSnapshot subData = dataSnapshot.child("users").child(mAuth.getUid()).child("gameData");

        Integer gamesAmount = dataSnapshot.child("users").child(mAuth.getUid()).child("gamesAmount").getValue(Integer.class);
        Long scoreTime = subData.child("scoreTime").getValue(Long.class);
        UserData data = dataSnapshot.child("users").child(mAuth.getUid()).getValue(UserData.class);

        parent.setData(gamesAmount, scoreTime, data.lastGames);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        // Getting Post failed, log a message
        Log.d(TAG, "onCancelled: ");

    }

}