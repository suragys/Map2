package com.example.aijaz.map2;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;

import java.io.IOException;

/**
 * Created by aijaz on 3/19/17.
 */

// Fetches data from url passed
public class FetchUrl extends AsyncTask<Object, String, String> {
    GoogleMap mMap;
    String url;
    NearByTransit nearByTransit;
    boolean isDestDuration;

    @Override
    protected String doInBackground(Object... params) {

        // For storing data from web service
        String data = "";
        if (params.length == 3) {
            try {
                mMap = (GoogleMap) params[0];
                url = (String) params[1];
                nearByTransit = (NearByTransit) params[2];
                // Fetching the data from web service
                DownloadUrl downloadUrl = new DownloadUrl();
//            data = downloadUrl.readUrl(url[0]);
                data = downloadUrl.readUrl(url);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        } else if (params.length == 1) {
            url = (String) params[0];
            DownloadUrl downloadUrl = new DownloadUrl();
            try {
                data = downloadUrl.readUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
//                mMap = (GoogleMap) params[0];
                url = (String) params[1];
                nearByTransit = (NearByTransit) params[2];
                isDestDuration = (boolean) params[3];
                // Fetching the data from web service
                DownloadUrl downloadUrl = new DownloadUrl();
//            data = downloadUrl.readUrl(url[0]);
                data = downloadUrl.readUrl(url);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
        }
        return data;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (isDestDuration) {
            Object[] DataTransfer = new Object[4];
            DataTransfer[0] = mMap;
            DataTransfer[1] = result;
            DataTransfer[2] = nearByTransit;
            DataTransfer[3] = isDestDuration;

            if (!result.contains("error_message")) {
                ParserTask parserTask = new ParserTask();


                // Invokes the thread for parsing the JSON data
                parserTask.execute(DataTransfer);
                Log.d("Fetch Url Success", "number for" + nearByTransit.getPos().toString());
            } else {

                Log.d("Fetch Url Error", "number for" + nearByTransit.getPos().toString());
            }

        } else if (!isDestDuration) {
            Object[] DataTransfer = new Object[3];
            DataTransfer[0] = mMap;
            DataTransfer[1] = result;
            DataTransfer[2] = nearByTransit;

//        ParserTime parserTime = new ParserTime();
//
//        parserTime.execute(DataTransfer);
            if (!result.contains("error_message")) {
                ParserTask parserTask = new ParserTask();


                // Invokes the thread for parsing the JSON data
                parserTask.execute(DataTransfer);
                Log.d("Fetch Url Success", "number for" + nearByTransit.getPos().toString());
            } else {

                Log.d("Fetch Url Error", "number for" + nearByTransit.getPos().toString());
            }

        } else {
            Log.d("Fetch Url Success", result.substring(0, 20));
        }
    }
}
