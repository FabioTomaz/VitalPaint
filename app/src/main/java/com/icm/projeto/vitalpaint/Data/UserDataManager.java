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
//classe para ler/escrever dados de utilizadores da database
public class UserDataManager {
    private String name;
    private String userName;
    private String email;
    private int nMatchPlayed;
    private int nVictories;
    private com.icm.projeto.vitalpaint.Data.UserData loggedUser;
    List<String> locationsPlayed;
    private DatabaseReference dbData;
    private UserDataManager user;

    public UserDataManager(String name, String userName, String email) {
        this.name = name;
        this.userName = userName;
        this.email = email;
        this.nMatchPlayed = 0;
        this.nVictories = 0;
        locationsPlayed = new ArrayList<>();
    }

    public UserDataManager(String name, String userName, String email, int nMatchPlayed, int nVictories) {
        this.name = name;
        this.userName = userName;
        this.email = email;
        this.nMatchPlayed = nMatchPlayed;
        this.nVictories = nVictories;
        locationsPlayed = new ArrayList<>();//pode ser-nos útil manter a ordem de inserção, mas não queremos elementos duplicados
    }

    public UserDataManager() {

    }


    public void uploadUserData() {
        dbData = FirebaseDatabase.getInstance().getReference().child("Users");//aceder ao nó Users, que guarda os usuários
        //nota:
        //'.', '#', '$', '[', or ']' não podem estar num path de firebase, logo o username n pode ter estes carateres

        Map<String, Map<String, String>> newUserNode = new HashMap(); //nó com a chave username e um mapa com os dados do user
        Map<String, Object> profile = new HashMap<>();
        /*profile.put("Name", NAME);
        profile.put("Email", EMAIL);
        profile.put("Number of Played Matches", nMatchPlayed+"");
        profile.put("Matches Won", nVictories+"");*/
        profile.put(userName, new UserDataManager(name, null, email));
        //newUserNode.put(USERNAME, profile);
        //dbData.child(USERNAME); //introduzir novo nó filho, com o username do user e referenciar o no acabado de criar
        dbData.updateChildren(profile);
    }
    //https://stackoverflow.com/questions/37031222/firebase-add-new-child-with-specified-name

    public com.icm.projeto.vitalpaint.Data.UserData getLoggedUserDataFromEmail(String email) {
        final String EMAIL = email;

        dbData = FirebaseDatabase.getInstance().getReference().child("Users");
        dbData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    loggedUser = data.getValue(com.icm.projeto.vitalpaint.Data.UserData.class);
                    if (loggedUser.EMAIL.equals(EMAIL)) {
                        loggedUser.setUSERNAME(data.getKey());//encontrados dados do user, adicionar user name q esta na chave e nao nos valores
                        Log.i("", data.getValue() + "");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Log.i("outer", user + "");
        return loggedUser;
    }

    public UserDataManager getUserDataFromEmail(String email) {
        final String EMAIL = email;

        dbData = FirebaseDatabase.getInstance().getReference().child("Users");
        dbData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                UserDataManager userData = new UserDataManager();
                for (DataSnapshot data : snapshot.getChildren()) {
                    userData = data.getValue(com.icm.projeto.vitalpaint.Data.UserDataManager.class);
                    if (userData.getEmail().equals(EMAIL)) {
                        userData.setUserName(data.getKey());//encontrados dados do user
                        Log.i("", data.getValue() + "");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Log.i("outer", user + "");
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

    public static boolean userNameExists(final String userName) {//nao esta a funcionar..
        final boolean[] bool = {false};

        final DatabaseReference dbData = FirebaseDatabase.getInstance().getReference().child("Users");
        dbData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(userName)) {
                    bool[0] = true;
                } else {
                    bool[0] = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return bool[0];
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
}
