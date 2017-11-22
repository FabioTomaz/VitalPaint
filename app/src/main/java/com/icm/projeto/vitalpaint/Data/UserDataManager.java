package com.icm.projeto.vitalpaint.Data;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//classe para ler/escrever dados de utilizadores da database
@SuppressLint("ParcelCreator")
public class UserDataManager  implements Serializable, Parcelable{
    private static final long serialVersionUID = 1L;
    private final String email;
    private DatabaseReference dbData;
    private List<UserDataListener> list;

    public UserDataManager(String email) {
        list = new ArrayList<UserDataListener>();
        this.email = email;
    }

    public interface UserDataListener extends Serializable{
        public void onReceiveUserData(int requestType, UserData user, Bitmap profilePic, Bitmap headerPic);
    }

    public void addListener(UserDataListener userListener, int requestType) {
        list.add(userListener);
        userDataFromEmailListener(requestType);
    }

    public void uploadUserData(UserData userData) {
        dbData = FirebaseDatabase.getInstance().getReference().child("Users").child(encodeUserEmail(email));//aceder ao nó Users, que guarda os usuários
        dbData.setValue(userData);
    }
    public void addFriend(String friendEmail){
        dbData = FirebaseDatabase.getInstance().getReference().child("Users").child(encodeUserEmail(email)).child("friends").child(encodeUserEmail(friendEmail));
        dbData.setValue(friendEmail);
    }
    public void addLocation(Location locationsPlayed){
        dbData = FirebaseDatabase.getInstance().getReference().child("Users").child(encodeUserEmail(email)).child("locationsPlayed").push();
        dbData.setValue(locationsPlayed);
    }


    private void userDataFromEmailListener(final int requestType) {
        DatabaseReference dbData = FirebaseDatabase.getInstance().getReference().child("Users").child(encodeUserEmail(email));
        dbData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                UserData userData = new UserData();
                List<UserData> listOfFriends = new ArrayList<>();
                for (DataSnapshot emp : snapshot.child("friends").getChildren()) {
                    UserData friendData = new UserData();
                    userData.setEMAIL(emp.child("email").getValue(String.class));
                    userData.setNAME(emp.child("name").getValue(String.class));
                    listOfFriends.add(friendData);
                }
                userData.setFriends(listOfFriends);
                userData.setEMAIL(snapshot.child("email").getValue(String.class));
                userData.setNAME(snapshot.child("name").getValue(String.class));
                userData.setSHORTBIO(snapshot.child("shotbio").getValue(String.class));
                userData.setnMatchPlayed(snapshot.child("nMatchPlayed").getValue(Integer.class));
                userData.setnVictories(snapshot.child("nVictories").getValue(Integer.class));
                final UserData finalUserData = userData;

                StorageReference storageRef = FirebaseStorage.getInstance().getReference("User Profile Photos/"+email+"/profilePic");
                try {
                    final File  localFile = File.createTempFile("profile", "jpg");
                    final Bitmap[] image = {null};
                    storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            final Bitmap profilePic = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            StorageReference storageRef = FirebaseStorage.getInstance().getReference("User Profile Photos/"+email+"/headerPic");
                            try {
                            final File localFile = File.createTempFile("profile", "jpg");
                            final Bitmap[] image = {null};
                            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    for (int i = 0; i < list.size(); i++) {
                                        list.get(i).onReceiveUserData(requestType, finalUserData, profilePic, BitmapFactory.decodeFile(localFile.getAbsolutePath()));
                                    }
                                }
                            });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public static String encodeUserEmail(String email) {
        return email.replace(".", ",");
    }

    public static String decodeUserEmail(String email) {
        return email.replace(".", ",");
    }

    @Override
    public int describeContents() {return 0;}

    @Override
    public void writeToParcel(Parcel dest, int flags) {}
}

    /*public static boolean userNameExists(final String userName) {//nao esta a funcionar..
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
    }*/


//atualizar na database o nome de um user. Se for o user logado, a classe UserData tmb será atualizada
    /*public void updateName(final String username, final String name){
        final DatabaseReference dbData = FirebaseDatabase.getInstance().getReference().child("Users");
        dbData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                UserData userData = new UserData();
                for (DataSnapshot data : snapshot.getChildren()) {//percore os users ate encontrar o user com username correto
                    if (data.getKey().equals(username)) {
                        userData = data.getValue(com.icm.projeto.vitalpaint.Data.UserData.class);//obter dados do user
                        userData.setNAME(name);//atualizar o nome
                        if(UserData.loggedUser.getUSERNAME().equals(username))
                            UserData.loggedUser.setNAME(name); //atualizar os dados do user logado na classe UserData
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put(username, userData);
                        dbData.updateChildren(map); //do nos Users, ele atualiza o no do username ja existente com os novos dados
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }*/


