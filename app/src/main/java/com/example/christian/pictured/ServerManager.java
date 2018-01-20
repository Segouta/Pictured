package com.example.christian.pictured;

/*
 * Created by Christian on 17-1-2018.
 */

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.toIntExact;

public class ServerManager implements ValueEventListener{
    private DatabaseReference mDatabase;
    private PlayActivity parent;


    private String thingText;

    public ServerManager(PlayActivity parent, DatabaseReference mDatabase)
    {
        this.mDatabase = mDatabase;
        this.parent = parent;
        mDatabase.addListenerForSingleValueEvent(this);
    }

    public String getThingText() {
        return thingText;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

        final ArrayList<String> objects = new ArrayList<>();
        for (DataSnapshot child : dataSnapshot.child("things").getChildren()) {
            String object = child.getValue(String.class);
            objects.add(object);
        }
        long amountOfThings = dataSnapshot.child("things").getChildrenCount();
        thingText = objects.get(getRandomNumberInRange(amountOfThings - 1));

        parent.newThingArrived();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        // Getting Post failed, log a message
        thingText = "No connection to database";

    }

    private static int getRandomNumberInRange(long maxLong) {

        int min = 0;
        int max = (int) maxLong;

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

}
