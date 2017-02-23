package com.trianz.locationalarm;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.trianz.locationalarm.Utils.GeofenceController;
import com.trianz.locationalarm.Utils.HomeController;
import com.trianz.locationalarm.Utils.NamedGeofence;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.trianz.locationalarm.Utils.Constants.Geometry.MY_PERMISSIONS_REQUEST_LOCATION;
import static com.trianz.locationalarm.Utils.Constants.Geometry.SET_REMINDER_REQUEST;
import static com.trianz.locationalarm.Utils.Constants.Instances.context;
import static com.trianz.locationalarm.Utils.Constants.Instances.frameLayout;
import static com.trianz.locationalarm.Utils.Constants.Instances.isDateSelected;
import static com.trianz.locationalarm.Utils.Constants.Instances.mBottomSheetBehavior1;
import static com.trianz.locationalarm.Utils.Constants.Instances.recyclerView;
import static com.trianz.locationalarm.Utils.Constants.Instances.reminderError;
import static com.trianz.locationalarm.Utils.Constants.Instances.selectedDate;
import static com.trianz.locationalarm.Utils.Constants.Instances.selectedPlace;
import static com.trianz.locationalarm.Utils.Constants.Instances.toolbar;

public class HomeActivity  extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,OnDateSelectedListener, OnMonthChangedListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;

    private RemindersListAdapter remindersListAdapter;
    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
    SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM");

    @Bind(R.id.calender_frame)
    MaterialCalendarView widget;

