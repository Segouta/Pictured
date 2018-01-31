package com.example.christian.pictured;

/*
 * By Christian Bijvoets, Minor Programmeren UvA, January 2018.
 * This class manages the items from the Firebase database that are the same for every user.
 * such as endTime and thing to find.
 */

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import static com.android.volley.VolleyLog.TAG;

public class ServerManager implements ValueEventListener{

    private DatabaseReference mDatabase;
    private PlayActivity parent;

    public ServerManager(PlayActivity parent, DatabaseReference mDatabase)
    {
        this.mDatabase = mDatabase;
        this.parent = parent;
        mDatabase.addListenerForSingleValueEvent(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        // Get the index of the current thing.
        Integer index = dataSnapshot.child("currentThing").child("index").getValue(Integer.class);

        // When received, call setThing function in PlayActivity.
        parent.setThing(dataSnapshot.child("things").child(index.toString()).getValue(String.class),
                dataSnapshot.child("currentThing").child("endMillis").getValue(Long.class));
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        // Getting Post failed, log a message
        Log.d(TAG, "onCancelled: ");

    }

}