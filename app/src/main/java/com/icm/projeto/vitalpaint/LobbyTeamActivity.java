package com.icm.projeto.vitalpaint;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.icm.projeto.vitalpaint.Data.GameDataManager;

public class LobbyTeamActivity extends AppCompatActivity {
    private String gameName;
    private int nPlayers;
    private String gameMode;
    private GameDataManager dbManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_lobby);
        gameName = getIntent().getStringExtra("gameName");
        nPlayers = Integer.parseInt(getIntent().getStringExtra("nElements"));
        gameName = getIntent().getStringExtra("gameName");
        this.setTitle(gameName);

        //dbManager = new GameDataManager("Game1", blueTeamPlayers, redTeamPlayers);
    }
}
