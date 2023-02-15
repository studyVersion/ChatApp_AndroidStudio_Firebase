package com.example.SwissCaps.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.SwissCaps.R;

import java.util.List;

public class CustumCallAdapter extends ArrayAdapter<String> {

    private final Context context;
    private List<String> callNames;
    private List<String> hour;

    public CustumCallAdapter(@NonNull Context context, int resource, @NonNull List<String> callNames,List<String> hour ) {
        super(context, -1, callNames);
        this.context = context;
        this.callNames =callNames;
        this.hour = hour;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.call_item_card, parent, false);
        TextView callName =  rowView.findViewById(R.id.call_name);
        TextView callHour = rowView.findViewById(R.id.call_hour);
        ImageView callIcon = rowView.findViewById(R.id.call_type);

        callName.setText(callNames.get(position));
        callHour.setText(hour.get(position));
        if(position%2 == 0){
            callIcon.setImageResource(R.drawable.call_made);
        }else{
            callIcon.setImageResource(R.drawable.call_received);
        }

        return rowView;
    }

}
