package com.icm.projeto.vitalpaint.Data;

/**
 * Created by young on 27/11/2017.
 */

public class GamePlayed {
    public static enum RESULT{
        WON, LOST, DRAW
    }

    public GamePlayed(){
    }

    @Override
    public String toString() {
        return "GamePlayed{" +
                "gameResult=" + gameResult +
                ", startDate='" + startDate + '\'' +
                ", gameMode=" + gameMode +
                ", time=" + time +
                ", city='" + city + '\'' +
                '}';
    }

    private RESULT gameResult;
    private String startDate;
    private GameMode gameMode;
    private int time;
    private String city;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public RESULT getGameResult() {
        return gameResult;
    }

    public void setGameResult(RESULT gameResult) {
        this.gameResult = gameResult;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public GamePlayed(RESULT gameResult, String startDate, GameMode gameMode, int time, String city) {
        this.gameResult = gameResult;
        this.startDate = startDate;
        this.gameMode = gameMode;
        this.time = time;
        this.city = city;
    }
}
