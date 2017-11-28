package com.icm.projeto.vitalpaint;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.icm.projeto.vitalpaint.Data.UserData;
import com.icm.projeto.vitalpaint.Data.UserDataManager;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


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
    private UserDataManager userDataManager;
    public static final int PROFILE_DATA = 1;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private String userEmail;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        userEmail = (String) (String) getArguments().getSerializable("userEmail");
        userDataManager = new UserDataManager(userEmail);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }

    public static ProfileFragment newInstance(String userEmail) {
        ProfileFragment fragmentDemo = new ProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable("userEmail", userEmail);
        fragmentDemo.setArguments(args);
        return fragmentDemo;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("Perfil");
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        profileImageView = (ImageView) view.findViewById(R.id.profile_image);
        headerImageView = (ImageView) view.findViewById(R.id.header_cover_image);
        name = (TextView) view.findViewById(R.id.user_profile_name);
        shortBio = (TextView) view.findViewById(R.id.user_profile_short_bio);
        if(FirebaseAuth.getInstance().getCurrentUser().getEmail() == userEmail) {
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
        }
        userDataManager.addListener(this);
        userDataManager.userDataFromEmailListener(PROFILE_DATA);
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
            uploadUserImage(auth.getCurrentUser().getEmail(),"profilePic");
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
            uploadUserImage(auth.getCurrentUser().getEmail(), "headerPic");
        }
    }

    public void uploadUserImage(String userEmail, String imageType) {
        //create reference to images folder and assing a name to the file that will be uploaded
        imageRef = storageRef.child("User Profile Photos/"+ userEmail+"/"+imageType+"/");
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
    public void onReceiveUserData(int requestType, UserData user, Bitmap profilePic, Bitmap headerPic) {
        if (getView()!=null) {
            name.setText(user.getNAME());
            shortBio.setText(user.getSHORTBIO());

            PieChart pieChart = (PieChart) getView().findViewById(R.id.pieChart);
            List<PieEntry> entries = new ArrayList<>();

            entries.add(new PieEntry(user.getnVictories(), "Vitorias"));
            entries.add(new PieEntry(user.getnDraws(), "Empates"));
            entries.add(new PieEntry(user.getnLosses(), "Derrotas"));

            PieDataSet set = new PieDataSet(entries, "Resultados dos Jogos");
            set.setValueTextSize(20);
            set.setColors(ColorTemplate.COLORFUL_COLORS);
            PieData data = new PieData(set);
            pieChart.getDescription().setEnabled(false);
            pieChart.setHoleRadius(35f);
            pieChart.setData(data);
            pieChart.invalidate(); // refresh
            pieChart.animateY(600, Easing.EasingOption.EaseInOutQuad);

            if (headerPic != null)
                headerImageView.setImageBitmap(headerPic);
            if (profilePic != null)
                profileImageView.setImageBitmap(profilePic);
            if(FirebaseAuth.getInstance().getCurrentUser().getEmail()==userEmail) {
                Snackbar snackbar = Snackbar
                        .make(getView(), "Podes alterar a tua biografia, foto de perfil e foto de capa clicando neles.", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }
        if(FirebaseAuth.getInstance().getCurrentUser().getEmail()==userEmail){
            shortBio.setOnClickListener(new View.OnClickListener() {
                String in = null;
                @Override
                public void onClick(View v) {
                    MaterialDialog builder = new MaterialDialog.Builder(getContext())
                            .title("Escreve a tua biografia")
                            .positiveText("Confirmar")
                            .negativeText("Cancelar")
                            .inputRangeRes(2, 20, R.color.navigationBarColor)
                            .inputType(InputType.TYPE_CLASS_TEXT )
                            .input("Biografia Nova", "", new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                    in = input.toString();
                                }
                            })
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    userDataManager.updateShortBio(in);
                                }
                            })
                            .show();
                }
            });
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MaterialDialog builder = new MaterialDialog.Builder(getContext())
                            .title("Novo Nome")
                            .positiveText("Confirmar")
                            .negativeText("Cancelar")
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    userDataManager.updateShortBio("new short bio");
                                }
                            })
                            .show();
                }
            });
        }
    }
}
