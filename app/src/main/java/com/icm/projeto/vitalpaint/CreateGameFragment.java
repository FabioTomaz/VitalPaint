package com.icm.projeto.vitalpaint;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateGameFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {//@link CreateGameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateGameFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private Button createBtn;
    private View inflatedView;
    private EditText gameName;
    private Spinner nTeamElements;
    private Spinner gameMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle("Criar Jogo");
        this.inflatedView = inflater.inflate(R.layout.fragment_create_game, container, false);
        createBtn = (Button) inflatedView.findViewById(R.id.create_game);
        gameName = (EditText) inflatedView.findViewById(R.id.game_name);
        gameMode =(Spinner) inflatedView.findViewById(R.id.game_mode_spinner);
        nTeamElements =(Spinner) inflatedView.findViewById(R.id.nLementsSpinner);
        //Log.i("email:", UserData.EMAIL+"");
        //listener para o click do botao de criar lobby. Iniciar atividade de Lobby
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("",  nTeamElements.getSelectedItem().toString());
                if ( gameMode.getSelectedItemPosition() == 1) {//selecionado modo Team vs Team
                    Intent intent = new Intent(getActivity(), LobbyTeamActivity.class);
                    intent.putExtra("gameName", gameName.getText().toString());
                    intent.putExtra("gameMode", gameMode.getSelectedItem().toString());
                    intent.putExtra("nElements", nTeamElements.getSelectedItem().toString());
                    intent.putExtra("isHost", true);//este utilizador criou o lobby
                    startActivity(intent);
                }
            }
        });
        return inflatedView;


    }

    @Override
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
