package com.icm.projeto.vitalpaint;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import com.icm.projeto.vitalpaint.Data.GameData;
import com.icm.projeto.vitalpaint.Data.Lobby;
import com.icm.projeto.vitalpaint.Data.LobbyDataListener;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class LobbyListFragment extends Fragment {

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
    private String[] from = {"lobby_name", "gameMode", "gameStart", "gameDuration", "zone"};
    private int[] to = {R.id.lobby_name, R.id.gameMode, R.id.gameStart, R.id.gameDuration, R.id.zone};
    private double lat;
    private double longt;
    private String city;
    private LocationManager locationManager;
    private LocationListener locationListener;


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

    }

    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate view
        View view = inflater.inflate(R.layout.fragment_lobby_list, container, false);
        // Return view
        // Set the adapter
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();
        //receber atualizaçoes a cada 5 segundos,
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, (LocationListener) locationListener);

        lobbiesListView = (ListView) view.findViewById(R.id.lvLobbies);
        lobbiesListView.setAdapter(adapterItems);

        lobbiesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                HashMap<String, String> lobby = (HashMap<String, String>) lobbiesListView.getItemAtPosition(i);
                Intent intent = null;
                Log.i("lobby", lobby+"");
                Log.i("duration", lobby.get("gameStart").replaceAll("Início: ", "")+"");
                if(lobby.get("gameMode").equals("TEAMVSTEAM"))
                    intent = new Intent(getActivity(), LobbyTeamActivity.class);
                intent.putExtra("gameName", lobby.get("lobby_name")+"");
                intent.putExtra("gameMode", lobby.get("gameMode").toString());
                intent.putExtra("startDate", lobby.get("gameStart").replaceAll("Início: ", ""));
                intent.putExtra("duration", lobby.get("gameDuration").replaceAll("[Duração: m]", ""));
                intent.putExtra("isHost", false);
                intent.putExtra("lobbyLat", 0.0); //nao precisamos de passar as coordenadas do lobby, estas apenas sao escritas na
                intent.putExtra("lobbyLongt", 0.0); //firebase na altura da criaçao do lobby, nao as vamos usar mais quando nos juntamos a lobby
                intent.putExtra("city", lobby.get("zone").replaceAll("Zona: ", ""));
                startActivity(intent);
                startActivity(intent);
                //adapter.dismiss(); // If you want to close the adapter
            }
        });
        final Context context = getActivity().getApplicationContext();
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() { //listener para ler no inicio do fragmento da DB
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                GameData lobby = new GameData();
                hm = new HashMap<>();
                listViewContents = new ArrayList<>();
                //obter dados dos lobbys e colocá-los nas estruturas de dados para o listView
                DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");
                DateTime gameStart;
                for (DataSnapshot data : snapshot.getChildren()) {
                    lobby = data.getValue(GameData.class);
                    hm = new HashMap<>();
                    gameStart = formatter.parseDateTime(lobby.getStartDate());
                    Log.i("gamestart", gameStart.plusMinutes(lobby.getDuration()+5)+"");
                    if(!gameStart.plusMinutes(lobby.getDuration()+5).isAfter(null)){//verificar se existem lobbys que ja iniciaram e nao apresentar esses resultados
                        dbRef.child(data.getKey()).setValue(null);//apagar lobby invalido
                    }
                    else{ float[] dist = new float[1];
                        // se  o lobby estiver a mais que 10 kilometros do user, nao apresentar
                        Location.distanceBetween(lobby.getLobbyLat(), lobby.getLobbyLong(), lat, longt, dist);
                        if ( dist[0]/1000 <= 10) {
                            if (gameStart.isAfter(null)) {//o jogo ainda nao começou
                                hm.put("lobby_name", lobby.getGameName());
                                hm.put("gameMode", lobby.getGameMode() + "");
                                hm.put("gameStart", "Início: " + lobby.getStartDate());
                                hm.put("gameDuration", "Duração: " + lobby.getDuration() + "m");
                                Log.i("lobby", hm + "");
                                hm.put("zone", "Zona: " + lobby.getCity());
                                listViewContents.add(hm);//atualizar a lista de lobbies
                                hm = new HashMap<>();
                            }
                        }
                    }
                }
                SimpleAdapter simpleAdapter = new SimpleAdapter(context, listViewContents, R.layout.lobby_list_view, from, to);
                lobbiesListView.setAdapter(simpleAdapter);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        dbRef.addValueEventListener(new ValueEventListener() { //listener para ler no inicio do fragmento da DB
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                GameData lobby = new GameData();
                hm = new HashMap<>();
                listViewContents = new ArrayList<>();
                //obter dados dos lobbys e colocá-los nas estruturas de dados para o listView
                DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");
                DateTime gameStart;
                for (DataSnapshot data : snapshot.getChildren()) {
                    lobby = data.getValue(GameData.class);
                    hm = new HashMap<>();
                    gameStart = formatter.parseDateTime(lobby.getStartDate());
                    Log.i("gamestart", gameStart.plusMinutes(lobby.getDuration()+5)+"");
                    if(!gameStart.plusMinutes(lobby.getDuration()+5).isAfter(null)){//verificar se existem lobbys que ja iniciaram e nao apresentar esses resultados
                        dbRef.child(data.getKey()).setValue(null);//apagar lobby invalido
                    }
                    else{
                        float[] dist = new float[1];
                        // se  o lobby estiver a mais que 10 kilometros do user, nao apresentar
                        Location.distanceBetween(lobby.getLobbyLat(), lobby.getLobbyLong(), lat, longt, dist);
                        if ( dist[0]/1000 <= 10){
                            if(gameStart.isAfter(null)){//o jogo ainda nao começou
                                hm.put("lobby_name", lobby.getGameName());
                                hm.put("gameMode", lobby.getGameMode() + "");
                                hm.put("gameStart", "Início: " + lobby.getStartDate());
                                hm.put("gameDuration", "Duração: " + lobby.getDuration() + "m");
                                hm.put("zone", "Zona: " + lobby.getCity());
                                Log.i("lobby", hm + "");
                                listViewContents.add(hm);//atualizar a lista de lobbies
                                hm = new HashMap<>();
                            }
                        }
                    }
                }
                SimpleAdapter simpleAdapter = new SimpleAdapter(context, listViewContents, R.layout.lobby_list_view, from, to);
                lobbiesListView.setAdapter(simpleAdapter);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        return view;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onResume(){
        super.onResume();
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();
        //receber atualizaçoes a cada 5 segundos,
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, (LocationListener) locationListener);

    }

    @Override
    public void onPause(){
        super.onPause();
        locationManager.removeUpdates(locationListener);
        //locationManager=null;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            //editLocation.setText("");
            //pb.setVisibility(View.INVISIBLE);
            /*String longitude = "Longitude: " + loc.getLongitude();
            Log.v("longitude", longitude);
            String latitude = "Latitude: " + loc.getLatitude();
            Log.v("latitude", latitude);*/
            lat = loc.getLatitude();
            longt = loc.getLongitude();

        /*------- To get city name from coordinates -------- */
            String cityName = null;
            Geocoder gcd = new Geocoder(getActivity().getBaseContext(), Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(loc.getLatitude(),
                        loc.getLongitude(), 1);
                if (addresses.size() > 0) {
                    System.out.println(addresses.get(0).getLocality());
                    city = addresses.get(0).getLocality();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            Log.v("city", city);
            //editLocation.setText(s);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
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