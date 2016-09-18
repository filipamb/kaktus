package pl.atendesoftware.amitogo.activities;

import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Ja on 2016-09-18.
 */
public class MeterPointWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mWindow;
    private final View mContents;

    public MeterPointWindowAdapter(View mWindow, View mContents) {
        this.mWindow = mWindow;
        this.mContents = mContents;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
