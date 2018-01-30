package com.example.christian.pictured;

import java.util.ArrayList;
import java.util.Date;

public class UserData {

    public String username;
    public String email;
    public Date subscriptionDate;
    public int gamesAmount;
    public ArrayList<Long> lastGames;
    public GameData gameData;

    public UserData() {}

    public UserData(String username, String email, Date subscriptionDate, int gamesAmount, ArrayList<Long> lastGames, GameData gameData) {
        this.username = username;
        this.email = email;
        this.subscriptionDate = subscriptionDate;
        this.gamesAmount = gamesAmount;
        this.lastGames = lastGames;
        this.gameData = gameData;
    }
}
