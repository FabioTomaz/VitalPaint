package com.icm.projeto.vitalpaint;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.icm.projeto.vitalpaint.Data.UserDataManager;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by young on 27/11/2017.
 */

public class SendInviteTask extends AsyncTask<String ,Void,Void>{
    public static final MediaType JSON  = MediaType.parse("application/json; charset=utf-8");
    @Override
    protected Void doInBackground(String... params) {
            final String senderEmail = params[0];
            final String friendEmail = params[1];
            DatabaseReference dbData = FirebaseDatabase.getInstance().getReference().child("Users").child(UserDataManager.encodeUserEmail(friendEmail)).child("name");
            dbData.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    try {
                        Log.i("FRIENDINVITE", senderEmail);
                        Log.i("FRIENDINVITE", friendEmail);
                        Log.i("FRIENDINVITE", snapshot.getValue().toString());
                        OkHttpClient client = new OkHttpClient();
                        JSONObject json=new JSONObject();
                        JSONObject dataJson=new JSONObject();
                        dataJson.put("message", senderEmail + " convidou te para jogar um jogo. Clica para entrar no lobby.");
                        json.put("data",dataJson);
                        json.put("to", "/topics/"+snapshot.getValue());
                        Log.i("JSON", json.toString());
                        RequestBody body = RequestBody.create(JSON, json.toString());
                        Log.i("JSON", body.toString());
                        Request request = new Request.Builder()
                                .header("Authorization","key=" + "AAAAIRnfZs0:APA91bH_26b0gb-ceF9EkszO20wBX3ix2ObW216w-XfJoz6HKM73ivKt8KQG_ZteSE3JTulunjdSa7eOsNEy60SXpyJRulepJkK7t4-adke6CqvIRBAAzWvMWSS0ZNUUMhfIUJ36-PGj")
                                .url("https://fcm.googleapis.com/fcm/send")
                                .post(body)
                                .build();
                        Response response = client.newCall(request).execute();
                        String finalResponse = response.body().string();
                        Log.i("resposta", "RESPONSE"+finalResponse);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        return null;
    }
}
