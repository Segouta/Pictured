package com.example.christian.pictured;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserData {

    public String username;
    public String email;
    public Date subscriptionDate;
    public int points;
    public ArrayList<Integer> history;
    public String layout;
    public Long lastGame;

    public UserData() {}

    public UserData(String username, String email, Date subscriptionDate, int points, ArrayList<Integer> history, String layout, Long lastGame) {
        this.username = username;
        this.email = email;
        this.subscriptionDate = subscriptionDate;
        this.points = points;
        this.history = history;
        this.layout = layout;
        this.lastGame = lastGame;

    }
}
