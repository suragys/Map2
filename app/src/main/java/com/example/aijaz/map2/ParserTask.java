package com.example.aijaz.map2;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by aijaz on 3/19/17.
 */

public class ParserTask extends AsyncTask<Object, Integer, List<List<HashMap<String, String>>>> {
    GoogleMap mMap;
    String jsonData;
    NearByTransit nearByTransit;
    boolean isDestDuration;
    // Parsing the data in non-ui thread
    @Override
    protected List<List<HashMap<String, String>>> doInBackground(Object... params) {

        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;

        if(params.length == 4){
            try {
//                mMap = (GoogleMap) params[0];
                jsonData = (String) params[1];
                nearByTransit = (NearByTransit) params[2];
                isDestDuration = (boolean) params[3];
//            jObject = new JSONObject(jsonData[0]);
//            Log.d("ParserTask",jsonData[0].toString());
                jObject = new JSONObject(jsonData);
//                nearByTransit.setApiResponse(jObject);
                Log.d("ParserTask", jsonData.toString());
                DataParser2 parser = new DataParser2();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                String json = jObject.toString();
                routes = parser.parse(jObject);
                String time = parser.getDurations(jObject);
                nearByTransit.setTransitTime(time);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        } else {

            try {
                mMap = (GoogleMap) params[0];
                jsonData = (String) params[1];
                nearByTransit = (NearByTransit) params[2];
//            jObject = new JSONObject(jsonData[0]);
//            Log.d("ParserTask",jsonData[0].toString());
                jObject = new JSONObject(jsonData);
                nearByTransit.setApiResponse(jObject);
                Log.d("ParserTask", jsonData.toString());
                DataParser2 parser = new DataParser2();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                String json = jObject.toString();
                routes = parser.parse(jObject);
                String time = parser.getDurations(jObject);
                nearByTransit.setCycDuration(time);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }
    }

    // Executes in UI thread, after the parsing process
    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {
        if(isDestDuration){
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);

                Log.d("onPostExecute", "onPostExecute lineoptions decoded");

            }

            nearByTransit.setPolylineOptionsBetweenTransits(lineOptions);

            // Drawing polyline in the Google Map for the i-th route
//            if (lineOptions != null) {
//                mMap.addPolyline(lineOptions);
//            } else {
//                Log.d("onPostExecute", "without Polylines drawn");
//            }
        } else {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);

                Log.d("onPostExecute", "onPostExecute lineoptions decoded");

            }

            nearByTransit.setPolyLineOptions(lineOptions);

            // Drawing polyline in the Google Map for the i-th route
//            if (lineOptions != null) {
//                mMap.addPolyline(lineOptions);
//            } else {
//                Log.d("onPostExecute", "without Polylines drawn");
//            }
        }
    }
}
