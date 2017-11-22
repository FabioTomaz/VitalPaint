package com.icm.projeto.vitalpaint;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.icm.projeto.vitalpaint.Data.UserData;

import java.util.List;

public class FriendsListAdapter extends ArrayAdapter<UserData>{
    private final Activity context;
    private final List<UserData> userDatas;

    public FriendsListAdapter(Activity context,
                              List<UserData> userDatas) {
        super(context, R.layout.listview_activity, userDatas);
        this.context = context;
        this.userDatas = userDatas;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Log.i("OLAAAA", userDatas.get(position).toString());
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.listview_activity, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.listview_item_title);
        TextView txtUnderTitle = (TextView) rowView.findViewById(R.id.listview_item_short_description);
        //ImageView imageView = (ImageView) rowView.findViewById(R.id.listview_image);

        txtTitle.setText(userDatas.get(position).getNAME());
        txtUnderTitle.setText(userDatas.get(position).getEMAIL());
        //imageView.setImageResource(imageId.get(position));

        return rowView;
    }
}
