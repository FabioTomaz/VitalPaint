package com.icm.projeto.vitalpaint.Data;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.icm.projeto.vitalpaint.ProfileFragment;

import java.io.File;
import java.io.IOException;
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

    public List<String> getLocationsPlayed() {
        return locationsPlayed;
    }

    public void setLocationsPlayed(List<String> locationsPlayed) {
        this.locationsPlayed = locationsPlayed;
    }

    public Bitmap getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(Bitmap profilePic) {
        this.profilePic = profilePic;
    }

    public Bitmap getHeaderPic() {
        return headerPic;
    }

    public void setHeaderPic(Bitmap headerPic) {
        this.headerPic = headerPic;
    }

    private Bitmap profilePic;
    private Bitmap headerPic;
    private static final String PHOTOSFOLDER = "User Profile Photos";
    private static final String PROFILEFOLDER = "profilePic";
    private static final String HEADERFOLDER = "headerPic";

    public UserDataManager(String name, String userName, String email) {
        this.name = name;
        this.userName = userName;
        this.email = email;
        this.nMatchPlayed = 0;
        this.nVictories = 0;
        locationsPlayed = new ArrayList<>();
        profilePic = null;
        headerPic = null;
    }

    public UserDataManager(String name, String userName, String email, int nMatchPlayed, int nVictories) {
        this.name = name;
        this.userName = userName;
        this.email = email;
        this.nMatchPlayed = nMatchPlayed;
        this.nVictories = nVictories;
        locationsPlayed = new ArrayList<>();//pode ser-nos útil manter a ordem de inserção, mas não queremos elementos duplicados
        profilePic = null;
        headerPic = null;
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

    public UserData getLoggedUserDataFromEmail(String email) {
        final String EMAIL = email;

        DatabaseReference dbData = FirebaseDatabase.getInstance().getReference().child("Users");
        dbData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    user = data.getValue(com.icm.projeto.vitalpaint.Data.UserDataManager.class);
                    if (user.getEmail().equals(EMAIL)) {
                        user.setUserName(data.getKey());//encontrados dados do user, adicionar user name q esta na chave e nao nos valores
                        //colocar as fotos de capa do user
                        try {
                            user.setProfilePic(ProfileFragment.downloadProfilePic(user.getUserName()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            user.setHeaderPic(ProfileFragment.downloadHeaderPic(user.getUserName()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //colocar os dados do user na classe UserData para q estejam acessiveis no projeto
                        //Log.i("", data.getValue() + "");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return this.setLoggedUserData(user);

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
                for (DataSnapshot data : snapshot.getChildren()) {//percore os users ate encontrar o user com username correto
                    if (data.getKey().equals(userName)) {
                        bool[0]=true;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return bool[0];
    }
    //atualizar na database o nome de um user. Se for o user logado, a classe UserData tmb será atualizada
    public void updateName(final String username, final String name){
        final DatabaseReference dbData = FirebaseDatabase.getInstance().getReference().child("Users");
        dbData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                UserDataManager userDatam = new UserDataManager();
                for (DataSnapshot data : snapshot.getChildren()) {//percore os users ate encontrar o user com username correto
                    if (data.getKey().equals(username)) {
                        userDatam = data.getValue(com.icm.projeto.vitalpaint.Data.UserDataManager.class);//obter dados do user
                        userDatam.setName(name);//atualizar o nome
                        if(UserData.USERNAME.equals(username))
                            UserData.setNAME(name); //atualizar os dados do user logado na classe UserData
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put(username, userDatam);
                        dbData.updateChildren(map); //do nos Users, ele atualiza o no do username ja existente com os novos dados
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Log.i("outer", user + "");
    }
    /*//atualizar na database o username de um user. Se for o user logado, a classe UserData tmb será atualizada
    public boolean updateUserName(final String oldUsername, final String newUserName){
        if (!userNameExists(newUserName)) {

            final DatabaseReference dbData = FirebaseDatabase.getInstance().getReference().child("Users");
            dbData.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    UserDataManager userDatam = new UserDataManager();
                    for (DataSnapshot data : snapshot.getChildren()) {//percore os users ate encontrar o user com username correto
                        if (data.getKey().equals(oldUsername)) {
                            userDatam = data.getValue(com.icm.projeto.vitalpaint.Data.UserDataManager.class);//obter dados do user
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put(newUserName, userDatam);
                            dbData.updateChildren(map);//cria um novo nó com o novo username novo e todos os dados do user.
                            //remover o no do username antigo
                            dbData.child(oldUsername).removeValue(); //dbData.child(oldUsername).setValue(null); //tmb funciona
                            if(UserData.USERNAME.equals(oldUsername)){
                                UserData.setUSERNAME(newUserName);
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {  }
            });
            //Log.i("", user + "");
            return true;
        }
        else{
            return false;
        }
    }*/

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

    public void uploadProfilePic(String username, Uri uri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("User Profile Photos/"+username+"profilePic/");
        storageRef.putFile(uri);
    }
    public void uploadHeaderPic(String username, Uri uri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("User Profile Photos/"+username+"headerPic/");
        storageRef.putFile(uri);
    }

    public static Bitmap convertToBitMap(File file){
        return BitmapFactory.decodeFile(file.getAbsolutePath());
    }
    public static UserData setLoggedUserData(UserDataManager user){
        UserData us = new UserData( user.getName(), user.getUserName(), user.getEmail() );
        us.nVictories = user.getnVictories();
        us.nMatchPlayed = user.getnMatchPlayed();
        us.profilePic = user.getProfilePic();
        us.headerPic = user.getHeaderPic();
        us.locationsPlayed = user.getLocationsPlayed();
        return us;
    }


}

