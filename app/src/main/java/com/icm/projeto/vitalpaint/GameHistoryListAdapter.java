package com.icm.projeto.vitalpaint;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.FirebaseDatabase;
import com.icm.projeto.vitalpaint.Data.GamePlayed;
import com.icm.projeto.vitalpaint.Data.UserDataManager;

/**
 * Created by young on 24/11/2017.
 */

public class GameHistoryListAdapter extends FirebaseListAdapter<GamePlayed> {
    private final Context context;
    private final Activity activity;
    private final String userEmail;

    public GameHistoryListAdapter(Activity activity, Context context, String userEmail) {
        super(activity, GamePlayed.class, R.layout.listview_activity, FirebaseDatabase.getInstance().getReference().child("Users").child(UserDataManager.encodeUserEmail(userEmail)).child("GamesPlayed"));
        this.context = context;
        this.userEmail = userEmail;
        this.activity = activity;
    }

    @Override
    protected void populateView(View v, final GamePlayed model, int position) {
        final TextView txtGameResult = (TextView) v.findViewById(R.id.game_result);
        final TextView txtMode = (TextView) v.findViewById(R.id.game_mode);
        final TextView txtStartDate = (TextView) v.findViewById(R.id.game_start_date);
        final ImageView imageView = (ImageView) v.findViewById(R.id.image_game_result);


        if(model.getGameResult()==GamePlayed.RESULT.WON) {
            imageView.setImageResource(R.drawable.ic_win);
            txtGameResult.setText("Vitoria");
        }else if(model.getGameResult()==GamePlayed.RESULT.LOST) {
            imageView.setImageResource(R.drawable.ic_loss);
            txtGameResult.setText("Derrota");
        }
        txtStartDate.setText(model.getStartDate().toString());
        txtMode.setText(model.getGameMode().toString());
    }
}
