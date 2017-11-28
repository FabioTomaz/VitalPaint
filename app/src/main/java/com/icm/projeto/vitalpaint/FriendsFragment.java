package com.icm.projeto.vitalpaint;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.icm.projeto.vitalpaint.Data.UserData;
import com.icm.projeto.vitalpaint.Data.UserDataManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;


/**fmts@ua.pt
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment{
    private FloatingActionButton addFriendButton;
    private ListView friendsListView;
    private FirebaseListAdapter<String> adapter;
    public static final int PROFILE_DATA = 1;
    public static final int CHECK_USER_EXISTS = 2;
    private UserDataManager userDataManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Amigos");
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        adapter = new FriendsListAdapter(getActivity(), getContext(),FirebaseAuth.getInstance().getCurrentUser().getEmail());
        friendsListView = (ListView)view.findViewById(R.id.list_friends);
        friendsListView.setAdapter(adapter);
        friendsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String user = (String)adapter.getItem(position);
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, ProfileFragment.newInstance(user), user+"Profile");
                ft.commit();
                ft.addToBackStack(null);
            }
        });
        final FriendsFragment friendsFragment = this;
        addFriendButton = (FloatingActionButton) view.findViewById(R.id.add_friend);
        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(getContext());
                View promptsView = li.inflate(R.layout.dialog_input_text, null);

                MaterialDialog builder = new MaterialDialog.Builder(getContext())
                        .title("Adicionar Amigo")
                        .positiveText("Adicionar!")
                        .negativeText("Cancelar")
                        .inputType(InputType.TYPE_CLASS_TEXT )
                        .input("Email do Jogador", "", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {}
                        })
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                final String friendEmail = dialog.getInputEditText().getText().toString();
                                if(friendEmail.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Não te podes adicionar como amigo :D", Toast.LENGTH_SHORT);
                                    toast.show();
                                }else{
                                    DatabaseReference dbData = FirebaseDatabase.getInstance().getReference().child("Users").child(UserDataManager.encodeUserEmail(friendEmail));
                                    dbData.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(final DataSnapshot dataSnapshot) {
                                            if(!dataSnapshot.exists()) {
                                                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "O email do utilizador indicado não existe", Toast.LENGTH_SHORT);
                                                toast.show();
                                            } else {
                                                Log.i("friendemail", UserDataManager.encodeUserEmail(friendEmail));
                                                DatabaseReference dbUserData = FirebaseDatabase.getInstance().getReference().child("Users").child(UserDataManager.encodeUserEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail())).child("friends");
                                                dbUserData.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot snapshot) {
                                                        boolean bool = false;
                                                        for (DataSnapshot emp : snapshot.getChildren()) {
                                                            Log.i("friendemail", "dsffsdfds"+emp.getValue(String.class));
                                                            if(emp.getValue(String.class).equals(friendEmail)) {
                                                                bool = true;
                                                                break;
                                                            }
                                                        }
                                                        if(bool==false){
                                                            userDataManager.addFriend(friendEmail);
                                                            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Amigo adicionado com sucesso!", Toast.LENGTH_SHORT);
                                                            toast.show();
                                                        }else{
                                                            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Já és amigo do utilizador indicado!", Toast.LENGTH_SHORT);
                                                            toast.show();
                                                        }
                                                    }
                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {}
                                                });
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {}
                                    });
                                }
                            }
                        })
                        .negativeColor(getResources().getColor(R.color.navigationBarColor))
                        .positiveColor(getResources().getColor(R.color.navigationBarColor))
                        .show();
            }
        });
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        userDataManager = new UserDataManager((String) getArguments().getSerializable("userEmail"));
    }

    public static FriendsFragment newInstance(String userEmail) {
        FriendsFragment fragmentDemo = new FriendsFragment();
        Bundle args = new Bundle();
        args.putSerializable("userEmail", userEmail);
        fragmentDemo.setArguments(args);
        return fragmentDemo;
    }
}
