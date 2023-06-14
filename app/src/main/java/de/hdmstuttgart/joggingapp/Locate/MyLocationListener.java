package de.hdmstuttgart.joggingapp.Locate;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import de.hdmstuttgart.joggingapp.ui.main.Fragments.WorkoutFragment;

/*---------- Listener class to get coordinates ------------- */
public class MyLocationListener implements LocationListener {

    private Context context;

    public MyLocationListener(Context context) {
        this.context = context;
    }

    /**
     * Reads current longitude and latitude and adds it to the List.
     *
     * @param loc location
     */
    @Override
    public void onLocationChanged(Location loc) {
        double latitude = loc.getLatitude();
        double longitude = loc.getLongitude();
        double[] position = {longitude, latitude};

        WorkoutFragment.positions.add(position);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }
}
