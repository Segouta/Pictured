package com.example.christian.pictured;

/*
 * Created by Christian on 10-1-2018.
 */

import java.util.ArrayList;

public class Thing {

    private String name;
    private ArrayList<User> whoFoundIt;
    private ArrayList<Snap> snaps;
    private Game game;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<User> getWhoFoundIt() {
        return whoFoundIt;
    }

    public void setWhoFoundIt(ArrayList<User> whoFoundIt) {
        this.whoFoundIt = whoFoundIt;
    }

    public Game getGame() {
        return game;
    }
}
