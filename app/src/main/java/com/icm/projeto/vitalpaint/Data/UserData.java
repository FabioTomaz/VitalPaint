package com.icm.projeto.vitalpaint.Data;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Bruno Silva on 14/11/2017.
 */
//excelente tutorial de firebase  database:
//   https://www.learnhowtoprogram.com/android/data-persistence/firebase-firebase-structure-and-further-setup
public class UserData {
    public static String NAME;
    public static String USERNAME;
    public static String EMAIL;
    private int nMatchPlayed;
    private int nVictories;
    private UserData user;
    List<String> locationsPlayed;
    private DatabaseReference dbData;

    public UserData(String name, String userName, String email){
        this.NAME = name;
        this.USERNAME = userName;
        this.EMAIL = email;
        this.nMatchPlayed = 0;
        this.nVictories = 0;
        locationsPlayed = new ArrayList<>();//pode ser-nos útil manter a ordem de inserção, mas não queremos elementos duplicados
    }

    public UserData(String name, String userName, String email, int nMatchPlayed, int nVictories){
        this.NAME = name;
        this.USERNAME = userName;
        this.EMAIL = email;
        this.nMatchPlayed = nMatchPlayed;
        this.nVictories = nVictories;
        locationsPlayed = new ArrayList<>();//pode ser-nos útil manter a ordem de inserção, mas não queremos elementos duplicados
    }

    public UserData(){

    }

    public String getName() {
        return NAME;
    }

    public void setName(String name) {
        this.NAME = name;
    }

    public String getEmail() {
        return EMAIL;
    }

    public void setEmail(String email) {
        this.EMAIL = email;
    }

    public List<String> getLocationsPlayed() {
        return locationsPlayed;
    }

    public void setLocationsPlayed(List<String> locationsPlayed) {
        this.locationsPlayed = locationsPlayed;
    }

    public String getUserName() {
        return USERNAME;
    }

    public void setUserName(String userName) {
        this.USERNAME = userName;
    }

    public int getnMatchPlayed() {
        return nMatchPlayed;
    }

    public void setnMatchPlayed(int nMatchPlayed) {
        this.nMatchPlayed = nMatchPlayed;
    }

    public int getnVictories() {
        return nVictories;
    }

    public void setnVictories(int nVictories) {
        this.nVictories = nVictories;
    }

    public void uploadUserData(){
        dbData = FirebaseDatabase.getInstance().getReference().child("Users");//aceder ao nó Users, que guarda os usuários
        //nota:
        //'.', '#', '$', '[', or ']' não podem estar num path de firebase, logo o username n pode ter estes carateres

        Map<String, Map<String, String>> newUserNode= new HashMap(); //nó com a chave username e um mapa com os dados do user
        Map<String, Object> profile = new HashMap<>();
        /*profile.put("Name", NAME);
        profile.put("Email", EMAIL);
        profile.put("Number of Played Matches", nMatchPlayed+"");
        profile.put("Matches Won", nVictories+"");*/
        profile.put(USERNAME, new UserData(NAME, null, EMAIL));
        //newUserNode.put(USERNAME, profile);
        //dbData.child(USERNAME); //introduzir novo nó filho, com o username do user e referenciar o no acabado de criar
        dbData.updateChildren(profile);
    }
    //https://stackoverflow.com/questions/37031222/firebase-add-new-child-with-specified-name

    public UserData getUserDataFromEmail(String email) {
        final String EMAIL = email;

        dbData = FirebaseDatabase.getInstance().getReference().child("Users");
        dbData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for(DataSnapshot data : snapshot.getChildren()){
                    user = data.getValue(UserData.class);
                    if(user.getEmail().equals(EMAIL)){
                        user.setUserName(data.getKey());//encontrados dados do user
                        Log.i("", data.getValue()+"");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Log.i("outer", user+"");
        return user;
    }

    @Override
    public String toString() {
        return "UserData{" +
                "nMatchPlayed=" + nMatchPlayed +
                ", nVictories=" + nVictories +
                ", locationsPlayed=" + locationsPlayed +
                '}';
    }

    public static boolean userNameExists(final String userName){//nao esta a funcionar..
        final boolean[] bool = {false};

        final DatabaseReference dbData = FirebaseDatabase.getInstance().getReference().child("Users");
        dbData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(userName)) {
                    bool[0] = true;
                }
                else{
                    bool[0] = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return bool[0];
    }



}
