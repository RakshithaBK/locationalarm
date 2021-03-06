package com.trianz.locationalarm.Utils;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.location.Location;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;
import com.trianz.locationalarm.Adapters.RemindersListAdapter;
import com.trianz.locationalarm.Controllers.GeofenceController;

import java.security.KeyStore;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

public class Constants {

    public static class Geometry {
        public static double MinLatitude = -90.0;
        public static double MaxLatitude = 90.0;
        public static double MinLongitude = -180.0;
        public static double MaxLongitude = 180.0;
        public static double MinRadius = 0.01; // kilometers
        public static double MaxRadius = 20.0; // kilometers
        public static int PROXIMITY_RADIUS = 1000;
        public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
        public static final int MY_PERMISSIONS_REQUEST_SENDSMS = 99;
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
        public static final String MY_PREFS_NAME = "MyPrefsFile";
        public static String repeatAlarmIntervalValue = "Does not repeat";

    }

    public static class Instances {
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
        public static Map<String, String> addressList = new HashMap<String, String>();
        public static String searchKey;
        public static Matcher matcher = null;

        //GeofenceController
        public static Boolean connectedFlag = false;
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
        public static double latitude;
        public static double longitude;

        //AddReminderActivity
        public static ImageView speechToText;

        //Add reminderToOthers
        public static final String KEY_Time = "time";
        public static final String KEY_DATE = "date";
        public static final String KEY_ALLDAYFLAG = "reminder_type";
        public static final String KEY_PHONENUMBER = "remind_to";
        public static final String KEY_REPEATALARMVALUE = "repeat";

        //fingerPrint Auth
        public static TabLayout tabLayout;
        public static LinearLayout container;
        public static FingerprintManager fingerprintManager;
        public static KeyguardManager keyguardManager;
        public static KeyStore keyStore;
        public static KeyGenerator keyGenerator;
        public static final String KEY_NAME = "example_key";
        public static Cipher cipher;
        public static FingerprintManager.CryptoObject cryptoObject;

        //HomeControl
        public static GoogleMap mMap;
        public static GoogleApiClient mGoogleApiClient;
        public static Location mLastLocation;
        public static Marker mCurrLocationMarker;
        public static LocationRequest mLocationRequest;

        public static RemindersListAdapter remindersListAdapter;
        public static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
        public static SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM");

    }

    public static class mapInstances {
        public static GoogleMap mMap;
        public static GoogleApiClient mGoogleApiClient;
        public static Location mLastLocation;
        public static Marker mCurrLocationMarker;
        public static LocationRequest mLocationRequest;
    }

    public static class authServiceInstances {
        public static final String KEY_MOBILE = "mobile";
        public static final String KEY_EMAIL = "email";
        public static final String KEY_PASSWORD = "password";
        public static final String KEY_USERNAME = "username";
        public static final String KEY_TOKEN = "fcmRegistrationId";

        public static final String KEY_MOBILE_LOGIN = "mobile";
        public static final String KEY_PASSWORD_LOGIN = "password";

        public static final String KEY_REG_OTP = "otp";
        public static final String KEY_REG_OTP_TOKEN = "otp_token";
        public static final String KEY_REG_OTP_SENDER = "mobile";

        public static final String KEY_FORPWD_MOBILE = "mobile";
        public static final String KEY_FORPWD_OTP_TOKEN = "password_otp_token";
        public static final String KEY_FORPWD_PWD_OTP = "password_otp";
        public static final String KEY_FORPWD_NEW_PASSWORD = "password";

        //send response notification
        public static final String KEY_RESPONSE_DATE = "date";
        public static final String KEY_RESPONSE_TIME = "time";
        public static final String KEY_RESPONSE_REMINDER_TYPE = "reminder_type";
        public static final String KEY_RESPONSE_REPLY_TO = "reply_to";
        public static final String KEY_RESPONSE_REPLY_STATUS = "reply_status";
        public static final String KEY_RESPONSE_REPEAT = "repeat";

    }

    public static class serviceUrls {
        public static final String REGISTER_URL = "http://52.30.191.42:8080/locationAlarm/alarm/register/user";
        public static final String LOGIN_URL = "http://52.30.191.42:8080/locationAlarm/alarm/login/user";
        public static final String REG_OTP_VERIFICATION_URL = "http://52.30.191.42:8080/locationAlarm/alarm/register/verifyOTP";
        public static final String FORGOTPWD_URL = "http://52.30.191.42:8080/locationAlarm/alarm/login/forgotPassword/sendOtp";
        public static final String FORGOTPWD_OTP_VERIFICATION_URL = "http://52.30.191.42:8080/locationAlarm/alarm/login/forgotPassword/verifyOtp";
        public static final String LOGOUT_URL = "http://52.30.191.42:8080/locationAlarm/alarm/logout/user";
        public static final String SEND_NOTIFICATION_RESPONSE_ = "http://52.30.191.42:8080/locationAlarm/alarm/reminderothers/reply";
        public static final String REMIND_TO_OTHERS_URL = "http://52.30.191.42:8080/locationAlarm/alarm/reminderothers/send";
    }
}
