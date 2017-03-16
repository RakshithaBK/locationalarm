package com.trianz.locationalarm.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.trianz.locationalarm.R;
import com.trianz.locationalarm.Utils.SMSData;

import java.util.List;

/**
 * Created by Rakshitha.Krishnayya on 11-01-2017.
 */

public class OffersListAdapter extends ArrayAdapter<SMSData> {

    // List context
    private final Context context;
    // List values
    private final List<SMSData> smsList;

    public OffersListAdapter(Context context, List<SMSData> smsList) {
        super(context, R.layout.list_view_content, smsList);
        this.context = context;
        this.smsList = smsList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_view_content, parent, false);
        TextView senderNumber = (TextView) rowView.findViewById(R.id.smsNumberText);
        senderNumber.setText(smsList.get(position).getNumber() + "\n" + "\n"
                + smsList.get(position).getBody());

        return rowView;
    }
}
