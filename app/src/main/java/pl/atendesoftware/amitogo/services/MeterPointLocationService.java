package pl.atendesoftware.amitogo.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

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
import java.util.ArrayList;
import java.util.List;

import pl.atendesoftware.amitogo.R;
import pl.atendesoftware.amitogo.activities.MainActivity;
import pl.atendesoftware.amitogo.database.DatabaseWriterAdapter;
import pl.atendesoftware.amitogo.model.MeterPointLocation;
import pl.atendesoftware.amitogo.model.MeterPointLocationToDatabase;


public class MeterPointLocationService extends IntentService {

    public final static String URL = "http://10.255.1.52:8080/ceu/rs/meterpointlocation";
    public final static String DB_DOWNLOADED_PREFERENCE = "MeterPointLocationService Db Downloaded";


    public DatabaseWriterAdapter databaseWriterAdapter = new DatabaseWriterAdapter(this);
    public SharedPreferences preferences;
    public NotificationManager mNotifyManager;
    public NotificationCompat.Builder mBuilder;

    public int databaseUpdateNotificationId;


    public MeterPointLocationService() {
        super(MeterPointLocation.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        long start = System.currentTimeMillis();
        databaseWriterAdapter.open();


        // inicjalizacja Notify Managera
        mNotifyManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("Updating local database")
                .setSmallIcon(R.drawable.energa_logo);
        databaseUpdateNotificationId = 1;

        try {

            // notify Manager - start
            mBuilder.setContentText("Downloading data...");
            mBuilder.setProgress(0, 0, true);
            mNotifyManager.notify(databaseUpdateNotificationId, mBuilder.build());



            // pobranie danych
            java.net.URL url = new URL(URL);
            URLConnection connection = url.openConnection();
            InputStream in = new BufferedInputStream(connection.getInputStream());

            // konwersja InputStream na String i dodawanie do bazy danych
            addToDatabase(streamToString(in));

            // notify Manager - koniec
            mBuilder.setContentText("Database update complete").setProgress(0,0,false);
            mNotifyManager.notify(databaseUpdateNotificationId, mBuilder.build());

        } catch (Exception e) {
            e.printStackTrace();

            // notify Manager - błąd
            mBuilder.setContentText("Downloading file failed").setProgress(0,0,false);
            mNotifyManager.notify(databaseUpdateNotificationId, mBuilder.build());
        }

        databaseWriterAdapter.close();

        Log.d(this.getClass().getName(), "onHandleIntent: Time of execution in seconds: " + (System.currentTimeMillis() - start) / 1000);

        // dodanie do shared preferences info o sciagnieciu bazy danych
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(DB_DOWNLOADED_PREFERENCE, true);
        editor.commit();
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

        return stringBuilder.toString();
    }

    public void addToDatabase(String input) throws JSONException {
        int count = 0;
        List<MeterPointLocationToDatabase> meterPointLocationList = new ArrayList<>();

        Log.i(this.getClass().getSimpleName(), "Getting json Array");
        JSONArray jsonArray = new JSONArray(input);
        JSONObject jsonObject;
        Log.i(this.getClass().getSimpleName(), "Parsing array ...........");
        Log.i(this.getClass().getSimpleName(), "Array length " + jsonArray.length());


        for (int i = 0; i < jsonArray.length(); i += 100) {



            int loop_range = jsonArray.length() >= i + 100 ? 100 : jsonArray.length() - i;

            for (int j = 0; j < loop_range; j++) {
                jsonObject = jsonArray.getJSONObject(i + j);
                meterPointLocationList.add(new MeterPointLocationToDatabase(
                        jsonObject.getLong("meterPointId"),
                        jsonObject.getLong("x"),
                        jsonObject.getLong("y")));



            }
            count++;
            databaseWriterAdapter.bulkInsertMeterPointLocations(meterPointLocationList);
            meterPointLocationList.clear();
            Log.i("parseMeterPointLocation", "bulk iserted for " + count + " time and percentage = " + (100*i/jsonArray.length()) + " %");

            mBuilder.setContentText("Updating database...");
            mBuilder.setProgress(100,100*i/jsonArray.length(),false);
            mNotifyManager.notify(databaseUpdateNotificationId, mBuilder.build());
        }
        Log.i(this.getClass().getSimpleName(), "Array length " + jsonArray.length());


    }
}
