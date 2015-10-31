package me.numeros.numeroslib;

import android.content.Context;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Abraham on 28/07/2015.
 */
public class GPSListener implements GpsStatus.Listener, LocationListener {

    private static final long DURATION_TO_FIX_LOST_MS = 10000;
    private static final long MINIMUM_UPDATE_TIME = 0;
    private static final float MINIMUM_UPDATE_DISTANCE = 0.0f;
    private static GPSListener ourInstance;
    public boolean gpsEnabled;
    public boolean gpsFix;
    public int satellitesTotal;
    public int satellitesUsed;
    // the last location time is needed to determine if a fix has been lost
    public long locationTime = 0;
    public Location location;
    public float accuracy;
    private ArrayList<ILocationListener> _locationListeners;
    private LocationManager locationManager;
    private List<Float> rollingAverageData = new LinkedList<Float>();

    private GPSListener(Context context) {
        _locationListeners = new ArrayList<ILocationListener>();

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // ask for updates on the GPS status
        locationManager.addGpsStatusListener(this);
        // ask for updates on the GPS location
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                MINIMUM_UPDATE_TIME, MINIMUM_UPDATE_DISTANCE, this);

        GPSTracker tracker = new GPSTracker(context);
        if (tracker.canGetLocation()) {
            location = new Location("GPS location");
            location.setLatitude(tracker.getLatitude());
            location.setLongitude(tracker.getLongitude());
        }
    }

    public static GPSListener getInstance(Context context) {
        if (ourInstance == null)
            ourInstance = new GPSListener(context);

        return ourInstance;
    }

    public void addListener(ILocationListener listener) {
        if (!_locationListeners.contains(listener))
            _locationListeners.add(listener);
    }

    public void removeListener(ILocationListener listener) {
        if (_locationListeners.contains(listener))
            _locationListeners.remove(listener);
    }

    @Override
    public void onGpsStatusChanged(int changeType) {
        if (locationManager != null) {

            // status changed so ask what the change was
            GpsStatus status = locationManager.getGpsStatus(null);
            switch (changeType) {
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    gpsEnabled = true;
                    gpsFix = true;
                    break;
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    gpsEnabled = true;
                    // if it has been more then 10 seconds since the last update, consider the fix lost
                    gpsFix = System.currentTimeMillis() - locationTime < DURATION_TO_FIX_LOST_MS;
                    break;
                case GpsStatus.GPS_EVENT_STARTED: // GPS turned on
                    gpsEnabled = true;
                    gpsFix = false;
                    break;
                case GpsStatus.GPS_EVENT_STOPPED: // GPS turned off
                    gpsEnabled = false;
                    gpsFix = false;
                    break;
                default:
                    Log.w("GPSListener", "unknown GpsStatus event type. " + changeType);
                    return;
            }

            // number of satellites, not useful, but cool
            int newSatTotal = 0;
            int newSatUsed = 0;
            for (GpsSatellite sat : status.getSatellites()) {
                newSatTotal++;
                if (sat.usedInFix()) {
                    newSatUsed++;
                }
            }
            satellitesTotal = newSatTotal;
            satellitesUsed = newSatUsed;

            updateView();
        }
    }

    private void updateView() {
        for (ILocationListener listener : _locationListeners)
            listener.onLocationChanged(location);
    }

    @Override
    public void onLocationChanged(Location location) {
        locationTime = location.getTime();

        if (location.hasAccuracy()) {
            // rolling average of accuracy so "Signal Quality" is not erratic
            updateRollingAverage(location.getAccuracy());
        }

        updateView();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
            /* dont need this info */
    }

    @Override
    public void onProviderEnabled(String provider) {
            /* dont need this info */
    }

    @Override
    public void onProviderDisabled(String provider) {
            /* dont need this info */
    }

    private void updateRollingAverage(float value) {
        // does a simple rolling average
        rollingAverageData.add(value);
        if (rollingAverageData.size() > 10) {
            rollingAverageData.remove(0);
        }

        float average = 0.0f;
        for (Float number : rollingAverageData) {
            average += number;
        }
        average = average / rollingAverageData.size();

        accuracy = average;
    }
}
