package com.example.christian.pictured;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by Christian on 10-1-2018.
 */
public class User {

    private String username;
    private String email;
    private String hashPassword;
    private int score;
    private ArrayList<GameHistoryItem> gameHistoryItems;
    private ArrayList<User> friends;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHashPassword() {
        return hashPassword;
    }

    public void setHashPassword(String hashPassword) {
        this.hashPassword = hashPassword;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public ArrayList<GameHistoryItem> getGameHistoryItems() {
        return gameHistoryItems;
    }

    public ArrayList<User> getFriends() {
        return friends;
    }
}
