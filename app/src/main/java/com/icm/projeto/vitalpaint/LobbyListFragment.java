package com.icm.projeto.vitalpaint;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.icm.projeto.vitalpaint.Data.Lobby;
import com.icm.projeto.vitalpaint.Data.LobbyDataListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class LobbyListFragment extends Fragment implements LobbyDataListener {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private ListView lobbiesListView;
    private ArrayAdapter<Lobby> adapterItems;
    private DatabaseReference dbRef;
    private List<Lobby> lobbies;
    private List<String> lobbyName;
    private List<String> gameMode;
    private List<String> lobbyHost;
    private Map<String, String> hm;
    private List<Map<String, String>> listViewContents;
    private List<LobbyDataListener> listener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LobbyListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static LobbyListFragment newInstance(int columnCount) {
        LobbyListFragment fragment = new LobbyListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lobbies = new ArrayList<>();
        lobbyName = new ArrayList<>();
        gameMode = new ArrayList<>();
        lobbyHost = new ArrayList<>();
        hm = new HashMap<>();
        listViewContents = new ArrayList();
        listener = new ArrayList<>();
        // Create adapter based on items
        adapterItems = new ArrayAdapter<Lobby>(getActivity(), R.layout.lobby_list_view, lobbies);
        dbRef = FirebaseDatabase.getInstance().getReference().child("Games");
        this.addListener(this);//adicionar ao listener
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate view
        View view = inflater.inflate(R.layout.fragment_lobby_list, container, false);
        // Return view
        // Set the adapter

        lobbiesListView = (ListView) view.findViewById(R.id.lvLobbies);
        lobbiesListView.setAdapter(adapterItems);

        lobbiesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                HashMap<String, String> lobby = (HashMap<String, String>) lobbiesListView.getItemAtPosition(i);
                Intent intent = null;
                Log.i("", lobby+"");
                if(lobby.get("gameMode").equals("TEAMVSTEAM"))
                    intent = new Intent(getActivity(), LobbyTeamActivity.class);
                intent.putExtra("gameName", lobby.get("lobby_name")+"");
                intent.putExtra("gameMode", lobby.get("gameMode").toString());
                intent.putExtra("isHost", false);//o utlizador q se junta a um lobby nunca sera o host
                startActivity(intent);
                //adapter.dismiss(); // If you want to close the adapter
            }
        });

        return view;
    }

    @Override
    public void onLobbyListChange(List<Map<String, String>> lobbyList){//recebe os dados da firebase
        String[] from = {"lobby_name", "gameMode", "lobbyHost"};
        int[] to = {R.id.lobby_name, R.id.gameMode, R.id.lobbyHost};
        SimpleAdapter simpleAdapter = new SimpleAdapter(this.getContext(), lobbyList, R.layout.lobby_list_view, from, to);
        lobbiesListView.setAdapter(simpleAdapter);
    }

    public void addListener(LobbyListFragment lobbyListFragment) {
        listener.add(lobbyListFragment);
        lobbyList();//funçao q vai buscar os dados
    }

    private void lobbyList(){
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() { //listener para ler no inicio do fragmento da DB
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Lobby lobby = new Lobby();
                //obter dados dos lobbys e colocá-los nas estruturas de dados para o listView
                for (DataSnapshot data : snapshot.getChildren()) {
                    lobby = data.getValue(Lobby.class);
                    Log.i("lobby", lobby+"");
                    hm.put("lobby_name", lobby.getGameName());
                    hm.put("gameMode", lobby.getGameMode()+"");
                    hm.put("lobbyHost", lobby.getHost());
                    listViewContents.add(hm);//atualizar a lista de lobbies
                }
                listener.get(0).onLobbyListChange(listViewContents);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
    }
}
