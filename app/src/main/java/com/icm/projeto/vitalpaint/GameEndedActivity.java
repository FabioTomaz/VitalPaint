package com.icm.projeto.vitalpaint;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
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
import com.icm.projeto.vitalpaint.Data.UserData;
import com.icm.projeto.vitalpaint.Data.UserDataManager;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

public class GameEndedActivity extends AppCompatActivity implements UserDataManager.UserDataListener{
    private String startDate;
    private String winningTeam;
    private String myTeam;
    private String gameName;
    private TextView winnerTeamtxt;
    private LinearLayout winnerLayout;
    private DatabaseReference dbRef;
    private List<String> blueTeamPlayers;
    private List<String> redTeamPlayers;
    private ArrayAdapter blueAdapter;
    private ArrayAdapter redAdapter;
    private ListView blueTeamListView;
    private ListView redTeamListView;
    private FirebaseAuth auth;
    private UserDataManager userDataManager;
    //private GameMode gameMode;
    private String zone;
    public static final int PROFILE_DATA = 1;


    //@SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_ended);
        startDate = getIntent().getStringExtra("startDate");
        myTeam = getIntent().getStringExtra("myTeam");
        winningTeam = getIntent().getStringExtra("winnerTeam");
        gameName = getIntent().getStringExtra("gameName");
        zone = getIntent().getStringExtra("zone");
        //gameMode = GameMode.valueOf(getIntent().getStringExtra("gameMode")); //obter  a string do enum e converter para enum
        Log.i("GAMEEND", myTeam.toString());
        winnerTeamtxt = (TextView) findViewById(R.id.winner_team_txt);
        winnerLayout = (LinearLayout) findViewById(R.id.winnerlayout);
        winnerTeamtxt.setText("A "+winningTeam+" Ganhou!");
        /*if (winningTeam.equals("Equipa Azul"))
            winnerLayout.setBackgroundColor(getResources().getColor(R.color.blueTeamColor));
        else
            winnerLayout.setBackgroundColor(getResources().getColor(R.color.redTeamColor));*/

        //listviews
        blueTeamPlayers = new ArrayList<>();
        redTeamPlayers = new ArrayList<>();
        blueTeamListView = (ListView) findViewById(R.id.list_blue_team);
        redTeamListView = (ListView) findViewById(R.id.list_red_team);
        blueAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        redAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        blueTeamListView.setAdapter(blueAdapter);
        redTeamListView.setAdapter(redAdapter);

        auth = FirebaseAuth.getInstance();
        //get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userDataManager = new UserDataManager(user.getEmail());
        userDataManager.addListener(this);
        userDataManager.userDataFromEmailListener(PROFILE_DATA);

        dbRef = FirebaseDatabase.getInstance().getReference("Games").child(gameName);
        dbRef.child("Equipa Azul").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> users = new ArrayList<>();
                blueAdapter.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    if(!data.getKey().equals("score")) {
                        blueAdapter.add(data.child("name").getValue());
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
                blueAdapter.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    if(!data.getKey().equals("score")) {
                        redAdapter.add(data.child("name").getValue());
                    }
                }
                redAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onReceiveUserData(int requestType, UserData user, Bitmap profilePic, Bitmap headerPic) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users").child(UserDataManager.encodeUserEmail(user.getEMAIL()));
        GamePlayed.RESULT result;
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");
        DateTime gameBegin = formatter.parseDateTime(startDate);
        Period p =  new Period(new DateTime(), gameBegin);
        int time = p.getMinutes();
        Log.i("time", time+"");
        if (winningTeam.equals(myTeam)) {
            db.child("nVictories").setValue(user.getnVictories()+1);
            result = GamePlayed.RESULT.WON;
        }
        else {
            db.child("nLosses").setValue(user.getnVictories() + 1);
            result = GamePlayed.RESULT.LOST;
        }
        db.child("gamesPlayed").push().setValue(new GamePlayed(result, startDate, time ,zone));
    }
}
