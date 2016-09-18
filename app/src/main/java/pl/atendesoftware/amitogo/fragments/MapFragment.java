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

    public static final String REST_URL_METER_POINT_LOCATION = "http://192.168.0.14:8080/messenger/webapi/getmploc";
    public static final String REST_URL_STATION_LOCATION = "http://192.168.0.14:8080/messenger/webapi/getstatloc";

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

        // testowe odpalanie async taska
        MeterPointLocationHandler meterPointLocationHandler = new MeterPointLocationHandler();
        StationLocationHandler stationLocationHandler = new StationLocationHandler();
        meterPointLocationHandler.execute(REST_URL_METER_POINT_LOCATION);
        stationLocationHandler.execute(REST_URL_STATION_LOCATION);
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








    // ASYNC TASK DO POBIERANIA DANYCH O PP

    public class MeterPointLocationHandler extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                URL url = new URL(strings[0]);
                URLConnection connection = url.openConnection();

                // pobranie danych
                InputStream in = new BufferedInputStream(connection.getInputStream());

                // konwersja InputStream na String
                // wynik przekazany do onPostExecute

                publishProgress(streamToString(in));
            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }

        @Override
        protected void onProgressUpdate(String... values) {
            try {
                JSONArray jsonArray = new JSONArray(values[0]);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    ObjectLocation mpl = new ObjectLocation(
                            jsonObject.getLong("objectId"),
                            jsonObject.getDouble("latitude"),
                            jsonObject.getDouble("longitude"));

                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(mpl.getLatitude(), mpl.getLongitude()))
                            .title(getString(R.string.meter_point_market_title))
                            .snippet(String.valueOf(mpl.getMeterId()))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    meterPointMarkerSet.add(marker);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public String streamToString(InputStream is) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;

            try {

                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }

                reader.close();

            } catch (IOException e) {
                // obsłuż wyjątek
                Log.d(MainActivity.class.getSimpleName(), e.toString());
            }

            System.out.println(stringBuilder.toString());

            return stringBuilder.toString();
        }


    }






    // ASYNC TASK DO POBIERANIA DANYCH O STACJI

    public class StationLocationHandler extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                URL url = new URL(strings[0]);
                URLConnection connection = url.openConnection();

                // pobranie danych
                InputStream in = new BufferedInputStream(connection.getInputStream());

                // konwersja InputStream na String
                // wynik przekazany do onPostExecute

                publishProgress(streamToString(in));
            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }

        @Override
        protected void onProgressUpdate(String... values) {
            try {
                JSONArray jsonArray = new JSONArray(values[0]);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    ObjectLocation mpl = new ObjectLocation(
                            jsonObject.getLong("objectId"),
                            jsonObject.getDouble("latitude"),
                            jsonObject.getDouble("longitude"));

                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(mpl.getLatitude(), mpl.getLongitude()))
                            .title(getString(R.string.station_marker_title))
                            .snippet(String.valueOf(mpl.getMeterId()))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                    stationMarkerSet.add(marker);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public String streamToString(InputStream is) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;

            try {

                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }

                reader.close();

            } catch (IOException e) {
                // obsłuż wyjątek
                Log.d(MainActivity.class.getSimpleName(), e.toString());
            }

            System.out.println(stringBuilder.toString());

            return stringBuilder.toString();
        }


    }

}
