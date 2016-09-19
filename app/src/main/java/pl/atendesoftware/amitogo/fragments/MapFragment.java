package pl.atendesoftware.amitogo.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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

import java.util.HashSet;
import java.util.Set;

import pl.atendesoftware.amitogo.R;
import pl.atendesoftware.amitogo.activities.MeterPointListActivity;
import pl.atendesoftware.amitogo.activities.StationDetailsActivity;
import pl.atendesoftware.amitogo.database.DatabaseAdapter;
import pl.atendesoftware.amitogo.model.MeterPointLocation;
import pl.atendesoftware.amitogo.services.LocationUpdateService;

public class MapFragment extends Fragment
        implements OnMapReadyCallback {

    public static final String REST_URL_METER_POINT_LOCATION = "http://10.255.1.52:8080/ceu/rs/meterpointlocation";
    public static final String REST_URL_STATION_LOCATION = "http://10.255.1.52:8080/ceu/rs/stationlocation";

    private FragmentActivity mContext = null;
    private MapView mMapView = null;
    private GoogleMap mMap = null;
    private LocationUpdateReceiver mLocationUpdateReceiver = null;

    private DatabaseAdapter databaseAdapter;
    Set<MeterPointLocation> allMeterPointLocations;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        mContext = getActivity();
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = (MapView) view.findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        mContext.startService(new Intent(mContext, LocationUpdateService.class));

    /*    databaseAdapter = new DatabaseAdapter(mContext);
        databaseAdapter.open();*/

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

        /*allMeterPointLocations = databaseAdapter.getAllMeterPointLocations();
        for(MeterPointLocation mpl: allMeterPointLocations){
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(mpl.getLatitude(), mpl.getLongitude()))
                    .title(getString(R.string.meter_point_market_title))
                    .snippet(String.valueOf(mpl.getMeterId()))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        }*/

//        databaseAdapter.close();
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
