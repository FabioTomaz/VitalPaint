package com.icm.projeto.vitalpaint;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainMenuActivity extends AppCompatActivity {

    private Button createButton;
    private Button joinButton;
    private Button myProfileButton;
    private Button accountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        createButton = (Button) findViewById(R.id.btn_create);
        joinButton = (Button) findViewById(R.id.btn_create);
        myProfileButton = (Button) findViewById(R.id.btn_profile);
        accountButton = (Button) findViewById(R.id.btn_account_settings);


        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MainMenuActivity.this, JoinActivity.class));
            }
        });

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MainMenuActivity.this, CreateActivity.class));
            }
        });

        myProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MainMenuActivity.this, ProfileActivity.class));
            }
        });

        accountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainMenuActivity.this, AccountActivity.class));
            }
        });
    }
}
