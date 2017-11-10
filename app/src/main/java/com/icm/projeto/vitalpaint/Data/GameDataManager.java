package com.icm.projeto.vitalpaint.Data;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Bruno Silva on 07/11/2017.
 */

public class GameDataManager {

    private List<String> blueTeamPlayers;
    private List<String> redTeamPlayers;
    private String gameName;
    private Map<String, Map<String, Double>> blueTeamData = new HashMap<>();
    private Map<String, Map<String, Double>> redTeamData = new HashMap<>();
    private DatabaseReference dbData;

    public GameDataManager(String gameName, List<String> blueTeamPlayers, List<String> redTeamPlayers ){
        this.blueTeamPlayers = blueTeamPlayers;
        this.redTeamPlayers = redTeamPlayers;
        this.gameName = gameName;
        Map <String, Double> coordinates = new HashMap<>();
        coordinates.put("lat", 0.0);
        coordinates.put("long", 0.0);

        dbData = FirebaseDatabase.getInstance().getReference(gameName);//no inicial do jogo

        //no no do jogo, criar 2 nos filhos, um para cada equipa
        //em cada no dos filhos colocar todos os jogadores e para cada jogador a sua latitude e longitude

        for(String s : blueTeamPlayers){
            blueTeamData.put(s, coordinates);
        }
        for(String s : redTeamPlayers){
            redTeamData.put(s, coordinates);
        }

        dbData.child("Equipa Azul").setValue(blueTeamData);
        dbData.child("Equipa Vermelha").setValue(redTeamData);
        dbData.child("Game coordinates").setValue(redTeamData);
    }

    public void updatePlayerLocation(String team, String player, double lat, double longt){
        FirebaseDatabase.getInstance().getReference(gameName).child(team).child(player).child("lat").setValue(lat);
        FirebaseDatabase.getInstance().getReference(gameName).child(team).child(player).child("long").setValue(longt);
    }


    public void setLobbyCoordinates(){

    }



}
