package com.icm.projeto.vitalpaint;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LobbyTeamActivity extends AppCompatActivity {
    private String gameName;
    private int nPlayers;
    private String gameMode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_lobby);
        gameName = getIntent().getStringExtra("gameName");
        nPlayers = Integer.parseInt(getIntent().getStringExtra("nElements"));
        gameName = getIntent().getStringExtra("gameName");
        this.setTitle(gameName);
    }
}
