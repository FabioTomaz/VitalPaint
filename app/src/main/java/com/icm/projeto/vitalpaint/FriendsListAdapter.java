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
    protected void populateView(View v, String model1, int position) {
        Log.i("MODEL", model1);
        final String friendEmail = model1;
        final View view = v;
        DatabaseReference dbData = FirebaseDatabase.getInstance().getReference().child("Users").child(UserDataManager.encodeUserEmail(friendEmail));
        dbData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot snapshot) {
                TextView txtTitle = (TextView) view.findViewById(R.id.listview_item_title);
                TextView txtUnderTitle = (TextView) view.findViewById(R.id.listview_item_short_description);
                ImageView imageView = (ImageView) view.findViewById(R.id.imageRow);
                Log.i("USERRRR", userEmail);
                txtTitle.setText(snapshot.child("name").getValue(String.class));
                txtUnderTitle.setText(snapshot.child("email").getValue(String.class));
                Drawable drawable = ContextCompat.getDrawable(activity,R.drawable.imagem_perfil);
                RequestOptions options = new RequestOptions()
                        .error(drawable);

                    Glide.with(activity)
                            .load(FirebaseStorage.getInstance().getReference("User Profile Photos/" + friendEmail + "/profilePic"))
                            .apply(options)
                            .into(imageView);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }
}
