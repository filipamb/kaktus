package pl.atendesoftware.amitogo.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import pl.atendesoftware.amitogo.R;

public class StationDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_details);
        setTitle(getString(R.string.station_details_activity_title));
    }
}
