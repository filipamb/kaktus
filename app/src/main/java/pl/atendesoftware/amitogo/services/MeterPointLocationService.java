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
import java.net.HttpURLConnection;
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

    public final static String URL = "http://192.168.0.14:8080/messenger/webapi/getmploc";
    public final static String DB_DOWNLOADED_PREFERENCE = "MeterPointLocationService Db Downloaded";

    // zapisywacz bazy :D
    public DatabaseWriterAdapter databaseWriterAdapter = new DatabaseWriterAdapter(this);
    // shared preferences
    public SharedPreferences preferences;
    // norify manager
    public NotificationManager mNotifyManager;
    public NotificationCompat.Builder mBuilder;
    // id dla naszego norification
    public int databaseUpdateNotificationId;


    public MeterPointLocationService() {
        super(MeterPointLocation.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // inicjalizacja shared preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        long start = System.currentTimeMillis();


        // inicjalizacja Notify Managera
        mNotifyManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("Updating local database")
                .setSmallIcon(R.drawable.energa_logo);
        databaseUpdateNotificationId = 1;

        // otwarcie polaczenia z baza danych
        databaseWriterAdapter.open();


        try {

            // notify Manager - start
            mBuilder.setContentText("Downloading data...");
            mBuilder.setProgress(0, 0, true);
            mNotifyManager.notify(databaseUpdateNotificationId, mBuilder.build());

            // pobranie danych
            java.net.URL url = new URL(URL);

            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            InputStream in = new BufferedInputStream(connection.getInputStream());

            // konwersja InputStream na String i dodawanie do bazy danych
            addToDatabase(streamToString(in));

            // notify Manager - koniec
            mBuilder.setContentText("Database update complete").setProgress(0, 0, false);
            mNotifyManager.notify(databaseUpdateNotificationId, mBuilder.build());

            // dodanie do shared preferences info o sciagnieciu bazy danych jesli wszystko sie powiodlo
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(DB_DOWNLOADED_PREFERENCE, true);
            editor.commit();

            Log.d(this.getClass().getName(), "onHandleIntent: Time of execution in seconds: " + (System.currentTimeMillis() - start) / 1000);


        } catch (Exception e) {
            e.printStackTrace();

            // notify Manager - błąd przy pobieraniu danych
            mBuilder.setContentText("Downloading file failed").setProgress(0, 0, false);
            mNotifyManager.notify(databaseUpdateNotificationId, mBuilder.build());
        }

        // zamkniecie polaczenia z baza danych
        databaseWriterAdapter.close();

    }

    // zamiana wyniku z url na stringa
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

    // dodawanie stringa wynikowego (z obiektami json) do bazy
    public void addToDatabase(String input) throws JSONException {
        int count = 0;
        List<MeterPointLocationToDatabase> meterPointLocationList = new ArrayList<>();

        Log.i(this.getClass().getSimpleName(), "Getting json Array");
        Log.i(this.getClass().getSimpleName(), "Parsing array ...........");
        JSONArray jsonArray = new JSONArray(input);
        JSONObject jsonObject;
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
            Log.i("parseMeterPointLocation", "bulk iserted for " + count + " time and percentage = " + (100 * i / jsonArray.length()) + " %");

            mBuilder.setContentText("Updating database...");
            mBuilder.setProgress(100, 100 * i / jsonArray.length(), false);
            mNotifyManager.notify(databaseUpdateNotificationId, mBuilder.build());
        }
        Log.i(this.getClass().getSimpleName(), "Array length " + jsonArray.length());


    }
}
