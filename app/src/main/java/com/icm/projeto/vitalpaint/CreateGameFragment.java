package com.icm.projeto.vitalpaint;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.icm.projeto.vitalpaint.Data.GameMode;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Calendar;
import java.util.Date;


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
    private Spinner gameMode;
    private DatePicker startDate;
    private TimePicker startTime;
    private DateTime gameStart;
    private NumberPicker durationPicker;

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
        startTime = (TimePicker) inflatedView.findViewById(R.id.start_time);
        //colocar timepicker com formato 24h e inicializar para a hora atual
        startTime.setIs24HourView(true);
        startTime.setCurrentHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        durationPicker = (NumberPicker)  inflatedView.findViewById(R.id.duration_picker);
        durationPicker.setMinValue(5); //tempo min de jogo 5 minutos
        durationPicker.setMaxValue(260); //tempo min de jogo 260 minutos
        gameName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        //Log.i("email:", UserData.EMAIL+"");
        //listener para o click do botao de criar lobby. Iniciar atividade de Lobby
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //nota: nos date spinners, os meses comeÃ§am em 0...
                DateTime dateTime = new DateTime();
                gameStart = new DateTime(dateTime.getYear(), dateTime.getMonthOfYear(), dateTime.getDayOfMonth(), startTime.getCurrentHour(), startTime.getCurrentMinute());
                Log.i("date", gameStart.toString());
                DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");
                DateTime dt = formatter.parseDateTime(getSimpleStartTime(gameStart));

                if(gameStart.isAfter(null)) { //verificar se a data de inicio do jogo nÃ£o Ã© uma data passada
                    if (gameMode.getSelectedItemPosition() == 1) {//selecionado modo Team vs Team
                        Intent intent = new Intent(getActivity(), LobbyTeamActivity.class);
                        intent.putExtra("gameName", gameName.getText().toString());
                        intent.putExtra("startDate", getSimpleStartTime(gameStart));
                        intent.putExtra("duration", durationPicker.getValue());
                        intent.putExtra("gameMode", GameMode.TEAMVSTEAM.toString());//passar enum como string
                        intent.putExtra("isHost", true);//este utilizador criou o lobby
                        startActivity(intent);
                    } else if (gameMode.getSelectedItemPosition() == 1) {

                    }
                }
                else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setTitle(getResources().getString(R.string.invalidDateWarning));
                    alert.setMessage(getResources().getString(R.string.invalidDateWarningDetail));
                    alert.setPositiveButton("OK", null);
                    alert.show();
                }
            }

        });

        return inflatedView;
    }
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    public String getSimpleStartTime(DateTime time){
        return time.getDayOfMonth()+"/"+time.getMonthOfYear()+"/"+time.getYear()+" "+
                time.getHourOfDay()+":"+time.getMinuteOfHour();
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
