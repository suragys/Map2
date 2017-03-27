package com.example.aijaz.map2;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;

import org.json.JSONObject;

/**
 * Created by aijaz on 3/19/17.
 */

public class ParserTime extends AsyncTask<Object, Integer, String> {
    GoogleMap mMap;
    String jsonData;
    NearByTransit nearByTransit;

    // Parsing the data in non-ui thread
    @Override
    protected String doInBackground(Object... params) {

        JSONObject jObject;
        String time = null;

        try {
            mMap = (GoogleMap) params[0];
            jsonData = (String) params[1];
            nearByTransit = (NearByTransit) params[2];
//            jObject = new JSONObject(jsonData[0]);
//            Log.d("ParserTask",jsonData[0].toString());
            jObject = new JSONObject(jsonData);
            Log.d("ParserTime", jsonData.toString());
            DataParser2 parser = new DataParser2();
            Log.d("ParserTime", parser.toString());

            // Starts parsing data
            String json = jObject.toString();
            time = parser.getDurations(jObject);
            Log.d("ParserTask", "Executing routes");
            Log.d("ParserTask", time.toString());

        } catch (Exception e) {
            Log.d("ParserTask", e.toString());
            e.printStackTrace();
        }
        return time;
    }

    // Executes in UI thread, after the parsing process
    @Override
    protected void onPostExecute(String time) {
        nearByTransit.setCycDuration(time);
    }
}
