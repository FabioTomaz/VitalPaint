package com.icm.projeto.vitalpaint.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Bruno Silva on 18/11/2017.
 */

public class GameData implements Serializable{
    private String gameName;
    private GameMode gameMode;
    private String host;
    private double lobbyLat;
    private double lobbyLong;
    private String playerName;
    private String startDate;
    private int duration;
    private String city;
    private int radius;

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public GAMERESULT getResult() {
        return result;
    }

    public void setResult(GAMERESULT result) {
        this.result = result;
    }

    private GAMERESULT result;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    private List<String> blueTeamPlayers;

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }


    public GameData(String gameName, GameMode gameMode, String startDate, int duration, double lobbyLat, double lobbyLong, String city){
        this.gameName = gameName;
        this.gameMode = gameMode;
        this.startDate = startDate;
        this.duration = duration;
        this.lobbyLat = lobbyLat;
        this.lobbyLong = lobbyLong;
        this.city = city;
    }

    public GameData(){

    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }



    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public double getLobbyLat() {
        return lobbyLat;
    }

    public void setLobbyLat(double lobbyLat) {
        this.lobbyLat = lobbyLat;
    }

    public double getLobbyLong() {
        return lobbyLong;
    }

    public void setLobbyLong(double lobbyLong) {
        this.lobbyLong = lobbyLong;
    }

    public static enum GAMERESULT{
        REDTEAMWON, BLUETEAMWON, DRAW
    }


}
