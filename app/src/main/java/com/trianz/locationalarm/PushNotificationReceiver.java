package com.trianz.locationalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.trianz.locationalarm.Utils.Constants.Instances.DISCARD_KEY;
import static com.trianz.locationalarm.Utils.Constants.Instances.notificationManager;

/**
 * Created by Rakshitha.Krishnayya on 21-02-2017.
 */

public class PushNotificationReceiver extends BroadcastReceiver{


    @Override
    public void onReceive(Context context, Intent intent) {
       // Bundle bundle= intent.getExtras();
        int reminderEvent = DISCARD_KEY;

        if(reminderEvent == 0){
            notificationManager.cancel(reminderEvent);
        }else{
        }
    }

}


