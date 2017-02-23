package com.trianz.locationalarm.Utils;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

public class Constants {

  public static class Geometry {
    public static double MinLatitude = -90.0;
    public static double MaxLatitude = 90.0;
    public static double MinLongitude = -180.0;
    public static double MaxLongitude = 180.0;
    public static double MinRadius = 0.01; // kilometers
    public static double MaxRadius = 20.0; // kilometers
    public static  int PROXIMITY_RADIUS = 1000;
      public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
      public static final int MY_PERMISSIONS_REQUEST_RECORD = 99;
      public static int SET_REMINDER_REQUEST = 1;
      // The minimum distance to change updates in meters
      public static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
      // The minimum time between updates in milliseconds
      public static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
      // How many Geocoder should return our GPSTracker
     public static int geocoderMaxResults = 1;
      public static final int RESULT_SPEECH = 1;
  }

  public static class SharedPrefs {
    public static String Geofences = "SHARED_PREFS_GEOFENCES";
      public static String remindMeBeforeTimeValue = "20minutes";
      public static String repeatAlarmIntervalValue = "Does not repeat";

  }
    public static class Instances{
        public static Place selectedPlace = null;
        public static String selectedDate = null;
        public static RecyclerView recyclerView;
        public static TextView reminderError;
        public static FrameLayout frameLayout;
        public static Boolean isDateSelected = false;
        public static Context context;
        public static BottomSheetBehavior mBottomSheetBehavior1;
        public static Toolbar toolbar;
        public static Boolean selfReminderFlag;

        //OffersInstanc Variables
        public static String address = new String();
        public static String smsBody = new String();
        public static Map<String,String> addressList = new HashMap<String, String>();
        public static String searchKey;
        public static Matcher matcher = null;

        //GeofenceController
        public static Boolean connectedFlag=false;
        public static Gson gson;
        public static SharedPreferences prefs;
        public static GeofenceController INSTANCE;
        public static Geofence geofenceToAdd;
        public static NamedGeofence namedGeofenceToAdd;


        //GPS Tracker Instances
        public static Context mContext;
        // flag for GPS Status
        public static boolean isGPSEnabled = false;
        // flag for network status
        public static boolean isNetworkEnabled = false;
        // flag for GPS Tracking is enabled
        public static boolean isGPSTrackingEnabled = false;
        public static String provider_info;
        public static  double latitude;
        public static double longitude;

        //AddReminderActivity
        public static ImageView speechToText;
        public static EditText message;
        public static Place place;
        public static String location_name,reminder_message;
        public static double location_latitude, location_longitude;
        public static float radius;

        //Add reminderToOthers
        public static final String KEY_HOUR = "12";
        public static final String KEY_MONTH = "1";
        public static final String KEY_DAY = "1";
        public static final String KEY_YEAR = "2050";
        public static final String KEY_MINUTE = "00";
        public static final String KEY_ALLDAYFLAG = "false";
        public static final String KEY_PHONENUMBER = "9836871071";
        public static final String KEY_REPEATALARMVALUE = "Does not repeat";


        //Notification
        public static NotificationManager notificationManager;
        public static  int DISCARD_KEY = 1;
        public static  int SAVE_KEY = 1;

        //ReminderInstance
       public static boolean allDayFlag;
       public static int selectedHourAlarm;
        public static  int selectedMinuteAlarm;
        public static  int selectedYearAlarm;
        public static  int selectedMonthAlarm;
        public static int selectedDayAlarm;
       public static Calendar myCalender = Calendar.getInstance();
        public static String repeatAlarmIntervalValue;
        public static int pendingIntentRequestCode;

        public static  MediaPlayer mediaPlayer;
        public static String notificationTypeValue;
        public static  String audioFilePath;
        public static Ringtone ringtone;


    }

    public static class mapInstances{
        public static GoogleMap mMap;
        public static GoogleApiClient mGoogleApiClient;
        public static Location mLastLocation;
        public static Marker mCurrLocationMarker;
        public static LocationRequest mLocationRequest;
    }

    public static class authServiceInstances{
        public static final String KEY_MOBILE = "mobile";
        public static final String KEY_EMAIL = "email";
        public static final String KEY_PASSWORD = "password";
        public static final String KEY_TOKEN = "fcmRegistrationId";

        public static final String KEY_MOBILE_LOGIN = "mobile";
        public static final String KEY_PASSWORD_LOGIN = "password";
    }

    public static class serviceUrls{
        public static final String REGISTER_URL = "http://10.10.5.112:8080/com.location.alarm.api2/alarm/register/user";
        public static final String LOGIN_URL = "http://10.10.5.202:8080/com.priya.jersey.first/alarm/login/user";
    }

}


