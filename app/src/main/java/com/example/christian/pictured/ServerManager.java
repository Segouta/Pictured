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

public class ServerManager implements ValueEventListener{

    private DatabaseReference mDatabase;
    private PlayActivity parent;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public ServerManager(PlayActivity parent, DatabaseReference mDatabase)
    {
        this.mDatabase = mDatabase;
        this.parent = parent;
        mDatabase.addListenerForSingleValueEvent(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Integer index = dataSnapshot.child("currentThing").child("index").getValue(Integer.class);

        parent.setThing(dataSnapshot.child("things").child(index.toString()).getValue(String.class), dataSnapshot.child("currentThing").child("endMillis").getValue(Long.class));
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        // Getting Post failed, log a message
        Log.d(TAG, "onCancelled: ");

    }

}