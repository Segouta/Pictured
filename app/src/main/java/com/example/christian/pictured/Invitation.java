package com.example.christian.pictured;

/*
 * Created by Christian on 11-1-2018.
 */

import java.util.Date;

public class Invitation {

    private String status;
    private Date date;
    private String message;
    private Game game;
    private User user;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Game getGame() {
        return game;
    }

    public User getUser() {
        return user;
    }
}
