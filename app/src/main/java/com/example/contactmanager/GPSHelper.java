/* Written by: Sarah Kolanowski + Huy Hoang
 * NetID: sck160130 + hdh160030
 * CS4301.002
 * Assignment: Contact List pt. 4
 */
package com.example.contactmanager;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

/* Written by: Sarah Kolanowski
 * This class handles the GPS location listener/ handles getting the users location.
 */
public class GPSHelper implements LocationListener {
    Context context;
    private LocationManager locationManager;
    Location currLocation;

    public GPSHelper(Context c){
        context = c;
    }
    @Override
    public void onLocationChanged(Location location) {

    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }


    /* This app needs permission to access the GPS so here we check to see if the GPS is enabled. If not
     * then we notify the user in a toast. Otherwise if we have permission/ the GPS is enabled we get the
     * last known location of the user. We call this in our ContactInfo class.
     */
    public Location getLocation(){
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(context, "Permission not granted", Toast.LENGTH_SHORT).show();
            return null;
        }
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGPDEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(isGPDEnabled){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6000, 0, this);
            currLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            return currLocation;
        }
        else{
            Toast.makeText(context, "Please enable GPS", Toast.LENGTH_LONG).show();
        }
        return null;
    }


    @Override
    public void onProviderEnabled(String provider) {

    }


    @Override
    public void onProviderDisabled(String provider) {

    }
}
