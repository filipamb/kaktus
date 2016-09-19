package pl.atendesoftware.amitogo.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Set;

import pl.atendesoftware.amitogo.R;
import pl.atendesoftware.amitogo.activities.MainActivity;
import pl.atendesoftware.amitogo.activities.MeterPointListActivity;
import pl.atendesoftware.amitogo.activities.StationDetailsActivity;
import pl.atendesoftware.amitogo.model.ObjectLocation;
import pl.atendesoftware.amitogo.services.LocationUpdateService;

public class MapFragment extends Fragment
        implements OnMapReadyCallback{

    public static final String REST_URL_METER_POINT_LOCATION = "http://10.255.0.85:8080/ceu/rs/meterpointlocation";
    public static final String REST_URL_STATION_LOCATION = "http://10.255.1.52:8080/ceu/rs/stationlocation";

    private FragmentActivity mContext = null;
    private MapView mMapView = null;
    private GoogleMap mMap = null;
    private LocationUpdateReceiver mLocationUpdateReceiver = null;
    private Set<Marker> meterPointMarkerSet = new HashSet<>();
    private Set<Marker> stationMarkerSet = new HashSet<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        mContext = getActivity();
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = (MapView) view.findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        mContext.startService(new Intent(mContext, LocationUpdateService.class));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mLocationUpdateReceiver = new LocationUpdateReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LocationUpdateService.LOCATION_CHANGED_ACTION);
        mContext.registerReceiver(mLocationUpdateReceiver, intentFilter);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        mContext.unregisterReceiver(mLocationUpdateReceiver);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mContext.stopService(new Intent(mContext, LocationUpdateService.class));
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        mMapView.onLowMemory();
        super.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent=null;
        if (meterPointMarkerSet.contains(marker)){
                intent = new Intent(getActivity(), MeterPointListActivity.class);
        } else if(stationMarkerSet.contains(marker)) {
            intent = new Intent(getActivity(), StationDetailsActivity.class);
        }

                startActivity(intent);
            }
        });
    }

    private class LocationUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateMapPosition((Location) intent.getParcelableExtra("location"));
        }
    }

    private void updateMapPosition(Location location) {
        if (mMap != null)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
    }









}
