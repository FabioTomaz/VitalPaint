package com.icm.projeto.vitalpaint;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.*;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.icm.projeto.vitalpaint.Data.UserData;
import com.icm.projeto.vitalpaint.Data.UserDataManager;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FriendsListAdapter extends FirebaseListAdapter<String> {
    private final Context context;
    private final Activity activity;
    private final String userEmail;

    public FriendsListAdapter(Activity activity, Context context, String userEmail) {
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