//    @Bind(R.id.textView)
//    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        context = this;
        //calender widget
        ButterKnife.bind(this);
        widget.setOnDateChangedListener(this);
        widget.setOnMonthChangedListener(this);
        //Scrollable cardview
        final View bottomSheet = findViewById(R.id.bottom_sheet1);
        mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior1.setHideable(false);
        mBottomSheetBehavior1.setPeekHeight(400);

        //Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setTitle("");
        switchCalenderToMap();
        frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        frameLayout.getBackground().setAlpha(0);
        fabButtonsAction();
        navigationDrawerAttach();
        reminderError = (TextView) findViewById(R.id.reminder_error_msg);
        if(!isNetworkAvailable()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.turn_on_internet)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            HomeActivity.this.finish();
                        }
                    }).setCancelable(false)
                    .create()
                    .show();
        }
        if(!isLocationEnabled(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.turn_on_gps)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            HomeActivity.this.finish();
                        }
                    }).setCancelable(false)
                    .create()
                    .show();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        placeAutoComplete();
        GeofenceController.getInstance().init(this);
        recyclerViewSetter();

        //FireBaseMessaging

        if (getIntent().getExtras() != null) {

            for (String key : getIntent().getExtras().keySet()) {
                String value = getIntent().getExtras().getString(key);

//                if (key.equals("AnotherActivity") && value.equals("True")) {
//
//                    //      Toast.makeText(MainActivity.this, "Another Activity!!!", Toast.LENGTH_SHORT).show();
//
//                }

            }
        }

        subscribeToPushService();
    }
    private void subscribeToPushService() {
        FirebaseMessaging.getInstance().subscribeToTopic("news");

        Log.d("AndroidBash", "Subscribed");
        //  Toast.makeText(MainActivity.this, "Subscribed", Toast.LENGTH_SHORT).show();

        String token = FirebaseInstanceId.getInstance().getToken();

        // Log and toast
        //   Log.d("AndroidBash", token);
        //   Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();

        FirebaseMessaging.getInstance().subscribeToTopic("news");
    }



    public void switchCalenderToMap(){
        //calender switch to map
        ImageView calenderImg = (ImageView) findViewById(R.id.calenderImg);
        calenderImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FrameLayout search_place = (FrameLayout) findViewById(R.id.search_place_card);
                ViewGroup content_calender= (ViewGroup) findViewById(R.id.calender_frame);
                if(!isDateSelected){
                    content_calender.setVisibility(View.VISIBLE);
                    search_place.setVisibility(View.INVISIBLE);
                    getSupportActionBar().setTitle("");
                    getSupportActionBar().setSubtitle("");
                    isDateSelected = true;
                }else{
                    content_calender.setVisibility(View.INVISIBLE);
                    search_place.setVisibility(View.VISIBLE);
                    getSupportActionBar().setTitle("");
                    isDateSelected= false;

                }
            }
        });
    }

    public void fabButtonsAction(){
        //Fab actions
        FloatingActionButton wakeupfab = (FloatingActionButton) findViewById(R.id.fab_wakeup_alarm);
        FloatingActionButton  addReminderLocationfab = (FloatingActionButton) findViewById(R.id.fab_add_reminder_location);
        FloatingActionButton  addReminderDatefab = (FloatingActionButton) findViewById(R.id.fab_add_reminder_date);
        FloatingActionButton  remindothersfab = (FloatingActionButton) findViewById(R.id.fab_remind_others);

        //close Button in navigation
        final NavigationView  nvDrawer = (NavigationView) findViewById(R.id.nav_view);
        View headerView = getLayoutInflater().inflate(R.layout.nav_header_home, nvDrawer, false);
        nvDrawer.addHeaderView(headerView);
        ImageView img = (ImageView) headerView.findViewById(R.id.nav_close);
        img.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(Gravity.LEFT);
            }});
        final FloatingActionsMenu fabMenu = (FloatingActionsMenu) findViewById(R.id.fab_menu);
        HomeController.FloatActionBtnSetup(this,wakeupfab,addReminderLocationfab,addReminderDatefab,remindothersfab,fabMenu);
    }


    public void navigationDrawerAttach() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Button signOut = (Button) findViewById(R.id.signOutBtn);
        HomeController.NavigationDrawerSetup(this,drawer,navigationView,signOut);

    }

    public void placeAutoComplete(){
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

               // Log.i(TAG, "Place: " + place.getName());
                selectedPlace = place;

                // Add a marker to the selected place
                LatLng location = place.getLatLng();
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(location).title(place.getName().toString()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

                try {

                    getSupportActionBar().setTitle(selectedPlace.getName());
                    getSupportActionBar().setSubtitle(selectedPlace.getAddress());

                } catch (NullPointerException e)
                {
                    e.printStackTrace();
                }


            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
               // Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

      /*  List<NamedGeofence> remindersOnMap = new ArrayList<>();
        remindersOnMap = GeofenceController.getInstance().getNamedGeofences();

        for(int i=0; i< remindersOnMap.size(); i++)
        {
            double latitude = remindersOnMap.get(i).latitude;
            double longitude = remindersOnMap.get(i).longitude;
            String reminder = remindersOnMap.get(i).reminder_msg;
            mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
                    .title(reminder).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

        }  */


        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void  onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }


        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) + ContextCompat
                .checkSelfPermission(this,
                        Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (this, Manifest.permission.ACCESS_FINE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale
                            (this, Manifest.permission.READ_SMS)) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_SMS},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission
                                .ACCESS_FINE_LOCATION, Manifest.permission.READ_SMS},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            //Call whatever you want
       return true;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_SMS)
                            == PackageManager.PERMISSION_GRANTED) {
                        //Toast.makeText(this, "SMS permision granted", Toast.LENGTH_SHORT).show();
                    }


                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        int googlePlayServicesCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        Log.i(HomeActivity.class.getSimpleName(), "googlePlayServicesCode = " + googlePlayServicesCode);

        if (googlePlayServicesCode == 1 || googlePlayServicesCode == 2 || googlePlayServicesCode == 3) {
            GooglePlayServicesUtil.getErrorDialog(googlePlayServicesCode, this, 0).show();
        }
    }

    public void recyclerViewSetter()
    {
        recyclerView = (RecyclerView) findViewById(R.id.reminders_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(HomeActivity.this);

        if(GeofenceController.getInstance().getNamedGeofences().isEmpty())
        {
            reminderError.setVisibility(View.VISIBLE);
        }
        else {

            reminderError.setVisibility(View.GONE);
            remindersListAdapter = new RemindersListAdapter(GeofenceController.getInstance().getNamedGeofences());

            remindersListAdapter.setListener(new RemindersListAdapter.AllGeofencesAdapterListener() {
                @Override
                public void onDeleteTapped(NamedGeofence namedGeofence) {
                    List<NamedGeofence> namedGeofences = new ArrayList<>();
                    namedGeofences.add(namedGeofence);
                    GeofenceController.getInstance().removeGeofences(namedGeofences, geofenceControllerListener);
                }
            });

            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(remindersListAdapter);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SET_REMINDER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {

                refresh();

            }
            if (resultCode == Activity.RESULT_CANCELED) {

                // Do nothing
            }
        }
    }

    // region GeofenceControllerListener

    private GeofenceController.GeofenceControllerListener geofenceControllerListener = new GeofenceController.GeofenceControllerListener() {
        @Override
        public void onGeofencesUpdated() {

            refresh();
        }

        @Override
        public void onError() {

            showErrorToast();
        }
    };

    private void refresh() {

        if(GeofenceController.getInstance().getNamedGeofences().isEmpty())
        {
            reminderError.setVisibility(View.VISIBLE);
        }
        else if(GeofenceController.getInstance().getNamedGeofences().size() == 1)
        {
            recyclerViewSetter();
        }
        else {

            remindersListAdapter.notifyDataSetChanged();
        }

    }

    private void showErrorToast() {
        Toast.makeText(HomeActivity.this, HomeActivity.this.getString(R.string.Toast_error), Toast.LENGTH_SHORT).show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_addreminder) {
            // Handle the camera action
        } else if (id == R.id.nav_remindOthers) {

        } else if (id == R.id.nav_wakeUpAlarm) {

        } else if (id == R.id.nav_myReminders) {

        } else if (id == R.id.nav_offers) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private String getSelectedDatesString() {
        CalendarDay date = widget.getSelectedDate();
        if (date == null) {
            return "No Selection";
        }
        return FORMATTER.format(date.getDate());
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
       // textView.setText(getSelectedDatesString());
        selectedDate = getSelectedDatesString();
        Toast.makeText(this, selectedDate, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        getSupportActionBar().setTitle(monthFormat.format(date.getDate()).toString());
        getSupportActionBar().setSubtitle("");
    }
}
