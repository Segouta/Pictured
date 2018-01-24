package com.example.christian.pictured;

/*
 * Created by Christian on 24-1-2018.
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class RankListAdapter extends ArrayAdapter{

    public RankListAdapter(Context context, ArrayList<String> list){
        super(context, 0, list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_layout, parent, false);
        }

        TextView name = convertView.findViewById(R.id.name);
        TextView rank = convertView.findViewById(R.id.rank);

        String NAME = getItem(position).toString();

        name.setText(NAME);
        rank.setText(NAME);


        return convertView;
    }


}
