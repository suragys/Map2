package com.example.aijaz.map2;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by navneet on 23/7/16.
 */
public class GetBikeAndTransitRoutes extends AsyncTask<Object, String, String> {

    String googlePlacesData;
    GoogleMap mMap;

    ArrayList<NearByTransit> nearByTransitArrayList;
    LatLng source;
    LatLng dest;
    Polyline p1;
    Polyline p2;
    Polyline p3;
    Context applicationContext;

    @Override
    protected String doInBackground(Object... params) {
        try {
            Log.d("GetBikeAndTransitRoutes", "doInBackground entered");

            mMap = (GoogleMap) params[0];
            source = (LatLng) params[1];
            dest = (LatLng) params[2];
            nearByTransitArrayList = (ArrayList<NearByTransit>) params[3];
            applicationContext = (Context) params[4];

            String url = Utility.getUrl(source.latitude, source.longitude, "transit_station");


            DownloadUrl downloadUrl = new DownloadUrl();
            googlePlacesData = downloadUrl.readUrl(url);
            Log.d("GetBikeAndTransitRoutes", "doInBackground Exit");
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
        addDefaultCyclingTime();
        ShowNearbyPlaces(nearbyPlacesList);
        computeTop2(nearByTransitArrayList);

        Log.d("GooglePlacesReadTask", "onPostExecute Exit");

    }

    private void addDefaultCyclingTime() {
        Log.d(this.getClass().getSimpleName(), "Calculating full cycling time");
        NearByTransit n = new NearByTransit(source, dest);
        getCyclingTime(n);
        n.setTransitTime("0 mins");
        n.setTotalTimeInMin(Utility.getTimeInMin(n.getCycDuration()));
        nearByTransitArrayList.add(n);
        Log.d(this.getClass().getSimpleName(), "full cycling time = " + n.getTotalTimeInMin());

    }

    private void computeTop2(ArrayList<NearByTransit> nearByTransitArrayList) {
        for (int i = 0; i < nearByTransitArrayList.size(); i++) {
            NearByTransit n = nearByTransitArrayList.get(i);
            int totalTime = 0;
            if (n.getCycDuration() != null && n.getTransitTime() != null) {
                int c = 0;
                int t = 0;
                if (n.getCycDuration() != null) {
                    String cycTime = n.getCycDuration();
                    c = Utility.getTimeInMin(cycTime);
                }
                if (n.getTransitTime() != null) {
                    String transTime = n.getTransitTime();
                    t = Utility.getTimeInMin(transTime);
                }
                totalTime = c + t;
                n.setCycTimeInMin(c);
                n.setTotalTimeInMin(totalTime);
            }
        }

        NearByTransit min1 = nearByTransitArrayList.get(0);
        for (NearByTransit n : nearByTransitArrayList) {
            Log.e("Total time====", "" + n.getTotalTimeInMin());
            if (n.getTotalTimeInMin() != 0 && n.getTotalTimeInMin() < min1.getTotalTimeInMin()) {
                min1 = n;
            }
        }

        Log.d(this.getClass().getSimpleName(), " The best time is " + min1.getTotalTimeInMin());
//        LatLngBounds.Builder bc = new LatLngBounds.Builder();
//        int totalPoints = 0;
        if (min1.getPolyLineOptions() != null) {
            if (p1 != null) p1.remove();
            p1 = mMap.addPolyline(min1.getPolyLineOptions());
//            List<LatLng> points = p1.getPoints(); // route is instance of PolylineOptions
//            totalPoints += points.size();
//            for (LatLng item : points) {
//                bc.include(item);
//            }
        }

        if (min1.getPolylineOptionsBetweenTransits() != null) {
            if (p2 != null) p2.remove();
            p2 = mMap.addPolyline(min1.getPolylineOptionsBetweenTransits());
//            List<LatLng> points = p1.getPoints(); // route is instance of PolylineOptions
//            totalPoints += points.size();
//            for (LatLng item : points) {
//                bc.include(item);
//            }

        }

        if (min1.getPolylineOptionsBetweenDestTransitAndDest() != null) {
            if (p3 != null) p3.remove();
            p3 = mMap.addPolyline(min1.getPolylineOptionsBetweenDestTransitAndDest());
//            List<LatLng> points = p1.getPoints(); // route is instance of PolylineOptions
//            totalPoints += points.size();
//            for (LatLng item : points) {
//                bc.include(item);
//            }

        }


//        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 1000));

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(source);
        builder.include(dest);
        LatLngBounds bounds = builder.build();

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 50);
        mMap.animateCamera(cu, new GoogleMap.CancelableCallback() {
            public void onCancel() {
            }

            public void onFinish() {
                CameraUpdate zout = CameraUpdateFactory.zoomBy((float) -0.5);
                mMap.animateCamera(zout);
            }
        });

        Toast.makeText(applicationContext, "The total travel is " + min1.getTotalTimeInMin() + "mins", Toast.LENGTH_LONG).show();
        Toast.makeText(applicationContext, "The total travel is " + min1.getTotalTimeInMin() + "mins", Toast.LENGTH_LONG).show();

    }

