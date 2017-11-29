package com.icm.projeto.vitalpaint;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.icm.projeto.vitalpaint.Data.GameMode;
import com.icm.projeto.vitalpaint.Data.GamePlayed;
import com.icm.projeto.vitalpaint.Data.UserDataManager;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

public class GameEndedActivity extends AppCompatActivity{
    private String startDate;
    private String winningTeam;
    private String myTeam;
    private String gameName;
    private TextView winnerTeamtxt;
    private LinearLayout winnerLayout;
    private DatabaseReference dbRef;
    private DatabaseReference userDbRef;
    private List<String> blueTeamPlayers;
    private List<String> redTeamPlayers;
    private ArrayAdapter blueAdapter;
    private ArrayAdapter redAdapter;
    private ListView blueTeamListView;
    private ListView redTeamListView;
    private FirebaseAuth auth;
    private UserDataManager userDataManager;
    private GameMode gameMode;
    private String zone;
    private Button exitButton;
    private Button replayButton;
    public static final int PROFILE_DATA = 1;
    private String userEncEmail;
    private String myName;


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_ended);
        startDate = getIntent().getStringExtra("startDate");
        myTeam = getIntent().getStringExtra("myTeam");
        winningTeam = getIntent().getStringExtra("winnerTeam");
        gameName = getIntent().getStringExtra("gameName");
        zone = getIntent().getStringExtra("zone");
        myName = getIntent().getStringExtra("myName");
        gameMode = GameMode.valueOf(getIntent().getStringExtra("gameMode")); //obter  a string do enum e converter para enum
        Log.i("GAMEEND", myTeam.toString());
        winnerTeamtxt = (TextView) findViewById(R.id.winner_team_txt);
        winnerLayout = (LinearLayout) findViewById(R.id.winnerlayout);
        winnerTeamtxt.setText("A " + winningTeam + " Ganhou!");
        replayButton = (Button) findViewById(R.id.replayButton);
        if (winningTeam.equals("Equipa Azul"))
            winnerLayout.setBackgroundColor(getResources().getColor(R.color.blueTeamColor));
        else
            winnerLayout.setBackgroundColor(getResources().getColor(R.color.redTeamColor));

        //listviews
        blueTeamPlayers = new ArrayList<>();
        redTeamPlayers = new ArrayList<>();
        blueTeamListView = (ListView) findViewById(R.id.list_blue_team);
        redTeamListView = (ListView) findViewById(R.id.list_red_team);
        exitButton = (Button) findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameEndedActivity.this, PlayActivity.class);
                startActivity(intent);
            }
        });
        replayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameEndedActivity.this, GameMapActivity.class);
                Log.i("myteam", myTeam+"");
                intent.putExtra("myTeam", myTeam);
                intent.putExtra("gameName", gameName);
                intent.putExtra("userName", myName);
                intent.putExtra("startDate", startDate);
                intent.putExtra("zone", zone);
                if (gameMode == GameMode.TEAMVSTEAM)
                    intent.putExtra("gameMode", GameMode.TEAMVSTEAM.toString() );
                startActivity(intent);
            }
        });
        replayButton = (Button) findViewById(R.id.replayButton);
        blueAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        redAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        blueTeamListView.setAdapter(blueAdapter);
        redTeamListView.setAdapter(redAdapter);

        auth = FirebaseAuth.getInstance();
        //get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userEncEmail = UserDataManager.encodeUserEmail(user.getEmail());
        dbRef = FirebaseDatabase.getInstance().getReference("Games").child(gameName);
        userDbRef = FirebaseDatabase.getInstance().getReference("Users");
        dbRef.child("Equipa Azul").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> users = new ArrayList<>();
                blueAdapter.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (!data.getKey().equals("score")) {
                        blueAdapter.add(data.child("name").getValue(String.class));
                    }
                }
                blueAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        dbRef.child("Equipa Vermelha").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> users = new ArrayList<>();
                redAdapter.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (!data.getKey().equals("score")) {
                        redAdapter.add(data.child("name").getValue(String.class));
                    }
                }
                redAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        dbRef.child("Equipa Azul").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> users = new ArrayList<>();
                blueAdapter.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (!data.getKey().equals("score")) {
                        blueAdapter.add(data.child("name").getValue());
                    }
                }
                blueAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        userDbRef.child(userEncEmail).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> users = new ArrayList<>();
                blueAdapter.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    if (winningTeam.equals(myTeam)) {
                        int score = 0;
                        if (data.child("nVictories").getValue(Integer.class) != null)
                            score = data.child("nVictories").getValue(Integer.class);
                        score++;
                        userDbRef.child(userEncEmail).child("nVictories").setValue(score);
                    }
                    else{
                        int score = 0;
                        if (data.child("nLosses").getValue(Integer.class) != null)
                            score = data.child("nLosses").getValue(Integer.class);
                        score++;
                        userDbRef.child(userEncEmail).child("nLosses").setValue(score);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        GamePlayed.RESULT result;
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");
        DateTime gameBegin = formatter.parseDateTime(startDate);
        Period p =  new Period(new DateTime(), gameBegin);
        int time = p.getMinutes();
        Log.i("time", time+"");
        if (winningTeam.equals(myTeam)) {
            result = GamePlayed.RESULT.WON;
        }


        else {
            result = GamePlayed.RESULT.LOST;
        }
        userDbRef.child(userEncEmail).child("gamesPlayed").push().setValue(new GamePlayed(result, startDate, gameMode, time ,zone));

        //apagar o no do jogo
        //dbRef.setValue(null);
    }

}
