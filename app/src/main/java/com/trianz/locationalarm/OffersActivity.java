package com.trianz.locationalarm;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.trianz.locationalarm.Utils.SMSData;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.trianz.locationalarm.Utils.Constants.Instances.address;
import static com.trianz.locationalarm.Utils.Constants.Instances.addressList;
import static com.trianz.locationalarm.Utils.Constants.Instances.matcher;
import static com.trianz.locationalarm.Utils.Constants.Instances.searchKey;
import static com.trianz.locationalarm.Utils.Constants.Instances.smsBody;

public class OffersActivity extends AppCompatActivity {

    private Cursor c;
    List<SMSData> smsList;
    ListView listView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);

        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.list);
        smsList = new ArrayList<SMSData>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.apptoolbar);
        setSupportActionBar(toolbar);

        // Defined Array values to show in ListView
        listView.setDivider(null);
        listView.setDividerHeight(0);

        cursorReadInbox();
        patterMatchToList(smsList);
    }
    public void cursorReadInbox(){
        Uri uri = Uri.parse("content://sms/inbox");
        final Pattern sLimitPattern = Pattern.compile("^[A-Z][A-Z]-[A-Z]+");
        c = getContentResolver().query(uri, new String[]{"_id", "address", "date", "body"},null ,null,null);
        startManagingCursor(c);
        // Read the sms data and store it in the list
        if(c.moveToFirst()) {
            for (int i = 0; i < c.getCount(); i++) {
                SMSData sms = new SMSData();
                address = c.getString(c.getColumnIndexOrThrow("address"));
                smsBody = c.getString(c.getColumnIndexOrThrow("body"));
                addressList.put(address, smsBody);
                matcher = sLimitPattern.matcher(address);
                if(matcher.find()){
                    sms.setNumber(address);
                    sms.setBody(smsBody);
                    smsList.add(sms);
                }else{
                    //Toast.makeText(this, "Matcher Not found", Toast.LENGTH_SHORT).show();
                }
                c.moveToNext();
            }
        }
    }

    public void patterMatchToList(List<SMSData> smsList){
        // Assign adapter to ListView
        listView.setAdapter(new OffersListAdapter(this, smsList));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SMSData sms = (SMSData) listView.getAdapter().getItem(position);
                String s = sms.getNumber();
                Pattern findMyPattern = Pattern.compile(s);
                Matcher foundAMatch = findMyPattern.matcher(s);

                while(addressList.containsKey(s)){
                    if(findMyPattern.matcher(s).matches()){
                        if(s.equals("VK-HDFCBK") || s.equals("VM-HDFCBK") || s.equals("DM-HDFCBK") ){
                            searchKey = "Hdfcbank";
                            moveaToMap();
                        }else if(s.equals("AD-OLACBS") || s.equals("IX-OLACBS") || s.equals("VM-OLACAB") ||s.equals("VK-OLACAB") ){
                            searchKey = "Ola";
                            moveaToMap();
                        }else if(s.equals("VK-PANTLS") || s.equals("VM-PANTLS") ){
                            searchKey = "Pantaloons";
                            moveaToMap();
                        }else if(s.equals("VM-DOMINO")){
                            searchKey = "Dominos";
                            moveaToMap();
                        }else if(s.equals("VM-BIGBZR")){
                            searchKey = "BigBazarShops";
                            moveaToMap();
                        }else if(s.equals("VK-UBERIN")){
                            searchKey = "Uber";
                            moveaToMap();
                        }else if(s.equals("DM-Centrl")){
                            searchKey = "CentralMall";
                            moveaToMap();
                        }else if(s.equals("DM-ENCRL")){
                            searchKey = "Titan";
                            moveaToMap();
                        }else if(s.equals("AX-ARWINF")) {
                            searchKey = "Airtel";
                            moveaToMap();
                        }else{
                            Toast.makeText(OffersActivity.this, "No location found", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                }
            }
        });

    }

    public  void  moveaToMap(){
        Intent MapsActivity = new Intent(OffersActivity.this,OffersMapActivity.class);
        MapsActivity.putExtra("Get_Location_Name",searchKey);
        startActivity(MapsActivity);
    }

    public void onDestroy() {
        super.onDestroy();
        if (c != null) {
            c.close();
        }
    }


}
