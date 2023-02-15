package com.example.SwissCaps.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.SwissCaps.R;
import com.example.SwissCaps.adapters.CustumCallAdapter;

import java.util.ArrayList;


public class CallsFragment extends Fragment {

    ListView listview;
    String[] names = new String[] { "Juan",
            "Badre",
            "Adnane",
            "Roman",
            "Victor",
            "Kevin",
            "Lucas",
            "Pablo"
    };
    String[] hours = new String[] { "18:45","18:45","18:45","18:45","18:45","18:45","18:45","18:45"
    };

    public CallsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_calls, container, false);
        listview = rootView.findViewById(R.id.calls_list);
        final ArrayList<String> namesList = new ArrayList<String>();
        for (int i = 0; i < names.length; ++i) {
            namesList.add(names[i]);
        }
        final ArrayList<String> hoursList = new ArrayList<String>();
        for (int i = 0; i < hours.length; ++i) {
            hoursList.add(hours[i]);
        }
        CustumCallAdapter a = new CustumCallAdapter(getActivity(), R.layout.call_item_card, namesList, hoursList);
        listview.setAdapter(a);
        return rootView;

    }

}
