package com.example.christian.pictured;

/*
 * Created by Christian on 30-1-2018.
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
