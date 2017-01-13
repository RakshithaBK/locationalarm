package com.trianz.locationalarm;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class OffersActivity extends Activity {

    private Cursor c;
    ListView listView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);

        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.list);
        List<SMSData> smsList = new ArrayList<SMSData>();

        // Defined Array values to show in ListView
        Uri uri = Uri.parse("content://sms/inbox");
        listView.setDivider(null);
        listView.setDividerHeight(0);


        c = getContentResolver().query(uri, new String[]{"_id", "address", "date", "body"},null ,null,null);
        startManagingCursor(c);

        // Read the sms data and store it in the list
        if(c.moveToFirst()) {
            for(int i=0; i < c.getCount(); i++) {
                SMSData sms = new SMSData();
                sms.setBody(c.getString(c.getColumnIndexOrThrow("body")).toString());
                sms.setNumber(c.getString(c.getColumnIndexOrThrow("address")).toString());
                smsList.add(sms);

                c.moveToNext();
            }
        }


        // Assign adapter to ListView
        listView.setAdapter(new OffersListAdapter(this, smsList));

    }

//    @Override
//    protected void onListItemClick(ListView l, View v, int position, long id) {
//        SMSData sms = (SMSData)listView.getListAdapter().getItem(position);
//
//        Toast.makeText(getApplicationContext(), sms.getBody(), Toast.LENGTH_LONG).show();
//
//    }

    public void onDestroy() {
        super.onDestroy();
        if (c != null) {
            c.close();
        }
    }
}
