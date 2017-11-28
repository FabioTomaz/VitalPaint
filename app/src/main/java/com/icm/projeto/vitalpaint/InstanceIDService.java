package com.icm.projeto.vitalpaint;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.icm.projeto.vitalpaint.Data.UserDataManager;

public class InstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String token = FirebaseInstanceId.getInstance().getToken();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() !=null)
            FirebaseDatabase.getInstance().getReference().child("Users").child(UserDataManager.encodeUserEmail(auth.getCurrentUser().getEmail())).child("tokenID").setValue(token);
        Log.i("Token da App", token);
    }
}