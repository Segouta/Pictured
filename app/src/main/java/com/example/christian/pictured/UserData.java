package com.example.christian.pictured;

import java.util.Date;
import java.util.List;

public class UserData {

    public String username;
    public String email;
    public Date subscriptionDate;
    public int points;

    public UserData() {}

    public UserData(String username, String email, Date subscriptionDate, int points) {
        this.username = username;
        this.email = email;
        this.subscriptionDate = subscriptionDate;
        this.points = points;

    }
}
