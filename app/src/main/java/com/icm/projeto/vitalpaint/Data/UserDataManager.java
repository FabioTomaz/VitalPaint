package com.icm.projeto.vitalpaint.Data;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.ExecutionException;

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

    public Context getContext() {
        return context;
    }

    private Context context;

    public UserDataManager(Context context) {
        this.context = context;
    }

    public UserDataManager() {

    }

    public String encodeUserEmail(String email) {
        return email.replace(".", ",");
    }

    public String decodeUserEmail(String email) {
        return email.replace(".", ",");
    }

    public void uploadUserData(String email, UserData userData) {
        dbData = FirebaseDatabase.getInstance().getReference().child("Users").child(encodeUserEmail(email));//aceder ao nó Users, que guarda os usuários
        dbData.setValue(userData);
    }
    //https://stackoverflow.com/questions/37031222/firebase-add-new-child-with-specified-name

    public void getLoggedUserFromEmail(String email) {
        this.mCheckInforInServer(this.encodeUserEmail(email));
    }

    public void mCheckInforInServer(String email) {
        this.fetchData(email, new OnGetDataListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(UserData data) {
                //DO SOME THING WHEN GET DATA SUCCESS HERE

            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                //DO SOME THING WHEN GET DATA FAILED HERE
            }
        });
    }


    public void fetchData(String email, final OnGetDataListener listener) {
        listener.onStart();
        final String EMAIL = (email);
        final UserData[] userData = new UserData[]{new UserData()};
        dbData = FirebaseDatabase.getInstance().getReference().child("Users");
        dbData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Log.i("key", data.getKey() + "");
                    if (data.getKey().equals(EMAIL))
                        userData[0].loggedUser = data.getValue(UserData.class);
                    Log.i("USEROBTAINED", UserData.loggedUser.toString() + "");
                    listener.onSuccess(userData[0]);
                    break;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailed(databaseError);
            }
        });
    }

    private class AsyncFetchUserData extends AsyncTask<String, Void, UserData> {
       /* private Context context;
        public AsyncFetchUserData(Context context){
            this.context = context;
        }
        ProgressDialog pdLoading = new ProgressDialog(context);*/

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            //pdLoading.setMessage("\tLoading User Data...");
            //pdLoading.show();
        }

        @Override
        protected UserData doInBackground(String... params) {
            /*final TaskCompletionSource<UserData> tcs = new TaskCompletionSource<>();
            dbData.child(encodeUserEmail(params[0])).addListenerForSingleValueEvent(new ValueEventListener() {
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
            UserData userData = null;
            if(t.isSuccessful()) {
                userData = t.getResult();
            }
            return userData;*/

            final TaskCompletionSource<UserData> tcs = new TaskCompletionSource<>();

            final String EMAIL = (params[0]);
            final UserData[] userData = new UserData[]{new UserData()};
            dbData = FirebaseDatabase.getInstance().getReference().child("Users");
            dbData.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        Log.i("key", data.getKey() + "");
                        if (data.getKey().equals(EMAIL))
                            userData[0].loggedUser = data.getValue(UserData.class);
                        Log.i("USEROBTAINED", UserData.loggedUser.toString() + "");
                        tcs.setResult(userData[0]);
                        break;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

            Task<UserData> t = tcs.getTask();

            try {
                Tasks.await(t);
            } catch (ExecutionException | InterruptedException e) {
                t = Tasks.forException(e);
            }

            if (t.isSuccessful()) {
                UserData result = t.getResult();
            }
            return userData[0];

        }

        @Override
        protected void onPostExecute(UserData result) {
            //super.onPostExecute(result);

            //this method will be running on UI thread

            //pdLoading.dismiss();
        }

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


