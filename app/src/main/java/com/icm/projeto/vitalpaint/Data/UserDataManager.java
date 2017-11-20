package com.icm.projeto.vitalpaint.Data;


import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;

/**
 * Created by Bruno Silva on 14/11/2017.
 */
//classe para ler/escrever dados de utilizadores da database
public class UserDataManager {
    private com.icm.projeto.vitalpaint.Data.UserData loggedUser;
    private DatabaseReference dbData;
    private static final String PHOTOSFOLDER = "User Profile Photos";
    private static final String PROFILEFOLDER = "profilePic";
    private static final String HEADERFOLDER = "headerPic";

    public UserDataManager(){
        dbData = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    public String encodeUserEmail(String email){
        return email.replace(".",",");
    }

    public String decodeUserEmail(String email){
        return email.replace(".",",");
    }

    public void uploadUserData(String email, UserData userData) {
        dbData = FirebaseDatabase.getInstance().getReference().child("Users").child(encodeUserEmail(email));//aceder ao nó Users, que guarda os usuários
        dbData.setValue(userData);
    }
    //https://stackoverflow.com/questions/37031222/firebase-add-new-child-with-specified-name

    public void getLoggedUserFromEmail(String email) {
        final TaskCompletionSource<UserData> tcs = new TaskCompletionSource<>();
        dbData.child(encodeUserEmail(email)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                tcs.setResult(snapshot.getValue(UserData.class));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                tcs.setException(databaseError.toException());
            }
        });
        Task<UserData> t = tcs.getTask();
        try {
            Tasks.await(t);
        } catch (ExecutionException | InterruptedException e) {
            t = Tasks.forException(e);
        }

        if(t.isSuccessful()) {
            UserData.loggedUser = t.getResult();
        }
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
}

