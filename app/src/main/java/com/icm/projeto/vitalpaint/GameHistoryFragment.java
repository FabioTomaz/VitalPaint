package com.icm.projeto.vitalpaint;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.icm.projeto.vitalpaint.Data.GamePlayed;


/**
 * A simple {@link Fragment} subclass.
 */
public class GameHistoryFragment extends Fragment {
    private String userEmail;

    public GameHistoryFragment() {
        userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Historico de Jogos");
        View view = inflater.inflate(R.layout.fragment_game_history, container, false);
        FirebaseListAdapter<GamePlayed> adapter = new GameHistoryListAdapter(getActivity(), getContext(),FirebaseAuth.getInstance().getCurrentUser().getEmail());
        ListView friendsListView = (ListView)view.findViewById(R.id.list_friends);
        friendsListView.setAdapter(adapter);
        return view;
    }

}
