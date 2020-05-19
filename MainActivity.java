package com.example.abhiramtamvadagps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
TextView textView;
double lat;
double longe;
int x;
double summer;
Location loc;
LocationListener locationListener;
LocationManager locationManager;
ArrayList<String> adder;
ArrayList<String> times;
ListView addresse;
ListView timer;
CustomAdapter customAdapter;
CustomAdapte custom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.Text);
        lat = 0;
        longe = 0;
        x = 0;
        loc = null;
        adder = new ArrayList<>();
        addresse = findViewById(R.id.listaddie);
        timer = findViewById(R.id.listtime);
        times = new ArrayList<>();
        summer = 0.0;
        locationManager =  (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
         locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 0 );
                }
                textView.setText("Lat: "+location.getLatitude()+" Long: "+location.getLongitude());
                Log.d("CHECK", "WORKS ");
                lat = location.getLatitude();
                longe = location.getLongitude();
                TextView addie = findViewById(R.id.addie);
                try {
                    Geocoder geo = new Geocoder(MainActivity.this, Locale.US );
                    List<Address> addresses = geo.getFromLocation(lat, longe, 3);
                    System.out.println(addresses);
                    System.out.println("Address: " + addresses.get(0));
                    addie.setText("Address: "+addresses.get(0).getAddressLine(0));
                    String adz = addresses.get(0).getAddressLine(0);
                    Log.d("ADDED", "3es3s: "+adz);
                    System.out.println(adder);
                    adder.add(adz);
                    TextView distance = findViewById(R.id.distance);
                    if((loc != null)&&(SystemClock.elapsedRealtime()!=0.0)) {
                        summer += loc.distanceTo(location);
                        times.add(((int)(SystemClock.elapsedRealtime()/1000))+"");
                        Log.d("timer", "3es3s: "+times);
                        customAdapter = new CustomAdapter(MainActivity.this, R.layout.adapter_customadd ,adder);
                        addresse.setAdapter(customAdapter);
                        customAdapter.notifyDataSetChanged();
                        custom = new CustomAdapte(MainActivity.this, R.layout.adapter_custom ,times);
                        timer.setAdapter(custom);
                        custom.notifyDataSetChanged();
                    }
                    loc = location;
                    distance.setText("Distance: "+summer+" Meters");
                }catch (Exception e){
                    Log.d("CHECK", "FAILS ");
                    e.printStackTrace();
                }
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

         Button button = findViewById(R.id.button);
         button.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 summer -= summer;
                 TextView distance = findViewById(R.id.distance);
                 distance.setText("Distance: "+summer+" Meters");
             }
         });
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 0 );
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1f, locationListener);
    }
        public void onDestroy(){
            super.onDestroy();
            locationManager.removeUpdates(locationListener);
        }

    public class CustomAdapter extends ArrayAdapter<String> {
        Context parentConetext;
        List<String> list;
        int xmlResources;


        public CustomAdapter(@NonNull Context context, int resource, @NonNull List objects) {
            super(context, resource, objects);
            parentConetext = context;
            xmlResources = resource;
            list = objects;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) parentConetext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View adapterView = layoutInflater.inflate(xmlResources, null);
            TextView poser = adapterView.findViewById(R.id.address);
            poser.setText(""+list.get(position));
            return adapterView;
            }
        }
    public class CustomAdapte extends ArrayAdapter<Double> {
        Context parentConetext;
        List<String> list;
        int xmlResources;


        public CustomAdapte(@NonNull Context context, int resource, @NonNull List objects) {
            super(context, resource, objects);
            parentConetext = context;
            xmlResources = resource;
            list = objects;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) parentConetext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View adapterView = layoutInflater.inflate(xmlResources, null);
            TextView poser = adapterView.findViewById(R.id.time);
            int zero = 0;
            if((position-1)>=0) {
                zero = Integer.parseInt(times.get(position - 1));
            }else{
                zero = Integer.parseInt(times.get(0));
            }
            int actual = Integer.parseInt(list.get(position));
            actual -= zero;
            poser.setText((String.valueOf(actual))+ " Seconds");
            return adapterView;
        }
    }
}
