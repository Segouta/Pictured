package com.example.christian.pictured;

/*
 * Created by Christian on 17-1-2018.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.signum;
import static java.lang.Math.toIntExact;

public class ServerManager extends Service implements ValueEventListener {

    private DatabaseReference mDatabase;
    private PlayActivity parent;

    private long endMillis;
    private String thingText;
    private static ServerManager sInstance;

    public ServerManager()
    {
        this.mDatabase =  FirebaseDatabase.getInstance().getReference();;

    }

    public String getThingText() {
        return thingText;
    }

    public long getEndMillis() {
        return endMillis;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

        final ArrayList<String> objects = new ArrayList<>();
        for (DataSnapshot child : dataSnapshot.child("things").getChildren()) {
            String object = child.getValue(String.class);
            objects.add(object);
        }

        Integer thingIndex = dataSnapshot.child("currentThing").child("index").getValue(Integer.class);
        Long endMillisTmp = dataSnapshot.child("currentThing").child("endMillis").getValue(Long.class);
        if(endMillisTmp != null)
        {
            endMillis = endMillisTmp;
        }

        if (thingIndex != null) {
            thingText = objects.get(thingIndex);
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.social)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");


            PlayActivity.getInstance().newThingArrived(this);
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sInstance = this;
        mDatabase.addValueEventListener(this);


        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        sInstance = this;
        return null;
    }
}
