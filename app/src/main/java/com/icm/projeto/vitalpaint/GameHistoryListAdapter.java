package com.icm.projeto.vitalpaint;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.icm.projeto.vitalpaint.Data.UserDataManager;

/**
 * Created by young on 24/11/2017.
 */

public class GameHistoryListAdapter extends FirebaseListAdapter<String> {
    private final Context context;
    private final Activity activity;
    private final String userEmail;

    public GameHistoryListAdapter(Activity activity, Context context, String userEmail) {
        super(activity, String.class, R.layout.listview_activity, FirebaseDatabase.getInstance().getReference().child("Users").child(UserDataManager.encodeUserEmail(userEmail)).child("friends"));
        this.context = context;
        this.userEmail = userEmail;
        this.activity = activity;
    }

    @Override
    protected void populateView(View v, final String model, int position) {
        final TextView txtTitle = (TextView) v.findViewById(R.id.listview_item_title);
        final TextView txtUnderTitle = (TextView) v.findViewById(R.id.listview_item_short_description);
        final ImageView imageView = (ImageView) v.findViewById(R.id.imageRow);
        DatabaseReference dbData = FirebaseDatabase.getInstance().getReference().child("Users").child(UserDataManager.encodeUserEmail(model));
        dbData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot snapshot) {
                txtTitle.setText(snapshot.child("name").getValue(String.class));
                txtUnderTitle.setText(snapshot.child("email").getValue(String.class));
                Drawable drawable = ContextCompat.getDrawable(activity,R.drawable.imagem_perfil);
                RequestOptions options = new RequestOptions()
                        .error(drawable);
                try {
                    Glide.with(activity)
                            .load(FirebaseStorage.getInstance().getReference("User Profile Photos/" + model + "/profilePic"))
                            .apply(options)
                            .into(imageView);
                }catch (NullPointerException e){
                    imageView.setImageDrawable(drawable);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
}
