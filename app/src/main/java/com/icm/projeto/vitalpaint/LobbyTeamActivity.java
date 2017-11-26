package com.icm.projeto.vitalpaint;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.icm.projeto.vitalpaint.Data.GameData;
import com.icm.projeto.vitalpaint.Data.GameDataManager;
import com.icm.projeto.vitalpaint.Data.GameMode;
import com.icm.projeto.vitalpaint.Data.UserData;
import com.icm.projeto.vitalpaint.Data.UserDataManager;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class LobbyTeamActivity extends AppCompatActivity implements UserDataManager.UserDataListener {
    private String gameName;
    private GameMode gameMode;
    private boolean isHost;
    private GameDataManager dbManager;
    private GameData gameData;
    private String myTeam;
    private Map<String, Double> coordinates;
    private String startDate;
    private int duration;
    private UserDataManager userDataManager;
    private String loggedUserName;
    private FirebaseAuth auth;
    private ListView blueTeamListView;
    private ListView redTeamListView;
    private Handler handler;
    private Runnable myRunnable;
    private Timer timer;

    List<String> blueTeamPlayers;
    List<String> redTeamPlayers;
    private ArrayAdapter blueAdapter;
    private ArrayAdapter redAdapter;
    private Button joinRed;
    private Button joinBlue;
    private FloatingActionButton exitTeam;
    private DatabaseReference blueTeam;
    private DatabaseReference redTeam;
    private DatabaseReference game;
    private final String ROOTNODE = "Games";
    public static final int PROFILE_DATA = 1;
    private ProgressBar progressBarCircle;
    private TextView textViewTime;
    private long diff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_lobby);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//nao bloquear o ecra
        loggedUserName = "";
        auth = FirebaseAuth.getInstance();
        userDataManager = new UserDataManager(auth.getCurrentUser().getEmail());
        userDataManager.addListener(this);
        userDataManager.userDataFromEmailListener(PROFILE_DATA);
        //criar listener para obter dados do user loggado

        progressBarCircle = (ProgressBar) findViewById(R.id.progressBarCircle);
        textViewTime = (TextView) findViewById(R.id.textViewTime);
        gameName = getIntent().getStringExtra("gameName");
        isHost = getIntent().getBooleanExtra ("isHost", false);
        gameMode = GameMode.valueOf(getIntent().getStringExtra("gameMode")); //obter  a string do enum e converter para enum
        startDate = getIntent().getStringExtra("startDate");
        duration = getIntent().getIntExtra("duration", 0);
        //duration = new Period(startDate, endDate);
        this.setTitle(gameName);
        coordinates = new HashMap<>();
        coordinates.put("lat", 0.0);
        coordinates.put("longt", 0.0);

        joinBlue = (Button) findViewById(R.id.enter_blue_team);
        joinRed = (Button) findViewById(R.id.enter_red_team);
        exitTeam = (FloatingActionButton) findViewById(R.id.exit_team);

        blueTeamPlayers = new ArrayList<>();
        redTeamPlayers = new ArrayList<>();
        gameData = new GameData(gameName, gameMode, startDate, duration);

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

        //Log.i("im host", "he");
        if (isHost) {
            Map<String, Object> map = new HashMap<>();
            map.put(gameName, gameData);
            game.updateChildren(map);
            FirebaseDatabase.getInstance().getReference(ROOTNODE).child(gameName).child("Equipa Azul").child("score").setValue(0);
            FirebaseDatabase.getInstance().getReference(ROOTNODE).child(gameName).child("Equipa Vermelha").child("score").setValue(0);
        }
        //juntar a equipa azul
        joinBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myTeam = "Equipa Azul";
                addUserToTeam(myTeam);
                joinBlue.setEnabled(false);
                joinRed.setEnabled(false);
                // mostrar botao para sair da equipa
                exitTeam.setVisibility(View.VISIBLE);
            }
        });
        // juntar a equipa vermelha
        joinRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {;
                myTeam = "Equipa Vermelha";
                addUserToTeam(myTeam);
                joinBlue.setEnabled(false);
                joinRed.setEnabled(false);
                // mostrar botao para sair da equipa
                exitTeam.setVisibility(View.VISIBLE);
            }
        });

        exitTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> player = new HashMap<>();
                player.put(loggedUserName, coordinates);
                redTeam.child(loggedUserName).setValue(null);
                //mostrar de novo botoes para juntar as equipas
                myTeam = "";
                blueTeam.child(loggedUserName).setValue(null);
                joinBlue.setEnabled(true);
                joinRed.setEnabled(true);
                // ocultar botao para sair da equipa
                exitTeam.setVisibility(View.GONE);

            }
        });

        //quando alguem sai/entra na equipa azul
        blueTeam.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String textOnLobbyBoard = "blue_team_player";
                List<String> users = new ArrayList<>();
                blueAdapter.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    if(!data.getKey().equals("score")) {
                        blueAdapter.add(data.child("name").getValue());
                    }
                }
                blueAdapter.notifyDataSetChanged();
                //adicionar os users na listview
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        //quando alguem sai/entra na equipa vermelha
        redTeam.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /*String textOnLobbyBoard = "blue_team_player";
                TextView textv = null;
                List<String> users = new ArrayList<>();*/
                redAdapter.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    if(!data.getKey().equals("score")) {
                        redAdapter.add(data.child("name").getValue());
                    }
                }
                blueAdapter.notifyDataSetChanged();
                //adicionar os users na listview
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    public void addUserToTeam(String team){
        FirebaseDatabase.getInstance().getReference().child("Games").child(gameName).child(team).child(UserDataManager.encodeUserEmail(auth.getCurrentUser().getEmail())).child("name").setValue(loggedUserName);
    }
    //sempre q a atividade entra no estado OnResume, iniciar um timer ate ao inicio da partida.
    //a partida n deve iniciar se a atividade estiver em background, ou for destruida, etc

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
    }

    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scheduleGame();
        CountDownTimer countDownTimer = new CountDownTimer(diff, 1000) {

            public void onTick(long millisUntilFinished) {
                progressBarCircle.setMax((int) millisUntilFinished / 1000);
                progressBarCircle.setProgress((int) millisUntilFinished / 1000);
                textViewTime.setText("A partida começa em: " + millisUntilFinished / 1000);
            }

            public void onFinish() {

                textViewTime.setText("Começar!");

            }
        };
        countDownTimer.start();
    }

    @Override
    public void onReceiveUserData(int requestType , UserData user, Bitmap profilePic, Bitmap headerPic) {
        loggedUserName = user.getNAME();
    }

    private void scheduleGame(){
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");
        DateTime dtStart = formatter.parseDateTime(startDate);
        DateTime dtCurrent = DateTime.now();
        diff = dtStart.getMillis() - dtCurrent.getMillis() ;
        Log.i("diff", diff+"");
        timer = new Timer();
        timer.schedule(new TimerTask(){
            public void run() {
                finish();
                LobbyTeamActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Intent intent = new Intent(LobbyTeamActivity.this, GameMapActivity.class);
                        Log.i("myteam", myTeam+"");
                        intent.putExtra("myTeam", myTeam);
                        intent.putExtra("gameName", gameName);
                        intent.putExtra("userName", loggedUserName);
                        intent.putExtra("duration", duration);
                        startActivity(intent);
                    }
                });
            }
        }, diff);
    }

    //se o user sair do lobby, entao retirá-lo da equipa, caso se tenha juntado a alguma
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if ( myTeam != null && myTeam.equals(""))
                game.child(gameName).child(myTeam).child(loggedUserName).setValue(null);
        }
        return super.onKeyDown(keyCode, event);
    }


}