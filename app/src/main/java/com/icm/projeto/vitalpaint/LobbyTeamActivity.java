package com.icm.projeto.vitalpaint;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.icm.projeto.vitalpaint.Data.GameData;
import com.icm.projeto.vitalpaint.Data.GameDataManager;
import com.icm.projeto.vitalpaint.Data.GameDate;
import com.icm.projeto.vitalpaint.Data.GameMode;
import com.icm.projeto.vitalpaint.Data.UserData;
import com.icm.projeto.vitalpaint.Data.UserDataManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LobbyTeamActivity extends AppCompatActivity implements UserDataManager.UserDataListener {
    private String gameName;
    private GameMode gameMode;
    private boolean isHost;
    private GameDataManager dbManager;
    private GameData gameData;
    private Map<String, Double> coordinates;
    private GameDate startDate;
    private GameDate endDate;
    private UserDataManager userDataManager;
    private UserData userData;
    private FirebaseAuth auth;
    private ListView blueTeamListView;
    private ListView redTeamListView;

    List<String> blueTeamPlayers;
    List<String> redTeamPlayers;
    private ArrayAdapter blueAdapter;
    private ArrayAdapter redAdapter;
    private Button joinRed;
    private Button joinBlue;
    private DatabaseReference blueTeam;
    private DatabaseReference redTeam;
    private DatabaseReference game;
    private final String ROOTNODE = "Games";
    public static final int PROFILE_DATA = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_lobby);
        auth = FirebaseAuth.getInstance();
        //criar listener para obter dados do user loggado
        userDataManager = new UserDataManager(auth.getCurrentUser().getEmail());
        //userDataManager.addListener(this, PROFILE_DATA);
        gameName = getIntent().getStringExtra("gameName");
        isHost = getIntent().getBooleanExtra ("isHost", false);
        gameMode = GameMode.valueOf(getIntent().getStringExtra("gameMode")); //obter  a string do enum e converter para enum
        startDate = (GameDate) getIntent().getSerializableExtra("startDate");
        endDate = (GameDate) getIntent().getSerializableExtra("endDate");
        this.setTitle(gameName);
        coordinates = new HashMap<>();
        coordinates.put("lat", 0.0);
        coordinates.put("longt", 0.0);

        joinBlue = (Button) findViewById(R.id.enter_blue_team);
        joinRed = (Button) findViewById(R.id.enter_red_team);

        blueTeamPlayers = new ArrayList<>();
        redTeamPlayers = new ArrayList<>();
        gameData = new GameData(gameName, gameMode);

        blueTeamListView = (ListView) findViewById(R.id.list_blue_team);
        redTeamListView = (ListView) findViewById(R.id.list_red_team);
        blueAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        redAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        blueTeamListView.setAdapter(blueAdapter);
        redTeamListView.setAdapter(redAdapter);

        //dbManager = new GameDataManager(gameName);//create new game
        game = FirebaseDatabase.getInstance().getReference().child(ROOTNODE);//criar nó do jogo
        blueTeam = FirebaseDatabase.getInstance().getReference(ROOTNODE).child(gameName).child("Equipa Azul");
        redTeam = FirebaseDatabase.getInstance().getReference(ROOTNODE).child(gameName).child("Equipa Vermelha");
        Log.i("startdate", startDate+"");
        Log.i("enddate", endDate+"");
        if (isHost) {
            //Log.i("im host", "he");
            Map<String, Object> map = new HashMap<>();
            map.put(gameName, gameData);
            game.updateChildren(map);
            blueTeam.child("score").setValue(0);
            redTeam.child("score").setValue(0);
        }

        joinBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> player = new HashMap<>();
                Log.i("ola", userData.getNAME()+"");
                player.put(userData.getNAME(), coordinates);
                blueTeam.updateChildren(player);
                joinBlue.setEnabled(false);
                joinRed.setEnabled(false);
                //botao para sair da equipa
            }
        });

        joinRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> player = new HashMap<>();
                player.put(userData.getNAME(), coordinates);
                redTeam.updateChildren(player);
                joinBlue.setEnabled(false);
                joinRed.setEnabled(false);
            }
        });

        blueTeam.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String textOnLobbyBoard = "blue_team_player";
                TextView textv = null;
                List<String> users = new ArrayList<>();
                UserData playerData;
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    if(!data.getKey().equals("score")) {
                        Log.i("", data.getValue(Object.class) + "");//nome de cada jogador na equipa atualmente
                        playerData = data.getValue(UserData.class);
                        blueAdapter.add(playerData.getNAME());
                    }
                }
                //adicionar os users na listview
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        redTeam.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /*String textOnLobbyBoard = "blue_team_player";
                TextView textv = null;
                List<String> users = new ArrayList<>();*/
                UserData playerData;
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    if(!data.getKey().equals("score")) {
                        Log.i("", data.getValue(UserData.class) + "");//nome de cada jogador na equipa atualmente
                        playerData = data.getValue(UserData.class);
                        redAdapter.add(playerData.getNAME());
                    }
                }
                //adicionar os users na listview
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onReceiveUserData(int requestType , UserData user, Bitmap profilePic, Bitmap headerPic) {
        this.userData = userData;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {//eliminar o jogo da BD caso o host saia do lobby
        if (keyCode == KeyEvent.KEYCODE_BACK && isHost) {
            // do something
            FirebaseDatabase.getInstance().getReference().child("Games").child(gameName).setValue(null);
        }
        return super.onKeyDown(keyCode, event);
    }


}