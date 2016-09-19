package pl.atendesoftware.amitogo.webservice;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
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
import java.util.Set;

import pl.atendesoftware.amitogo.activities.MainActivity;
import pl.atendesoftware.amitogo.database.DatabaseAdapter;
import pl.atendesoftware.amitogo.model.MeterPointLocation;

// ASYNC TASK DO POBIERANIA DANYCH O PP

public class MeterPointLocationHandler extends AsyncTask<String, String, String> {

    private int count = 0;

    private ProgressDialog dialog;
    private Context context;
    private DatabaseAdapter databaseAdapter;

    public MeterPointLocationHandler(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(context);
        dialog.setTitle("Downloading...");
        dialog.show();

        databaseAdapter = new DatabaseAdapter(context);
        databaseAdapter.open();

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
        databaseAdapter.close();
        dialog.dismiss();

    }

    @Override
    protected void onProgressUpdate(String... values) {
        try {
            JSONArray jsonArray = new JSONArray(values[0]);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                databaseAdapter.createMeterPointLocation(
                        jsonObject.getLong("meterPointId"),
                        jsonObject.getDouble("x"),
                        jsonObject.getDouble("y"));
            }
        } catch (JSONException e) {
            Log.i(this.getClass().getSimpleName(),"Json exception " + count++);
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
