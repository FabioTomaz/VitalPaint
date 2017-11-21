package com.icm.projeto.vitalpaint.Data;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
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
import java.util.concurrent.ExecutionException;

//classe para ler/escrever dados de utilizadores da database
public class UserDataManager  implements Serializable{
    private final String email;
    private DatabaseReference dbData;
    private List<UserDataListener> list;
    private List<UserProfilePicListener> profilePicListeners;
    private List<UserHeaderPicListener> headerPicListeners;

    public UserDataManager(String email) {
        list = new ArrayList<UserDataListener>();
        this.email = email;
    }

    public interface UserDataListener{
        public void onReceiveUserData(UserData user);
    }

    public interface UserHeaderPicListener{
        public void onReceiveUserHeaderPic(Bitmap user);
    }

    public interface UserProfilePicListener{
        public void onReceiveUserProfilePic(Bitmap user);
    }

    public void addListener(UserDataListener userListener) {
        list.add(userListener);
        userDataFromEmailListener();
    }

    public void addProfilePicListener(UserProfilePicListener userListener) {
        profilePicListeners.add(userListener);
        try {
            profilePicListener();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addHeaderPicListener(UserHeaderPicListener userListener) {
        headerPicListeners.add(userListener);
        try {
            headerPicListener();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void uploadUserData(UserData userData) {
        dbData = FirebaseDatabase.getInstance().getReference().child("Users").child(encodeUserEmail(email));//aceder ao nó Users, que guarda os usuários
        dbData.setValue(userData);
    }

    private void userDataFromEmailListener() {
        DatabaseReference dbData = FirebaseDatabase.getInstance().getReference().child("Users");
        dbData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    if (data.getKey().equals(encodeUserEmail(email))){
                        for(int i = 0; i<list.size(); i++) {
                            UserData user = data.getValue(UserData.class);
                            list.get(i).onReceiveUserData(user);
                        }
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }


    public void profilePicListener() throws IOException {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("User Profile Photos/"+email+"/profilePic/");
        final File localFile = File.createTempFile("profile", "jpg");
        final Bitmap[] image = {null};
        storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                for(int i = 0; i<profilePicListeners.size(); i++) {
                    profilePicListeners.get(i).onReceiveUserProfilePic(BitmapFactory.decodeFile(localFile.getAbsolutePath()));
                }
            }
        });
    }

    public void headerPicListener() throws IOException {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("User Profile Photos/"+email+"/headerPic/");
        final File localFile = File.createTempFile("profile", "jpg");
        final Bitmap[] image = {null};
        storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                for(int i = 0; i<headerPicListeners.size(); i++) {
                    headerPicListeners.get(i).onReceiveUserHeaderPic(BitmapFactory.decodeFile(localFile.getAbsolutePath()));
                }
            }
        });
    }

    public String encodeUserEmail(String email) {
        return email.replace(".", ",");
    }

    public String decodeUserEmail(String email) {
        return email.replace(".", ",");
    }
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


