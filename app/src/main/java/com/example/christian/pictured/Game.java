package com.example.christian.pictured;

/*
 * Created by Christian on 10-1-2018.
 */

import java.util.ArrayList;
import java.util.Date;

public class Game {

    private int gameId;
    private String gameName;
    private Player gameCreator;
    private Date gameStartTime;
    private Date gameEndTime;
    private Date gameCreatedTime;
    private ArrayList<Thing> things;
    private ArrayList<Invitation> invitations;
    private ArrayList<Player> players;

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public Player getGameCreator() {
        return gameCreator;
    }

    public void setGameCreator(Player gameCreator) {
        this.gameCreator = gameCreator;
    }

    public Date getGameStartTime() {
        return gameStartTime;
    }

    public void setGameStartTime(Date gameStartTime) {
        this.gameStartTime = gameStartTime;
    }

    public Date getGameEndTime() {
        return gameEndTime;
    }

    public void setGameEndTime(Date gameEndTime) {
        this.gameEndTime = gameEndTime;
    }

    public Date getGameCreatedTime() {
        return gameCreatedTime;
    }

    public void setGameCreatedTime(Date gameCreatedTime) {
        this.gameCreatedTime = gameCreatedTime;
    }

    public ArrayList<Thing> getThings() {
        return things;
    }

    public ArrayList<Invitation> getInvitations() {
        return invitations;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }
}
