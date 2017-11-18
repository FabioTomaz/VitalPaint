package com.icm.projeto.vitalpaint.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Bruno Silva on 18/11/2017.
 */

public class GameData implements Serializable{

    private List<String> blueTeamPlayers;

    public List<String> getBlueTeamPlayers() {
        return blueTeamPlayers;
    }

    public void setBlueTeamPlayers(List<String> blueTeamPlayers) {
        this.blueTeamPlayers = blueTeamPlayers;
    }

    public List<String> getRedTeamPlayers() {
        return redTeamPlayers;
    }

    public void setRedTeamPlayers(List<String> redTeamPlayers) {
        this.redTeamPlayers = redTeamPlayers;
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

    private List<String> redTeamPlayers;
    private String gameName;
    private GameMode gameMode;
    private String host;
    private double lobbyLat;
    private double lobbyLong;


    public enum GameMode{ TEAMVSTEAM, DEATHMATCH};
}
