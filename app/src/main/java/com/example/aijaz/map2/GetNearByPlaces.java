package com.example.aijaz.map2;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by navneet on 23/7/16.
 */
public class GetNearByPlaces extends AsyncTask<Object, String, String> {

    String googlePlacesData;
    GoogleMap mMap;
    String url;
    Location myLocation;
    ArrayList<NearByTransit> nearByTransitArrayList;

    @Override
    protected String doInBackground(Object... params) {
        try {
            Log.d("GetBikeAndTransitRoutes", "doInBackground entered");
            mMap = (GoogleMap) params[0];
            url = (String) params[1];
            myLocation = (Location) params[2];
            nearByTransitArrayList = (ArrayList<NearByTransit>) params[3];
            DownloadUrl downloadUrl = new DownloadUrl();
            googlePlacesData = downloadUrl.readUrl(url);
            Log.d("GooglePlacesReadTask", "doInBackground Exit");
        } catch (Exception e) {
            Log.d("GooglePlacesReadTask", e.toString());
        }
        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d("GooglePlacesReadTask", "onPostExecute Entered");
        List<HashMap<String, String>> nearbyPlacesList = null;
        DataParser dataParser = new DataParser();
        nearbyPlacesList = dataParser.parse(result);
        ShowNearbyPlaces(nearbyPlacesList);
        Log.d("GooglePlacesReadTask", "onPostExecute Exit");
    }

    private void ShowNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList) {
        Log.e("SIZE of nearby list====", "" + nearbyPlacesList.size());
        for (int i = 0; i < nearbyPlacesList.size(); i++) {
            Log.d("onPostExecute", "Entered into showing locations");
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = nearbyPlacesList.get(i);
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));
            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");
//            LatLng latLng = new LatLng(lat, lng);
//            markerOptions.position(latLng);
//            markerOptions.title(placeName + " : " + vicinity);
//            mMap.addMarker(markerOptions);
//            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//            //move map camera
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            LatLng origin = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
            LatLng dest = new LatLng(lat,lng);


            // Getting URL to the Google Directions API
            String url = Utility.getUrl(origin, dest,"bicycling",0);

            Object[] DataTransfer = new Object[3];
            DataTransfer[0] = mMap;
            DataTransfer[1] = url;


            NearByTransit nearByTransit = new NearByTransit(origin,dest);
            nearByTransitArrayList.add(nearByTransit);
            Log.d("On_get_nearby_places", "added nearByTransit" + nearByTransitArrayList.size());
            DataTransfer[2] = nearByTransit;
            FetchUrl FetchUrl = new FetchUrl();

            // Start downloading json data from Google Directions API
//            FetchUrl.execute(DataTransfer);
            //move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        }
    }
}
