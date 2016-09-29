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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import java.util.Set;

import pl.atendesoftware.amitogo.R;
import pl.atendesoftware.amitogo.database.DatabaseReaderAdapter;
import pl.atendesoftware.amitogo.database.DatabaseWriterAdapter;
import pl.atendesoftware.amitogo.model.MeterPointLocation;
import pl.atendesoftware.amitogo.services.LocationUpdateService;

public class MapFragment extends Fragment
        implements OnMapReadyCallback {

    private ClusterManager<MyItem> mClusterManager;

    public static final String REST_URL_METER_POINT_LOCATION = "http://10.255.1.52:8080/ceu/rs/meterpointlocation";
    public static final String REST_URL_STATION_LOCATION = "http://10.255.1.52:8080/ceu/rs/stationlocation";

    private LatLngBounds POLAND_BOUNDS = new LatLngBounds(
            new LatLng(49.0300, 14.1400), new LatLng(55.9500, 24.1600));
    private LatLng ATENDE = new LatLng(52.2350999, 21.09921589999999);
    private FragmentActivity mContext = null;
    private MapView mMapView = null;
    private GoogleMap mMap = null;
    private LocationUpdateReceiver mLocationUpdateReceiver = null;

    private DatabaseReaderAdapter databaseReaderAdapter;
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

        databaseReaderAdapter = new DatabaseReaderAdapter(mContext);
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

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {


                                           @Override
                                           public void onCameraChange(CameraPosition cameraPosition) {
                                               LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
                                               // Check whether the camera changes report the same boundaries (?!), yes, it happens
                                               if (currentCameraBounds.northeast.latitude == bounds.northeast.latitude
                                                       && currentCameraBounds.northeast.longitude == bounds.northeast.longitude
                                                       && currentCameraBounds.southwest.latitude == bounds.southwest.latitude
                                                       && currentCameraBounds.southwest.longitude == bounds.southwest.longitude) {
                                                   return;
                                               }

        databaseReaderAdapter.close();

        setUpClusterer();
                                               final long snap = System.currentTimeMillis();
                                               if (lastCallMs + CAMERA_MOVE_REACT_THRESHOLD_MS > snap) {
                                                   lastCallMs = snap;
                                                   return;
                                               }

                                               Log.i(this.getClass().getName(), "On camera Change!!!!!!!!!!!!!!");


                                               /*databaseReaderAdapter.open();
                                               Set<MeterPointLocation> meterPointsByBounds = databaseReaderAdapter.getMeterPointsByBounds(bounds);
                                               databaseReaderAdapter.close();
                                               for(MeterPointLocation mpl:meterPointsByBounds){
                                                   Log.i(this.getClass().getName(),"Meter point with id " + mpl.getMeterId() + " longitude " +mpl.getLongitude()+ " and latitude " + mpl.getLatitude());
                                               }*/
                                               lastCallMs = snap;
                                               currentCameraBounds = bounds;

                                           }
                                       }

            );
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
        Toast.makeText(mContext, "" + mMap.getProjection().getVisibleRegion().latLngBounds, Toast.LENGTH_LONG).show();
    }

    private class MyItem implements ClusterItem {
        private final LatLng mPosition;

        public MyItem(double lat, double lng) {
            mPosition = new LatLng(lat, lng);
        }

        @Override
        public LatLng getPosition() {
            return mPosition;
        }
    }

    private void setUpClusterer() {
        // Position the map.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.503186, -0.126446), 10));

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<MyItem>(mContext, mMap);

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnCameraChangeListener(mClusterManager);
        // Add cluster items (markers) to the cluster manager.
        addItems();
    }

    private void addItems() {

        // Set some lat/lng coordinates to start with.
        double lat = 51.5145160;
        double lng = -0.1270060;

        // Add ten cluster items in close proximity, for purposes of this example.
        for (int i = 0; i < 10; i++) {
            double offset = i / 60d;
            lat = lat + offset;
            lng = lng + offset;
            MyItem offsetItem = new MyItem(lat, lng);
            mClusterManager.addItem(offsetItem);
        }
    }


}
