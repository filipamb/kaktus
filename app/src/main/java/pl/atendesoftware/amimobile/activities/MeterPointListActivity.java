package pl.atendesoftware.amimobile.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import pl.atendesoftware.amimobile.R;

public class MeterPointListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meter_point_list);
        setTitle(getString(R.string.meter_point_list_activity_title));
    }
}
