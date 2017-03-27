package com.example.aijaz.map2;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by surag on 3/19/17.
 */

public class Utility {
    private static int PROXIMITY_RADIUS = 4000;
    public static String getUrl(double latitude, double longitude, String nearbyPlace) {

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
//        googlePlacesUrl.append(("&rankby=" + "prominence"));
        googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + "AIzaSyDIdqc_BwvnRC24_FwS4-oSITnKFT1N5AY");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }

    public static String getUrl(LatLng origin, LatLng dest, String mode, int offsetTime) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        mode = "mode=" + mode;

        long time = System.currentTimeMillis() / 1000l;
        time += offsetTime;
        String departure_time = "departure_time=" + time;

        String key = "key=" + "AIzaSyCWErH4xncbeiqt-yXmeEFHRSApbjglCKA";

        // Building the parameters to the web service
        String parameters = key + "&" + str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&" + departure_time;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    public static void getBikeAndTransitRoutes(LatLng dest, ArrayList<NearByTransit> nearByTransitArrayList, GoogleMap map, Location origin, Context applicationContext) {


        LatLng source = new LatLng(origin.getLatitude(), origin.getLongitude());
        Object[] DataTransfer = new Object[5];
        DataTransfer[0] = map;
        DataTransfer[1] = source;
        DataTransfer[2] = dest;
        DataTransfer[3] = nearByTransitArrayList;
        DataTransfer[4] = applicationContext;

        GetBikeAndTransitRoutes getBikeAndTransitRoutes = new GetBikeAndTransitRoutes();
        getBikeAndTransitRoutes.execute(DataTransfer);
        Log.d("Get_Duration_Dest", "Got Duration for total travel");


    }

    public static int getTimeInMin(String s) {
        if (s == null) return 0;
        s = s.trim();
        String[] a = s.split(" ");
        int totalTime = 0;
        for (int i = 1; i < a.length; i += 2) {
            String unit = a[i];
            int time = Integer.parseInt(a[i - 1]);
            if (unit.contains("h")) {
                time = time * 60;
            }
            totalTime += time;
        }
        return totalTime;
    }
}
