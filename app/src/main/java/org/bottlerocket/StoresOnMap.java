package org.bottlerocket;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/*This activity shows all the stores on the map--
* Draws the icons for stores on the map at the given location*/
public class StoresOnMap extends AppCompatActivity implements GoogleMap.OnMapClickListener,FragmentMap.OnDataPass{

    MapView mapView;
    GoogleMap googleMap;
    Marker m;
    ArrayList<Store> stores;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stores_on_map);
        Toolbar toolbar = findViewById(R.id.toolbar);
        //((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        setSupportActionBar(toolbar);

        //Fancy Toolbar
        //getSupportActionBar().setIcon(R.drawable.shopping_cart);
        getSupportActionBar().setTitle("Stores on Map");
        //setTitle("Stores on Map");

        //FragmentMap fragobj = FragmentMap.newInstance(stores);
        FragmentMap fragobj=new FragmentMap();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mapLayout, fragobj);
        fragmentTransaction.commit();
    }

    @Override
    public void onMapClick(LatLng point) {
        Toast.makeText(this, "Tapped location is:"+point, Toast.LENGTH_LONG).show();
        //mTapTextView.setText("tapped, point=" + point);
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        super.onBackPressed();
    }

    public void onDataPass(LatLng data) {
        Log.d("LOG","Received Latlng value of selected point to this activity is: " + data.latitude+","+data.longitude);
    }
}
