package com.icm.projeto.vitalpaint.Data;

import com.google.firebase.database.DatabaseError;

/**
 * Created by Bruno Silva on 20/11/2017.
 */
public interface OnGetDataListener {
    public void onStart();
    public void onSuccess(UserData data);
    public void onFailed(DatabaseError databaseError);
}