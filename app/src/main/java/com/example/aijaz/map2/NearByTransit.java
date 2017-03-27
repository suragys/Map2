package com.example.aijaz.map2;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

/**
 * Created by aijaz on 3/19/17.
 */

public class NearByTransit {

    private LatLng origin;
    private LatLng pos;
    private LatLng destnation;
    private int cycTimeInMin;
    private String cycDuration;
    private String url;
    private int totalTimeInMin;
    private String transitTime;

    private JSONObject apiResponse;
    private PolylineOptions polyLineOptions;
    private PolylineOptions polylineOptionsBetweenTransits;
    private PolylineOptions polylineOptionsBetweenDestTransitAndDest;

    public PolylineOptions getPolylineOptionsBetweenDestTransitAndDest() {
        return polylineOptionsBetweenDestTransitAndDest;
    }

    public void setPolylineOptionsBetweenDestTransitAndDest(PolylineOptions polylineOptionsBetweenDestTransitAndDest) {
        this.polylineOptionsBetweenDestTransitAndDest = polylineOptionsBetweenDestTransitAndDest;
    }

    public int getTotalTimeInMin() {
        return totalTimeInMin;
    }

    public void setTotalTimeInMin(int totalTimeInMin) {
        this.totalTimeInMin = totalTimeInMin;
    }

    public String getTransitTime() {
        return transitTime;
    }

    public void setTransitTime(String transitTime) {
        this.transitTime = transitTime;
    }

    public PolylineOptions getPolylineOptionsBetweenTransits() {
        return polylineOptionsBetweenTransits;
    }

    public void setPolylineOptionsBetweenTransits(PolylineOptions polylineOptionsBetweenTransits) {

        this.polylineOptionsBetweenTransits = polylineOptionsBetweenTransits;


    }

    public NearByTransit(LatLng origin, LatLng pos) {
        this.origin = origin;
        this.pos = pos;
        this.url = Utility.getUrl(origin,pos,"bicycling",0);

    }

    public LatLng getOrigin() {
        return origin;
    }

    public void setOrigin(LatLng origin) {
        this.origin = origin;
    }

    public LatLng getPos() {
        return pos;
    }

    public void setPos(LatLng pos) {
        this.pos = pos;
    }

    public LatLng getDestnation() {
        return destnation;
    }

    public void setDestnation(LatLng destnation) {
        this.destnation = destnation;
    }

    public int getCycTimeInMin() {
        return cycTimeInMin;
    }

    public void setCycTimeInMin(int cycTimeInMin) {
        this.cycTimeInMin = cycTimeInMin;
    }

    public String getCycDuration() {
        return cycDuration;
    }

    public void setCycDuration(String cycDuration) {
        this.cycDuration = cycDuration;
    }

    public JSONObject getApiResponse() {
        return apiResponse;
    }

    public void setApiResponse(JSONObject apiResponse) {
        this.apiResponse = apiResponse;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public void setPolyLineOptions(PolylineOptions polyLineOptions) {
        this.polyLineOptions = polyLineOptions;
    }

    public PolylineOptions getPolyLineOptions() {
        return polyLineOptions;
    }
}
