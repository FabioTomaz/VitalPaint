package com.icm.projeto.vitalpaint;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.firebase.storage.FirebaseStorage;
import com.icm.projeto.vitalpaint.Data.UserData;
import com.icm.projeto.vitalpaint.Data.UserDataManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment implements UserDataManager.UserDataListener{
    private FloatingActionButton addFriendButton;
    private ListView friendsListView;
    private FriendsListAdapter adapter;
    private UserDataManager userDataManager;
    public static final int PROFILE_DATA = 1;
    public static final int CHECK_USER_EXISTS = 2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Amigos");
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        friendsListView = (ListView)view.findViewById(R.id.list_friends);
        adapter = new FriendsListAdapter(getActivity(), new ArrayList<UserData>());
        friendsListView.setAdapter(adapter);
        userDataManager.addListener(this, PROFILE_DATA);
        final FriendsFragment friendsFragment = this;
        addFriendButton = (FloatingActionButton) view.findViewById(R.id.add_friend);
        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(getContext());
                View promptsView = li.inflate(R.layout.dialog_input_text, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Light_Dialog_Alert);

                // set prompts.xml to alertdialog builder
                builder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);
                builder.setTitle("Adicionar Amigo");

                builder.setView(promptsView);

                // Set up the buttons
                builder.setPositiveButton("Adicionar!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //insert friend in db
                        UserDataManager userDataManager = new UserDataManager(userInput.getText().toString());
                        userDataManager.addListener(friendsFragment, CHECK_USER_EXISTS);
                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.create();
                builder.show();
            }
        });
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        userDataManager = (UserDataManager) getArguments().getSerializable("dbmanager");
    }

    public static FriendsFragment newInstance(UserDataManager manager) {
        FriendsFragment fragmentDemo = new FriendsFragment();
        Bundle args = new Bundle();
        args.putSerializable("dbmanager", manager);
        fragmentDemo.setArguments(args);
        return fragmentDemo;
    }

    @Override
    public void onReceiveUserData(int request, UserData user, Bitmap profilePic, Bitmap headerPic) {
        if(request == PROFILE_DATA) {
            if (user.getFriends() != null) {
                Log.i("pilas", user.getFriends().toString());
                adapter.addAll(user.getFriends());
                adapter.notifyDataSetChanged();
            }
        }else if(request == CHECK_USER_EXISTS){
            if(user==null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Light_Dialog_Alert);
                builder.setTitle("Não foi possivel adicionar amigo");

                builder.setMessage("O email do utilizador não existe ou a conexão ao servidor caiu");

                // Set up the buttons
                builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
            }else {
                UserData filteredUserData = new UserData();
                filteredUserData.setNAME(user.getNAME());
                filteredUserData.setEMAIL(user.getEMAIL());
                userDataManager.addFriend(filteredUserData);
            }
        }
    }
}
