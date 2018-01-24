package com.example.christian.pictured;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

public class FragmentA extends Fragment {

    ListView list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_slide_a, container, false);

        list = rootView.findViewById(R.id.list);

        ArrayList<String> lijst = new ArrayList<String>();
        String[] lijstje = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        lijst.addAll(Arrays.asList(lijstje));

        RankListAdapter rankListAdapter = new RankListAdapter(getContext(), lijst);
        list.setAdapter(rankListAdapter);

        return rootView;
    }
}
