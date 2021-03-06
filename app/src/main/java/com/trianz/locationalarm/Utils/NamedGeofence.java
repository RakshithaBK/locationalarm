package com.trianz.locationalarm.Utils;

import android.support.annotation.NonNull;

import com.google.android.gms.location.Geofence;

import java.util.UUID;

public class NamedGeofence implements Comparable {

  // region Properties

  public String id;
  public String reminder_msg;
  public String reminder_place;
  public String reminder_Date_ToAlarm;
  public double latitude;
  public double longitude;
  public float radius;

  // end region

  // region Public

  public Geofence geofence() {
    id = UUID.randomUUID().toString();
    return new Geofence.Builder()
            .setRequestId(id)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .setCircularRegion(latitude, longitude, radius)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build();
  }

  // endregion

  // region Comparable

  @Override
  public int compareTo(@NonNull Object another) {
    NamedGeofence other = (NamedGeofence) another;
    return reminder_msg.compareTo(other.reminder_msg);
  }

  // endregion
}
