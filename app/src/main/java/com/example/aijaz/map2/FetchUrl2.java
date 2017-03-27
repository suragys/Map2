package com.example.aijaz.map2;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;

/**
 * Created by aijaz on 3/19/17.
 */

// Fetches data from url passed
public class FetchUrl2 extends AsyncTask<Object, String, String> {
    GoogleMap mMap;
    String url;
    NearByTransit nearByTransit;
    boolean isDestDuration;

    @Override
    protected String doInBackground(Object... params) {

        // For storing data from web service
        String data = "";

            try {
                url = (String) params[0];

                // Fetching the data from web service
                DownloadUrl downloadUrl = new DownloadUrl();
                data = downloadUrl.readUrl(url);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }

        return data;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        Log.d(FetchUrl2.class.getSimpleName(),"The result string is " + result.substring(0,20));
    }
}