    private void ShowNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList) {
        Log.e("SIZE of nearby list====", "" + nearbyPlacesList.size());
        for (int i = 0; i < nearbyPlacesList.size(); i++) {
//            Log.d("onPostExecute", "Entered into showing locations");

            HashMap<String, String> googlePlace = nearbyPlacesList.get(i);
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));

            NearByTransit nearByTransit = new NearByTransit(source, new LatLng(lat, lng));
            getCyclingTime(nearByTransit);
            getDestTime(nearByTransit);
            nearByTransitArrayList.add(nearByTransit);


//            Object[] DataTransfer = new Object[3];
//            DataTransfer[0] = mMap;
//            DataTransfer[1] = url;
//
//
//            NearByTransit nearByTransit = new NearByTransit(origin,dest,url);
//            nearByTransitArrayList.add(nearByTransit);
//            Log.d("On_get_nearby_places", "added nearByTransit" + nearByTransitArrayList.size());
//            DataTransfer[2] = nearByTransit;
//            FetchUrl FetchUrl = new FetchUrl();
//
//            // Start downloading json data from Google Directions API
//            FetchUrl.execute(DataTransfer);
//            //move map camera
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
//            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        }
    }


    private void getDestTime(NearByTransit nearByTransit) {
        if (nearByTransit.getCycDuration() == null) {
            Log.e(this.getClass().getSimpleName(), " The cycle duration for " + nearByTransit.getPos().toString() + " is not present");
            return;
        }
        String jsonData = "";

        try {

            // do the same for the destination
            String url = Utility.getUrl(dest.latitude, dest.longitude, "transit_station");
//            DownloadUrl downloadUrl = new DownloadUrl();
//            googlePlacesData = downloadUrl.readUrl(url);
            FetchUrl2 fetchUrl2 = new FetchUrl2();
            fetchUrl2.execute(url);
            String googlePlacesDataString = fetchUrl2.get();
            List<HashMap<String, String>> nearbyPlacesList = null;
            DataParser dataParser = new DataParser();
            nearbyPlacesList = dataParser.parse(googlePlacesDataString);
//            addDefaultCyclingTime();
//            ShowNearbyPlaces(nearbyPlacesList);
            int totalTime = Integer.MAX_VALUE;
            for (int i = 0; i < nearbyPlacesList.size(); i++) {
                HashMap<String, String> googlePlace = nearbyPlacesList.get(i);
                double lat = Double.parseDouble(googlePlace.get("lat"));
                double lng = Double.parseDouble(googlePlace.get("lng"));
                LatLng destTransit = new LatLng(lat, lng);
//                NearByTransit nearByTransit = new NearByTransit(source, new LatLng(lat, lng));
                int fc = Utility.getTimeInMin(nearByTransit.getCycDuration());
                int cycTime = getCyclingTime(destTransit, dest);
                int transTime = getDestTime(nearByTransit.getPos(), destTransit);

                if (totalTime > fc + cycTime + transTime) {
                    totalTime = fc + cycTime + transTime;

                    // set poly lines between transits
                    FetchUrl2 fetchUrl = new FetchUrl2();
                    url = Utility.getUrl(nearByTransit.getPos(), destTransit, "transit", Utility.getTimeInMin(nearByTransit.getCycDuration()));
                    Log.d("GetBikeAnTransitRoute", url);
                    fetchUrl.execute(url);
                    jsonData = fetchUrl.get();
                    if (!jsonData.contains("error_message")) {
                        Log.d("Fetch Url Success", "number for" + nearByTransit.getPos().toString());
                        JSONObject jsonObject = new JSONObject(jsonData);
                        Log.d("ParserTask", jsonData.toString());
                        DataParser2 parser = new DataParser2();
                        // Starts parsing data
                        String json = jsonObject.toString();
                        List<List<HashMap<String, String>>> routes = parser.parse(jsonObject);
                        String time = parser.getDurations(jsonObject);
                        nearByTransit.setTransitTime(time);
                        Log.d("ParserTask", "Executing routes");
                        Log.d("ParserTask", routes.toString());
                        ArrayList<LatLng> points;
                        PolylineOptions lineOptions = null;

                        // Traversing through all the routes
                        for (int j = 0; j < routes.size(); j++) {
                            points = new ArrayList<>();
                            lineOptions = new PolylineOptions();

                            // Fetching i-th route
                            List<HashMap<String, String>> path = routes.get(j);

                            // Fetching all the points in i-th route
                            for (int l = 0; l < path.size(); l++) {
                                HashMap<String, String> point = path.get(l);

                                double lat1 = Double.parseDouble(point.get("lat"));
                                double lng2 = Double.parseDouble(point.get("lng"));
                                LatLng position = new LatLng(lat1, lng2);

                                points.add(position);
                            }

                            // Adding all the points in the route to LineOptions
                            lineOptions.addAll(points);
                            lineOptions.width(11);
                            lineOptions.color(Color.RED);

                            Log.d("onPostExecute", "onPostExecute line options decoded");

                        }

                        nearByTransit.setPolylineOptionsBetweenTransits(lineOptions);
                    }

                    // set poly lines between destTransit and dest

                    jsonData = "";


                    FetchUrl2 fetchUrl3 = new FetchUrl2();
                    url = Utility.getUrl(destTransit, dest, "bicycling", 0);
                    Log.d("GetBikeAnTransitRoute", url);
                    fetchUrl3.execute(url);
                    jsonData = fetchUrl3.get();
                    if (!jsonData.contains("error_message")) {
                        Log.d("Fetch Url Success", "number for" + nearByTransit.getPos().toString());
                        JSONObject jsonObject = new JSONObject(jsonData);
                        Log.d("ParserTask", jsonData.toString());
                        DataParser2 parser = new DataParser2();

                        // Starts parsing data
                        String json = jsonObject.toString();
                        List<List<HashMap<String, String>>> routes = parser.parse(jsonObject);
                        String time = parser.getDurations(jsonObject);
                        nearByTransit.setCycDuration(time);
                        Log.d("ParserTask", "Executing routes");
                        Log.d("ParserTask", routes.toString());
                        ArrayList<LatLng> points;
                        PolylineOptions lineOptions = null;

                        // Traversing through all the routes
                        for (int k = 0; k < routes.size(); k++) {
                            points = new ArrayList<>();
                            lineOptions = new PolylineOptions();

                            // Fetching i-th route
                            List<HashMap<String, String>> path = routes.get(k);

                            // Fetching all the points in i-th route
                            for (int j = 0; j < path.size(); j++) {
                                HashMap<String, String> point = path.get(j);

                                double la = Double.parseDouble(point.get("lat"));
                                double ln = Double.parseDouble(point.get("lng"));
                                LatLng position = new LatLng(la, ln);

                                points.add(position);
                            }

                            // Adding all the points in the route to LineOptions
                            lineOptions.addAll(points);
                            lineOptions.width(11);
                            lineOptions.color(Color.BLUE);


                            Log.d("onPostExecute", "onPostExecute line options decoded");

                        }

                        nearByTransit.setPolylineOptionsBetweenDestTransitAndDest(lineOptions);

                    } else {
                        Log.d("Fetch Url Error", "number for" + nearByTransit.getPos().toString());
                    }
                }
            }


//            jsonData = downloadUrl.readUrl(nearByTransit.getUrl());
//            FetchUrl2 fetchUrl = new FetchUrl2();
//            url = Utility.getUrl(nearByTransit.getPos(), dest, "transit", Utility.getTimeInMin(nearByTransit.getCycDuration()));
//            Log.d("GetBikeAnTransitRoute", url);
//            fetchUrl.execute(url);
//            jsonData = fetchUrl.get();
//            if (!jsonData.contains("error_message")) {
//                Log.d("Fetch Url Success", "number for" + nearByTransit.getPos().toString());
//                JSONObject jsonObject = new JSONObject(jsonData);
//                Log.d("ParserTask", jsonData.toString());
//                DataParser2 parser = new DataParser2();
//                // Starts parsing data
//                String json = jsonObject.toString();
//                List<List<HashMap<String, String>>> routes = parser.parse(jsonObject);
//                String time = parser.getDurations(jsonObject);
//                nearByTransit.setTransitTime(time);
//                Log.d("ParserTask", "Executing routes");
//                Log.d("ParserTask", routes.toString());
//                ArrayList<LatLng> points;
//                PolylineOptions lineOptions = null;
//
//                // Traversing through all the routes
//                for (int i = 0; i < routes.size(); i++) {
//                    points = new ArrayList<>();
//                    lineOptions = new PolylineOptions();
//
//                    // Fetching i-th route
//                    List<HashMap<String, String>> path = routes.get(i);
//
//                    // Fetching all the points in i-th route
//                    for (int j = 0; j < path.size(); j++) {
//                        HashMap<String, String> point = path.get(j);
//
//                        double lat = Double.parseDouble(point.get("lat"));
//                        double lng = Double.parseDouble(point.get("lng"));
//                        LatLng position = new LatLng(lat, lng);
//
//                        points.add(position);
//                    }
//
//                    // Adding all the points in the route to LineOptions
//                    lineOptions.addAll(points);
//                    lineOptions.width(11);
//                    lineOptions.color(Color.RED);
//
//                    Log.d("onPostExecute", "onPostExecute line options decoded");
//
//                }
//
//                nearByTransit.setPolylineOptionsBetweenTransits(lineOptions);
//            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    private int getDestTime(LatLng source, LatLng dest) {
        String jsonData = "";

        try {

            FetchUrl2 fetchUrl = new FetchUrl2();
            String url = Utility.getUrl(source, dest, "bicycling", 0);
            Log.d("GetBikeAnTransitRoute", url);
            fetchUrl.execute(url);
            jsonData = fetchUrl.get();
            if (!jsonData.contains("error_message")) {
                Log.d("Fetch Url Success", "number for" + dest.toString());
                JSONObject jsonObject = new JSONObject(jsonData);
                Log.d("ParserTask", jsonData.toString());
                DataParser2 parser = new DataParser2();

                // Starts parsing data
                String json = jsonObject.toString();
//                List<List<HashMap<String, String>>> routes = parser.parse(jsonObject);
                String time = parser.getDurations(jsonObject);

                return Utility.getTimeInMin(time);
            } else {
                Log.d("Fetch Url Error", "number for" + dest.toString());
                return 0;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getCyclingTime(LatLng source, LatLng dest) {
        String jsonData = "";

        try {

            FetchUrl2 fetchUrl = new FetchUrl2();
            String url = Utility.getUrl(source, dest, "bicycling", 0);
            Log.d("GetBikeAnTransitRoute", url);
            fetchUrl.execute(url);
            jsonData = fetchUrl.get();
            if (!jsonData.contains("error_message")) {
                Log.d("Fetch Url Success", "number for" + dest.toString());
                JSONObject jsonObject = new JSONObject(jsonData);
                Log.d("ParserTask", jsonData.toString());
                DataParser2 parser = new DataParser2();

                // Starts parsing data
                String json = jsonObject.toString();
//                List<List<HashMap<String, String>>> routes = parser.parse(jsonObject);
                String time = parser.getDurations(jsonObject);

                return Utility.getTimeInMin(time);
            } else {
                Log.d("Fetch Url Error", "number for" + dest.toString());
                return 0;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void getCyclingTime(NearByTransit nearByTransit) {
        // get the cycling route
        // and cycling time

        String jsonData = "";

        try {

            FetchUrl2 fetchUrl = new FetchUrl2();
            String url = Utility.getUrl(source, nearByTransit.getPos(), "bicycling", 0);
            Log.d("GetBikeAnTransitRoute", url);
            fetchUrl.execute(url);
            jsonData = fetchUrl.get();
            if (!jsonData.contains("error_message")) {
                Log.d("Fetch Url Success", "number for" + nearByTransit.getPos().toString());
                JSONObject jsonObject = new JSONObject(jsonData);
                Log.d("ParserTask", jsonData.toString());
                DataParser2 parser = new DataParser2();

                // Starts parsing data
                String json = jsonObject.toString();
                List<List<HashMap<String, String>>> routes = parser.parse(jsonObject);
                String time = parser.getDurations(jsonObject);
                nearByTransit.setCycDuration(time);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());
                ArrayList<LatLng> points;
                PolylineOptions lineOptions = null;

                // Traversing through all the routes
                for (int k = 0; k < routes.size(); k++) {
                    points = new ArrayList<>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = routes.get(k);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double la = Double.parseDouble(point.get("lat"));
                        double ln = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(la, ln);

                        points.add(position);
                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(11);
                    lineOptions.color(Color.BLUE);


                    Log.d("onPostExecute", "onPostExecute line options decoded");

                }

                nearByTransit.setPolyLineOptions(lineOptions);

            } else {
                Log.d("Fetch Url Error", "number for" + nearByTransit.getPos().toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
