package com.example.SwissCaps.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.fragment.app.Fragment;

import com.example.SwissCaps.R;
import com.example.SwissCaps.adapters.CustomChatAdapter;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {
    ListView listview;
    String[] values = new String[] { "Juan",
            "Badre",
            "Adnane",
            "Roman",
            "Victor",
            "Kevin",
            "Lucas",
            "Pablo"
    };
    String[] chatDescription = new String[] { "Hey There! Are you using chatApp?",
            "All data is...ummm...safe",
            "How is the scholarship going on?",
            "This morning i woke up at night",
            "are you kidding me man! you were supposed to be here ages ago!!",
            "Photo",
            "shut up haha",
            "okey deal!"
    };

    String[] chatDates = new String[] { "08:58   ",
            "YESTERDAY",
            "05/02/2023",
            "03/01/2023",
            "27/01/2023",
            "26/01/2023",
            "27/12/2022",
            "26/11/2022"
    };

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        listview = rootView.findViewById(R.id.chat_list);
        final ArrayList<String> chatNameList = new ArrayList<String>();
        for (int i = 0; i < values.length; ++i) {
            chatNameList.add(values[i]);
        }
        final ArrayList<String> chatDescriptionList = new ArrayList<String>();
        for (int i = 0; i < values.length; ++i) {
            chatDescriptionList.add(chatDescription[i]);
        }
        final ArrayList<String> chatDatesList = new ArrayList<String>();
        for (int i = 0; i < values.length; ++i) {
            chatDatesList.add(chatDates[i]);
        }
        final CustomChatAdapter adapter = new CustomChatAdapter(getActivity(), R.layout.list_item_chat, chatNameList,chatDescriptionList, chatDatesList);
        listview.setAdapter(adapter);
        return rootView;
    }

}