package pl.atendesoftware.amitogo.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import pl.atendesoftware.amitogo.R;
import pl.atendesoftware.amitogo.fragments.MapFragment;
import pl.atendesoftware.amitogo.services.MeterPointLocationService;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;


        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Bierzemy fragment Managera
        FragmentManager fragmentManager =  getSupportFragmentManager();

        // Tworzymy fragment i przypisujemy mu bundle z listą meter pointów
        MapFragment mapFragment = new MapFragment();

        // Zamieniamy fragment
        fragmentManager.beginTransaction().replace(R.id.fragment_container, mapFragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // sprawdzenie, czy pobralismy juz baze danych
        if(!PreferenceManager.getDefaultSharedPreferences(this).getBoolean(MeterPointLocationService.DB_DOWNLOADED_PREFERENCE,false)) {
            Log.i(this.getClass().getName(), "Shared preference FALSE");

            AlertDialog.Builder downloadDatabaseDialogBuilder = new AlertDialog.Builder(mContext);

            downloadDatabaseDialogBuilder.setMessage(getString(R.string.download_db_dialog_message))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.download_db_dialog_positive_button_name), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Intent intent = new Intent(mContext,MeterPointLocationService.class);
                            startService(intent);
                        }
                    })
                    .setNegativeButton(getString(R.string.download_db_dialog_negative_button_name), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });

            AlertDialog downloadDatabaseDialog = downloadDatabaseDialogBuilder.create();
            downloadDatabaseDialog.setTitle(getString(R.string.download_db_dialog_title));
            downloadDatabaseDialog.show();
        } else {
            Log.i(this.getClass().getName(), "Shared preference TRUE");
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.map:
                fragment = new MapFragment();
                break;
            case R.id.mode_3D:
                break;
            case R.id.logout:
                getSharedPreferences("amitogo", Context.MODE_PRIVATE).edit().putBoolean("loggedIn",false).apply();
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.settings:
                break;
        }
        if (fragment != null)
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
