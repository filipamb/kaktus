package pl.atendesoftware.amitogo.webservice;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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

import pl.atendesoftware.amitogo.R;
import pl.atendesoftware.amitogo.activities.MainActivity;
import pl.atendesoftware.amitogo.model.ObjectLocation;

public class MeterPointLocationHandler extends AsyncTask<String, String, String> {

    private ProgressDialog dialog;
    private Context mContext;
    private int count = 0;
    public MeterPointLocationHandler (Context context){
        mContext = context;
    }




    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(mContext);
        dialog.setMessage("Downloading object locations");
        dialog.show();
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
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
        Log.i(this.getClass().getSimpleName(),"NUMBER OF OBJECTS " + count);


    }

    @Override
    protected void onProgressUpdate(String... values) {
        try {
            JSONArray jsonArray = new JSONArray(values[0]);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ObjectLocation meterPointLocation = new ObjectLocation(
                        jsonObject.getLong("meterPointId"),
                        jsonObject.getDouble("x"),
                        jsonObject.getDouble("y"));

                if (meterPointLocation.getLatitude() != 0 && meterPointLocation.getLongitude() != 0 &&
                        meterPointLocation.getLatitude() != null & meterPointLocation.getLatitude() != null) {
                    count++;
                }

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
