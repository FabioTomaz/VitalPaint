package com.icm.projeto.vitalpaint.Data;

/**
 * Created by Bruno Silva on 20/11/2017.
 */

public class Lobby {
    public String getGameName() {
        return gameName;
    }

    @Override
    public String toString() {
        return "Lobby{" +
                "gameName='" + gameName + '\'' +
                ", gameMode=" + gameMode +
                '}';
    }
    public Lobby(){ }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    private String gameName;
    private GameMode gameMode;
    private String startDate;
    private int duration;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Lobby(String gameName, String host, GameMode gameMode, String startDate, int duration) {
        this.gameName = gameName;
        this.gameMode = gameMode;
        this.startDate = startDate;
        this.duration = duration;
    }

}