package com.icm.projeto.vitalpaint.Data;

import java.util.List;

/**
 * Created by Bruno Silva on 08/11/2017.
 */

public class GameSession {
    private String gameName;
    private List<String> blueTeamPlayers;
    private List<String> redTeamPlayers;

    public GameSession(String gameName, List<String> blueTeamPlayers, List<String> redTeamPlayers ){
        this.gameName = gameName;
        this.blueTeamPlayers = blueTeamPlayers;
        this.redTeamPlayers = redTeamPlayers;
    }
}
