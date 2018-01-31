package com.example.christian.pictured;

/*
 * By Christian Bijvoets, Minor Programmeren UvA, January 2018.
 * Gamedata contains times and layout that are necessary to show correct view to user,
 * even when logging in on a different device.
 */

public class GameData {

    public Long lastOpenedGameEndTime, openingTime, scoreTime, thingFoundTime;
    public String layout;

    public GameData() {}

    public GameData(String layout, Long lastOpenedGameEndTime, Long openingTime, Long scoreTime, Long thingFoundTime) {
        this.layout = layout;
        this.lastOpenedGameEndTime = lastOpenedGameEndTime;
        this.openingTime = openingTime;
        this.scoreTime = scoreTime;
        this.thingFoundTime = thingFoundTime;
    }
}
