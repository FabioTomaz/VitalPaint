package com.icm.projeto.vitalpaint.Data;

import com.icm.projeto.vitalpaint.Data.GameMode;

import java.util.ArrayList;

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
                ", host='" + host + '\'' +
                ", gameMode=" + gameMode +
                '}';
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    private String gameName;
    private String host;
    private GameMode gameMode;

    public Lobby(String gameName, String host, GameMode gameMode) {
        this.gameName = gameName;
        this.host = host;
        this.gameMode = gameMode;
    }



    public static ArrayList<Lobby> getItems() {
        ArrayList<Lobby> lobbys = new ArrayList<Lobby>();
        lobbys.add(new Lobby("Game1", "bsilva", GameMode.TEAMVSTEAM));
        lobbys.add(new Lobby("Game 2", "dropkick", GameMode.TEAMVSTEAM));
        lobbys.add(new Lobby("Game 3", "bsilva33", GameMode.DEATHMATCH));
        return lobbys;
    }
}
