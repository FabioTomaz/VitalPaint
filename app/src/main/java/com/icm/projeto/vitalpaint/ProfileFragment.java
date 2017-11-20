package com.icm.projeto.vitalpaint;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.icm.projeto.vitalpaint.Data.UserData;
import com.icm.projeto.vitalpaint.Data.UserDataManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements UserDataManager.UserDataListener{

    public static final int PICK_PHOTO_FOR_AVATAR = 1;
    public static final int PICK_PHOTO_FOR_HEADER = 2;
    Uri selectedImage;
    FirebaseStorage storage;
    StorageReference storageRef,imageRef;
    ProgressDialog progressDialog;
    UploadTask uploadTask;
    private ImageView profileImageView;
    private ImageView headerImageView;
    private TextView name;
    private TextView shortBio;

    public ProfileFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        UserDataManager userDataManager = new UserDataManager();
        userDataManager.addListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("Perfil");
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        profileImageView = (ImageView) getActivity().findViewById(R.id.profile_image);
        headerImageView = (ImageView) getActivity().findViewById(R.id.header_cover_image);
        shortBio = (TextView) view.findViewById(R.id.user_profile_name);
        shortBio = (TextView) view.findViewById(R.id.user_profile_short_bio);
        profileImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR);
            }
        });
        headerImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_PHOTO_FOR_HEADER);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK && data!= null && data.getData()!=null) {
            InputStream inputStream = null;
            try {
                inputStream = getActivity().getContentResolver().openInputStream(data.getData());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            ImageView imageProfile = (ImageView) getView().findViewById(R.id.profile_image);
            imageProfile.setImageBitmap(BitmapFactory.decodeStream(inputStream));
            selectedImage = data.getData();
            uploadImage("profilePic");
        }else if(requestCode == PICK_PHOTO_FOR_HEADER && resultCode == Activity.RESULT_OK && data!= null && data.getData()!=null) {
            InputStream inputStream = null;
            try {
                inputStream = getActivity().getContentResolver().openInputStream(data.getData());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            ImageView imageHeader = (ImageView) getView().findViewById(R.id.header_cover_image);
            imageHeader.setImageBitmap(BitmapFactory.decodeStream(inputStream));
            selectedImage = data.getData();
            uploadImage("headerPic");
        }
    }

    public void uploadImage(String imageType) {
        //create reference to images folder and assing a name to the file that will be uploaded
        imageRef = storageRef.child("User Profile Photos/"+UserData.loggedUser.getEMAIL()+"/"+imageType+"/");
        //creating and showing progress dialog
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMax(100);
        progressDialog.setMessage("Uploading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
        progressDialog.setCancelable(false);
        //starting upload
        uploadTask = imageRef.putFile(selectedImage);
        // Observe state change events such as progress, pause, and resume
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                //sets and increments value of progressbar
                progressDialog.incrementProgressBy((int) progress);
            }
        });
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(getActivity(), "Error in uploading!", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Toast.makeText(getActivity(), "Upload successful", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                //showing the uploaded image in ImageView using the download url
                //Picasso.with(UploadActivity.this).load(downloadUrl).into(imageView);
            }
        });
    }

    @Override
    public void onReceive(UserData user) {
        shortBio.setText(user.getNAME());
        /*shortBio.setText(user.getShortBio());
        try {
            downloadProfilePic(UserData.loggedUser.getUSERNAME());
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}
