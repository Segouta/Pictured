package com.example.christian.pictured;

/*
 * Created by Christian on 10-1-2018.
 */

import android.media.Image;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

public class Snap {

    private Time spendTime;
    private Image photo;
    private ArrayList<String> suggestionWords;
    private int distance;
    private Date foundTime;
    private Player player;
    private Thing thing;

    public Time getSpendTime() {
        return spendTime;
    }

    public void setSpendTime(Time spendTime) {
        this.spendTime = spendTime;
    }

    public Image getPhoto() {
        return photo;
    }

    public void setPhoto(Image photo) {
        this.photo = photo;
    }

    public ArrayList<String> getSuggestionWords() {
        return suggestionWords;
    }

    public void setSuggestionWords(ArrayList<String> suggestionWords) {
        this.suggestionWords = suggestionWords;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public Date getFoundTime() {
        return foundTime;
    }

    public void setFoundTime(Date foundTime) {
        this.foundTime = foundTime;
    }

    public Player getPlayer() {
        return player;
    }

    public Thing getThing() {
        return thing;
    }
}
