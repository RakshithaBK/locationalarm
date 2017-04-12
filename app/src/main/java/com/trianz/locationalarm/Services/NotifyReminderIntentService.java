/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.trianz.locationalarm.Services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.trianz.locationalarm.SetReminderSentByOthers;

import static com.trianz.locationalarm.Controllers.ReminderSetController.pad;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class NotifyReminderIntentService extends IntentService {

    public NotifyReminderIntentService() {
        super("NotifyReminderIntentService");
    }

     int reminderDay,reminderMinute,reminderHour,reminderMonth,reminderYear,pendingIntentRequestCode;
     String reminderEvent,reply_status_msg,sender_name,response_date,response_time;

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        Bundle bundle = intent.getExtras();
        reminderDay = bundle.getInt("reminderDay");
        reminderMinute = bundle.getInt("reminderMinute");
        reminderHour = bundle.getInt("reminderHour");
        reminderMonth = bundle.getInt("reminderMonth");
        reminderYear = bundle.getInt("reminderYear");
        reply_status_msg = bundle.getString("Status_msg");
        pendingIntentRequestCode = bundle.getInt("pendingIntentRequestCode");
        reminderEvent = bundle.getString("reminderEvent");
        sender_name = bundle.getString("sender");

        response_date = String.valueOf(pad(reminderDay)) + "/" + String.valueOf(pad(reminderMonth+1)) + "/" + String.valueOf(reminderYear);
        response_time = String.valueOf(pad(reminderHour)) + ":" + String.valueOf(pad(reminderMinute));
        SetReminderSentByOthers.executeTask(this, action,response_date,response_time,sender_name,reply_status_msg,reminderEvent);
    }
}