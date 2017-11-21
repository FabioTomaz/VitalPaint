package com.icm.projeto.vitalpaint;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.icm.projeto.vitalpaint.Data.UserData;
import com.icm.projeto.vitalpaint.Data.UserDataManager;

import java.io.File;

public class PlayActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, UserDataManager.UserDataListener, UserDataManager.UserHeaderPicListener, UserDataManager.UserProfilePicListener {
    private View headerView;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private UserDataManager userDataManager;
    NavigationView navigationView;
    Toolbar toolbar;

    public String encodeUserEmail(String email) {
        return email.replace(".", ",");
    }

    public String decodeUserEmail(String email) {
        return email.replace(".", ",");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        headerView = navigationView.getHeaderView(0);
        //get firebase auth instance
        auth = FirebaseAuth.getInstance();
        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(PlayActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
        auth.addAuthStateListener(authListener);
        userDataManager = new UserDataManager(user.getEmail());
        userDataManager.addListener(this);
        if (savedInstanceState == null) {
            Fragment fragment = new CreateGameFragment(); // <-------
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

    }

    /*private void downloadProfilePic(String email) throws IOException {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        final File localFile = File.createTempFile("profile", "jpg");
        final Bitmap image = null;
        storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                loadImage(ProfileFragment.convertToBitMap(localFile));
            }
        });
        UserData.loggedUser.profilePic = image;
    }*/

    public static Bitmap convertToBitMap(File file){
        return BitmapFactory.decodeFile(file.getAbsolutePath());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(id);
        if (id == R.id.nav_create_game) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame,  new CreateGameFragment());
            ft.commit();
        } else if (id == R.id.nav_join_game) {
            startActivity(new Intent(PlayActivity.this, GameMapActivity.class));
        } else if (id == R.id.nav_profile) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame,  ProfileFragment.newInstance(userDataManager));
            ft.commit();
        } else if (id == R.id.nav_friends) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame,  new FriendsFragment());
            ft.commit();
        } else if (id == R.id.nav_game_history) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame,  new GameHistoryFragment());
            ft.commit();
        } else if (id == R.id.nav_account) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame,  new AccountFragment());
            ft.commit();
        } else if (id == R.id.nav_logout) {
            auth.signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    @Override
    public void onReceiveUserData(UserData userData){
        TextView drawerUsername = (TextView) headerView.findViewById(R.id.navBarUsername);
        drawerUsername.setText(userData.getNAME());
    }

    @Override
    public void onReceiveUserHeaderPic(Bitmap user) {
        headerView.setBackground(new BitmapDrawable(getResources(), user));
    }

    @Override
    public void onReceiveUserProfilePic(Bitmap user) {
        ImageView drawerImage = (ImageView) headerView.findViewById(R.id.imageView);
        drawerImage.setImageBitmap(user);
    }
}
