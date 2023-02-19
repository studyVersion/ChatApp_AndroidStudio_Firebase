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
    String[] names = new String[] { "Juan Martinez",
            "Badre Serhiri",
            "Adnane El Biat",
            "Roman Lfita",
            "Victor Mendoza",
            "Kevin Hart",
            "Lucas Rodriges",
            "Pablo Martinez",
            "Adnane El Biat",
            "Kevin Hart",
            "Kevin Hart",
            "Lucas Rodriges"
    };

    String[] hours = new String[] { "February 14, 20:54",
                                    "(2) February 13, 18:45",
                                    "February 12, 20:00",
                                    "February 10, 18:00",
                                    "(3) February 8, 19:25",
                                    "February 5, 18:05",
                                    "February 2, 14:45",
                                    "January 30, 18:30",
                                    "January 26, 17:38",
                                    "January 25, 11:40",
                                    "(2) January 22, 10:30",
                                    "January 15, 18:30"

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
        for (int i = 0; i < names.length; ++i) {
            hoursList.add(hours[i]);
        }
        CustumCallAdapter a = new CustumCallAdapter(getActivity(), R.layout.call_item_card, namesList, hoursList);
        listview.setAdapter(a);
        return rootView;

    }

}
