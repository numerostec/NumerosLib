package me.numeros.numeroslib;

import android.location.Location;

import java.util.EventListener;

/**
 * Created by Abraham on 28/07/2015.
 */
public interface ILocationListener extends EventListener {
    void onLocationChanged(Location location);
}