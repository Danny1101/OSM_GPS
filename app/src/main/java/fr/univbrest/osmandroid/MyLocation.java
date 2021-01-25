package fr.univbrest.osmandroid;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class MyLocation implements LocationListener {

    private LocationManager locationManager;
    private Location location = null;
    private boolean _init_ok = false;
    private Context context;

    public MyLocation(Context context, int period_ms) {
        locationManager = (LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);
        this.context = context;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d("debug", "GPS access not granted");
            Toast.makeText(context,
                    "Veuillez autoriser l'accès GPS !",
                    Toast.LENGTH_LONG).show();
        } else {
            Log.d("debug", "GPS access granted");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 1, this);
            _init_ok = true;
        }

    }

    public boolean init_is_ok() {
        return _init_ok;
    }

    private Location getLastKnownLocation() {
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    public Location getLocation() {
        Location out=getLastKnownLocation();
        Log.d("debug", "getLocation() returning : " + out );
        return out;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("debug", "onLocationChanged() called");
        //this.location = location;
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("debug","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("debug","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("debug","status");
    }
}
