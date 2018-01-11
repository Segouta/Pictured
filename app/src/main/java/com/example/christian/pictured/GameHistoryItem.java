package com.example.christian.pictured;

/*
 * Created by Christian on 10-1-2018.
 */

public class GameHistoryItem {

    private int wonPoints;
    private int gameRank;
    private User user;
    private Game game;

    public int getWonPoints() {
        return wonPoints;
    }

    public void setWonPoints(int wonPoints) {
        this.wonPoints = wonPoints;
    }

    public int getGameRank() {
        return gameRank;
    }

    public void setGameRank(int gameRank) {
        this.gameRank = gameRank;
    }

    public User getUser() {
        return user;
    }

    public Game getGame() {
        return game;
    }
}
