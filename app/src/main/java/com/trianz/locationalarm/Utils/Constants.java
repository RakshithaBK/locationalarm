package com.trianz.locationalarm.Utils;

import android.content.Context;
import android.location.Location;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

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
      public static int SET_REMINDER_REQUEST = 1;

  }

  public static class SharedPrefs {
    public static String Geofences = "SHARED_PREFS_GEOFENCES";

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
    }

    public static class serviceUrls{
        public static final String REGISTER_URL = "http://10.10.5.202:8080/com.priya.jersey.first/alarm/register/user";
        public static final String LOGIN_URL = "http://10.10.5.202:8080/com.priya.jersey.first/alarm/login/user";
    }

}


