package com.icm.projeto.vitalpaint;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.icm.projeto.vitalpaint.Data.GameDataManager;

import java.util.ArrayList;
import java.util.List;

public class LobbyTeamActivity extends AppCompatActivity {
    private String gameName;
    private int nPlayers;
    private String gameMode;
    private boolean isHost;
    private GameDataManager dbManager;

    List<String> blueTeamPlayers;
    List<String> redTeamPlayers;
    List<String> playersOnLobby;
    private Button joinRed;
    private Button joinBlue;
    private DatabaseReference blueTeam;
    private DatabaseReference redTeam;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_lobby);
        gameName = getIntent().getStringExtra("gameName");
        nPlayers = Integer.parseInt(getIntent().getStringExtra("nElements"));
        isHost = Boolean.valueOf(getIntent().getStringExtra("isHost"));
        this.setTitle(gameName);

        joinBlue = (Button) findViewById(R.id.enter_blue_team);
        joinRed = (Button) findViewById(R.id.enter_blue_team);

        blueTeamPlayers = new ArrayList<>();
        redTeamPlayers = new ArrayList<>();

        if (isHost){

        }
        dbManager = new GameDataManager(gameName);//create new game

        blueTeam = FirebaseDatabase.getInstance().getReference(gameName).child("Equipa Azul");
        redTeam = FirebaseDatabase.getInstance().getReference(gameName).child("Equipa Vermelha");

        joinBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //blueTeam.updateChildren(new HashMap<String, Object>.put(""));
            }
        });

        blueTeam.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String textOnLobbyBoard = "blue_team_player";
                TextView textv = null;
                List<String> users = new ArrayList<>();
                for (int i = 0; i < 3; i++) //3 é o numero maximo de jogadores por equipa
                    users.add("");//inicializar o array

                for (DataSnapshot data : dataSnapshot.getChildren()){
                    Log.i("", data.getKey());//nome de cada jogador na equipa atualmente
                    users.add(data.getKey());
                }

                TextView textv1 = (TextView)findViewById(R.id.blue_team_player1);
                textv1.setText(users.get(0));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}