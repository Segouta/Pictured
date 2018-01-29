package com.example.christian.pictured;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserData {

    public String username;
    public String email;
    public Date subscriptionDate;
    public int gamesAmount;
    public ArrayList<Long> lastGames;

    public UserData() {}

    public UserData(String username, String email, Date subscriptionDate, int gamesAmount, ArrayList<Long> lastGames) {
        this.username = username;
        this.email = email;
        this.subscriptionDate = subscriptionDate;
        this.gamesAmount = gamesAmount;
        this.lastGames = lastGames;

    }
}
