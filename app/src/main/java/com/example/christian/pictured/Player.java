package com.example.christian.pictured;

/*
 * Created by Christian on 10-1-2018.
 */

import java.util.ArrayList;

public class Player {

    private int score;
    private int foundAmount;
    private User user;
    private Game game;
    private ArrayList<Snap> snaps;


    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getFoundAmount() {
        return foundAmount;
    }

    public void setFoundAmount(int foundAmount) {
        this.foundAmount = foundAmount;
    }

    public User getUser() {
        return user;
    }

    public Game getGame() {
        return game;
    }

    public ArrayList<Snap> getSnaps() {
        return snaps;
    }
}
