package com.example.christian.pictured;

/*
 * By Christian Bijvoets, Minor Programmeren UvA, January 2018.
 * This is a class that retrieves information needed to display things correctly in the playactivity.
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

    // Setup database and parent.
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
        // Retrieve all data needed for the PlayActivity.
        DataSnapshot subData = dataSnapshot.child("users").child(mAuth.getUid()).child("gameData");

        Long openingTime = subData.child("openingTime").getValue(Long.class);
        Integer gamesAmount = dataSnapshot.child("users").child(mAuth.getUid()).child("gamesAmount").getValue(Integer.class);
        Long scoreTime = subData.child("scoreTime").getValue(Long.class);
        Long userEndMillis = subData.child("lastOpenedGameEndTime").getValue(Long.class);
        Long endMillis = dataSnapshot.child("currentThing").child("endMillis").getValue(Long.class);
        ArrayList<Long> lastGamesList = dataSnapshot.child("users").child(mAuth.getUid()).getValue(UserData.class).lastGames;

        // Pass this information to the playactivity when it is loaded.
        parent.setTimeState(openingTime, scoreTime, gamesAmount, lastGamesList);

        // Check if the user is in the newest game, and if not adjust the layout.
        if (!Objects.equals(userEndMillis, endMillis)) {
            parent.storeLayout("unopened");
            parent.setLayout("unopened");
            mDatabase.child("users").child(mAuth.getUid()).child("gameData").child("lastOpenedGameEndTime").setValue(endMillis);
        }
        // Check if the time is expired and if so, set and store layout.
        else if (endMillis <= new Date().getTime()) {
            parent.storeLayout("expired");
            parent.setLayout("expired");
        }
        // If non of the above occurs, just get the layout and set it in the play activity.
        else {
            parent.setLayout(subData.child("layout").getValue(String.class));
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        // Getting Post failed, log a message
        Log.d(TAG, "onCancelled: ");

    }

}