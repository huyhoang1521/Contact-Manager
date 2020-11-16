/* Written by: Sarah Kolanowski + Huy Hoang
 * NetID: sck160130 + hdh160030
 * CS4301.002
 * Assignment: Contact List pt. 4
 */
package com.example.contactmanager;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/* Written by: Sarah Kolanowski
 * This class contains the map activity that will pull up once the user enters a valid address for their
 * desired contact and chooses to map that address. If the address is valid then this activity will
 * pull up, and output the users distance from that address in miles in a Toast notification
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    double newlat, newlng;
    double addrLat, addrLng;
    double distance = 0;

    /* Written by: Sarah Kolanowski
     * OnCreate we get all the extras send from the ContactInfo class. The latitiude and longitude
     * of the address entered as well as the lat and long of the user themselves.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ActivityCompat.requestPermissions(MapsActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 10);
        addrLat = getIntent().getDoubleExtra("lat", addrLat);
        addrLng = getIntent().getDoubleExtra("lng", addrLng);
        newlat = getIntent().getDoubleExtra("userlat", newlat);
        newlng = getIntent().getDoubleExtra("userlng", newlng);
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
    /* Written by: Sarah Kolanowski
     * Once map is ready we take the latlng of the entered address and map to that location, noted with
     * a marker.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng location = new LatLng(addrLat, addrLng);
        mMap.addMarker(new MarkerOptions().position(location).title("Marker on Address"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        distanceFromCurLocation();
    }


    /* Written by: Sarah Kolanowski
     * Calculate distance between 2 points based off of Haversine formula. Once the distance is
     * calculated we convert to miles (from kilometers). We the print the distance in miles in a
     * toast notification to the user
     */
    private void distanceFromCurLocation() {
        double youLat, youLng, conLat, conLng;
        youLat = Math.toRadians(newlat);
        youLng = Math.toRadians(newlng);
        conLat = Math.toRadians(addrLat);
        conLng = Math.toRadians(addrLng);

        //change in latitude
        double cngLat = (youLat - conLat);
        double cngLng = (youLng - conLng);
        double a = (Math.pow(Math.sin(cngLat/2), 2) + Math.cos(youLat) * Math.cos(conLat) * Math.pow(Math.sin(cngLng/2), 2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double earthRadius = 6371;
        distance = earthRadius * c; //ans in kilometers

        //convert kilometers to meters
        //approx 0.6213 miles per kilometer
        double mileToKM = 0.621371;
        //now we have our distance point to point in miles
        distance *= mileToKM;

        Toast.makeText(MapsActivity.this, "Distance from contact address in miles: " + distance, Toast.LENGTH_LONG).show();
    }
}
