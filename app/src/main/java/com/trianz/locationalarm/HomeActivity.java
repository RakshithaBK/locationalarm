package com.trianz.locationalarm;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.trianz.locationalarm.Adapters.RemindersListAdapter;
import com.trianz.locationalarm.Authentication.AuthenticationActivity;
import com.trianz.locationalarm.Controllers.GeofenceController;
import com.trianz.locationalarm.Controllers.HomeController;
import com.trianz.locationalarm.Utils.MySingleton;
import com.trianz.locationalarm.Utils.NamedGeofence;
import com.trianz.locationalarm.Utils.NetworkCallModels;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.trianz.locationalarm.Controllers.PermissionsCheckController.checkLocationPermission;
import static com.trianz.locationalarm.Utils.Constants.Geometry.MY_PERMISSIONS_REQUEST_LOCATION;
import static com.trianz.locationalarm.Utils.Constants.Geometry.SET_REMINDER_REQUEST;
import static com.trianz.locationalarm.Utils.Constants.Instances.FORMATTER;
import static com.trianz.locationalarm.Utils.Constants.Instances.context;
import static com.trianz.locationalarm.Utils.Constants.Instances.frameLayout;
import static com.trianz.locationalarm.Utils.Constants.Instances.isDateSelected;
import static com.trianz.locationalarm.Utils.Constants.Instances.mBottomSheetBehavior1;
import static com.trianz.locationalarm.Utils.Constants.Instances.mCurrLocationMarker;
import static com.trianz.locationalarm.Utils.Constants.Instances.mGoogleApiClient;
import static com.trianz.locationalarm.Utils.Constants.Instances.mLastLocation;
import static com.trianz.locationalarm.Utils.Constants.Instances.mLocationRequest;
import static com.trianz.locationalarm.Utils.Constants.Instances.mMap;
import static com.trianz.locationalarm.Utils.Constants.Instances.monthFormat;
import static com.trianz.locationalarm.Utils.Constants.Instances.recyclerView;
import static com.trianz.locationalarm.Utils.Constants.Instances.reminderError;
import static com.trianz.locationalarm.Utils.Constants.Instances.remindersListAdapter;
import static com.trianz.locationalarm.Utils.Constants.Instances.selectedDate;
import static com.trianz.locationalarm.Utils.Constants.Instances.selectedPlace;
import static com.trianz.locationalarm.Utils.Constants.Instances.toolbar;
import static com.trianz.locationalarm.Utils.Constants.SharedPrefs.MY_PREFS_NAME;
import static com.trianz.locationalarm.Utils.Constants.serviceUrls.LOGOUT_URL;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, OnDateSelectedListener, OnMonthChangedListener {

    ViewGroup content_calender;
    FrameLayout search_place;
    ImageView calenderImg;
    @Bind(R.id.calender_frame)
    MaterialCalendarView widget;

    /*Overrides to be defined
    Activity cycle methods */
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
        if (!HomeController.isNetworkAvailable(context)) {
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission(this);
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        placeAutoComplete();
        GeofenceController.getInstance().init(this);
        recyclerViewSetter();

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

    @Override
    public void onBackPressed() {}

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
    //Activity life cycle methods end here

    //GoogleApiclient methods
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);

        }
        // IndoorBuilding building = mMap.getFocusedBuilding();
        // onIndoorLevelActivated(building);
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        // LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
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
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    @Override
    public void onConnectionSuspended(int i) {}

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
                    }

                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_CONTACTS)
                            == PackageManager.PERMISSION_GRANTED) {
                    }
                } else {
                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
    //GoogleApiClient method ends here

    //Navigation overrides
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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

        } else if (id == R.id.nav_offers) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //Navigation overrides ends here!

    //Calender methods
    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        // textView.setText(getSelectedDatesString());
        selectedDate = getSelectedDatesString();
        isDateSelected = false;
        content_calender.setVisibility(View.INVISIBLE);
        search_place.setVisibility(View.VISIBLE);
        Toast.makeText(this, selectedDate, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        getSupportActionBar().setTitle(monthFormat.format(date.getDate()).toString());
        getSupportActionBar().setSubtitle("");
    }

    //Override method ends here
    public void switchCalenderToMap() {
        //calender switch to map
        calenderImg = (ImageView) findViewById(R.id.calenderImg);
        search_place = (FrameLayout) findViewById(R.id.search_place_card);
        content_calender = (ViewGroup) findViewById(R.id.calender_frame);
        HomeController.switchCalenderToMapSetUp(this, calenderImg, search_place, content_calender);
    }

    public void fabButtonsAction() {
        //Fab actions
        FloatingActionButton wakeupfab = (FloatingActionButton) findViewById(R.id.fab_wakeup_alarm);
        FloatingActionButton addReminderLocationfab = (FloatingActionButton) findViewById(R.id.fab_add_reminder_location);
        FloatingActionButton remindothersfab = (FloatingActionButton) findViewById(R.id.fab_remind_others);
        final FloatingActionsMenu fabMenu = (FloatingActionsMenu) findViewById(R.id.fab_menu);
        HomeController.FloatActionBtnSetup(this, wakeupfab, addReminderLocationfab, remindothersfab, fabMenu);
    }

    public void navigationDrawerAttach() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Button signOut = (Button) findViewById(R.id.signOutBtn);

        //close Button in navigation
        final NavigationView nvDrawer = (NavigationView) findViewById(R.id.nav_view);
        View headerView = getLayoutInflater().inflate(R.layout.nav_header_home, nvDrawer, false);
        nvDrawer.addHeaderView(headerView);
        ImageView img = (ImageView) headerView.findViewById(R.id.nav_close);
        img.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(Gravity.LEFT);
            }
        });

        TextView UserName_Nav_bar = (TextView) headerView.findViewById(R.id.Username);
        TextView Email_Nav_bar = (TextView) headerView.findViewById(R.id.emailText);
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String UserName = prefs.getString("UserName", "No UserName Defined");
        String Email = prefs.getString("Email", "No Email Defined");
        UserName_Nav_bar.setText(UserName);
        Email_Nav_bar.setText(Email);
        HomeController.NavigationDrawerSetup(this, drawer, navigationView, signOut);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();

            }
        });
    }

    public void logoutUser() {
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, LOGOUT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Parsing json object response
                            // response will be a json object
                            NetworkCallModels models = new NetworkCallModels();
                            models.setJson(new JSONObject(response));
                            models.status = Boolean.parseBoolean(models.getJson().getString("status"));
                            models.message = models.getJson().getString("message");

                            if (models.status) {
                                Toast.makeText(HomeActivity.this, models.message, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(HomeActivity.this, AuthenticationActivity.class));
                            } else {
                                Toast.makeText(HomeActivity.this, models.message, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomeActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                String access_TokenKey1 = prefs.getString("AccessToken", "No AccessToken Defined");
                headers.put("Authorization", access_TokenKey1);
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        MySingleton.getInstance(HomeActivity.this).addToRequestQueue(stringRequest);
    }

    public void placeAutoComplete() {
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
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

                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Status status) {}
        });
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public void recyclerViewSetter() {
        recyclerView = (RecyclerView) findViewById(R.id.reminders_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(HomeActivity.this);

        if (GeofenceController.getInstance().getNamedGeofences().isEmpty()) {
            reminderError.setVisibility(View.VISIBLE);
        } else {
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

    // region GeofenceControllerListener
    private GeofenceController.GeofenceControllerListener geofenceControllerListener = new GeofenceController.GeofenceControllerListener() {
        @Override
        public void onGeofencesUpdated() {

            refresh();
        }

        @Override
        public void onError() {
            HomeController.showErrorToast(context);
        }
    };

    public void refresh() {
        if (GeofenceController.getInstance().getNamedGeofences().isEmpty()) {
            reminderError.setVisibility(View.VISIBLE);
        } else if (GeofenceController.getInstance().getNamedGeofences().size() == 1) {
            recyclerViewSetter();
        } else {
            remindersListAdapter.notifyDataSetChanged();
        }
    }

    private String getSelectedDatesString() {
        CalendarDay date = widget.getSelectedDate();
        if (date == null) {
            return "No Selection";
        }
        return FORMATTER.format(date.getDate());
    }
}