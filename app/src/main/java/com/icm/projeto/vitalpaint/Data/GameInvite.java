package com.icm.projeto.vitalpaint.Data;

/**
 * Created by young on 27/11/2017.
 */

public class GameInvite {
    private String gameName;
    private String sender;
    private GameMode gameMode;
    private String teamToJoin;

    public GameInvite(String gameName, String sender, GameMode gameMode, String teamToJoin) {
        this.gameName = gameName;
        this.sender = sender;
        this.gameMode = gameMode;
        this.teamToJoin = teamToJoin;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public String getTeamToJoin() {
        return teamToJoin;
    }

    public void setTeamToJoin(String teamToJoin) {
        this.teamToJoin = teamToJoin;
    }

    public String getGameName() {

        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }
}
