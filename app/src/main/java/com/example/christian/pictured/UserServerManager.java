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
import java.util.Objects;

import static com.android.volley.VolleyLog.TAG;

public class UserServerManager implements ValueEventListener{

    private DatabaseReference mDatabase;
    private PlayActivity parent;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public UserServerManager(PlayActivity parent, DatabaseReference mDatabase)
    {
        this.parent = parent;
        this.mDatabase = mDatabase;
        mDatabase.addListenerForSingleValueEvent(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        DataSnapshot subData = dataSnapshot.child("users").child(mAuth.getUid()).child("gameData");

        Long openingTime = subData.child("openingTime").getValue(Long.class);
        Integer gamesAmount = dataSnapshot.child("users").child(mAuth.getUid()).child("gamesAmount").getValue(Integer.class);
        Long scoreTime = subData.child("scoreTime").getValue(Long.class);
        Long userEndMillis = subData.child("lastOpenedGameEndTime").getValue(Long.class);
        Long endMillis = dataSnapshot.child("currentThing").child("endMillis").getValue(Long.class);
        ArrayList<Long> lastGamesList = dataSnapshot.child("users").child(mAuth.getUid()).getValue(UserData.class).lastGames;


        parent.setTimeState(openingTime, scoreTime, gamesAmount, lastGamesList);

        if (!Objects.equals(userEndMillis, endMillis)) {
            parent.storeLayout("unopened");
            parent.setLayout("unopened");
            mDatabase.child("users").child(mAuth.getUid()).child("gameData").child("lastOpenedGameEndTime").setValue(endMillis);
        } else if (endMillis <= new Date().getTime()) {
            parent.storeLayout("expired");
            parent.setLayout("expired");
        } else {
            parent.setLayout(subData.child("layout").getValue(String.class));
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        // Getting Post failed, log a message
        Log.d(TAG, "onCancelled: ");

    }

}