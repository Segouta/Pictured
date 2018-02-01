package com.example.christian.pictured;

/*
 * By Christian Bijvoets, Minor Programmeren UvA, January 2018.
 * This interface connects the mainactivity with the connection listener with the other activities.
 * So when in another activity when the wifi is lost, the app will close that activity and return to
 * the main activity and disable and grey out options that require wifi.
 */

public interface WifiCheckInterface {
    void closeActivity();
}
