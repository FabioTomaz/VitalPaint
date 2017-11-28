package com.icm.projeto.vitalpaint;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GameEndedActivity extends AppCompatActivity {
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


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_ended);
        startDate = getIntent().getStringExtra("startDate");
        myTeam = getIntent().getStringExtra("myTeam");
        winningTeam = getIntent().getStringExtra("winnerTeam");
        gameName = getIntent().getStringExtra("gameName");
        Log.i("GAMEEND", myTeam.toString());
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View view = super.onCreateView(name, context, attrs);
        winnerTeamtxt = (TextView) findViewById(R.id.winner_team_txt);
        winnerLayout = (LinearLayout) findViewById(R.id.winnerlayout);
        winnerTeamtxt.setText("A "+winningTeam+" Ganhou!");
        if (winningTeam.equals("Equipa Azul"))
            winnerLayout.setBackgroundColor(getResources().getColor(R.color.blueTeamColor));
        else
            winnerLayout.setBackgroundColor(getResources().getColor(R.color.redTeamColor));

        //listviews
        blueTeamPlayers = new ArrayList<>();
        redTeamPlayers = new ArrayList<>();
        blueTeamListView = (ListView) findViewById(R.id.list_blue_team);
        redTeamListView = (ListView) findViewById(R.id.list_red_team);
        blueAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        redAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        blueTeamListView.setAdapter(blueAdapter);
        redTeamListView.setAdapter(redAdapter);

        dbRef = dbRef = FirebaseDatabase.getInstance().getReference("Games").child(gameName);
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
        return super.onCreateView(name, context, attrs);
    }
}
