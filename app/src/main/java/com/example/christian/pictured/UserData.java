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

    public UserData() {}

    public UserData(String username, String email, Date subscriptionDate, int points, ArrayList<Integer> history) {
        this.username = username;
        this.email = email;
        this.subscriptionDate = subscriptionDate;
        this.points = points;
        this.history = history;

    }
}
