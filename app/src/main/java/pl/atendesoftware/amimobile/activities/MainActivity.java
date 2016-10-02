package pl.atendesoftware.amimobile.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
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
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import pl.atendesoftware.amimobile.R;
import pl.atendesoftware.amimobile.fragments.MapFragment;
import pl.atendesoftware.amimobile.helpers.SharedPreferencesHelper;
import pl.atendesoftware.amimobile.services.LocationUpdateService;
import pl.atendesoftware.amimobile.services.MeterPointLocationService;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ProgressUpdateReceiver mProgressUpdateReceiver = null;
    private ProgressDialog mProgressDialog = null;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Bierzemy fragment Managera
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Tworzymy fragment i przypisujemy mu bundle z listą meter pointów
        MapFragment mapFragment = new MapFragment();

        // Zamieniamy fragment
        fragmentManager.beginTransaction().replace(R.id.fragment_container, mapFragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        SharedPreferences prefs = getSharedPreferences("amimobile", Context.MODE_PRIVATE);
        TextView drawerUsername = (TextView) findViewById(R.id.drawer_username);
        TextView drawerEmail = (TextView) findViewById(R.id.drawer_email);
        drawerUsername.setText(prefs.getString("name", "Atende Software"));
        drawerEmail.setText(prefs.getString("email", "atendesoftware@amimobile.pl"));

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // sprawdzenie, czy pobralismy juz baze danych
        if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean(MeterPointLocationService.DB_DOWNLOADED_PREFERENCE, false)) {
            Log.i(this.getClass().getName(), "Shared preference FALSE");

            Intent intent = new Intent(mContext, MeterPointLocationService.class);
            startService(intent);

            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setTitle("Download");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.show();
        } else {
            Log.i(this.getClass().getName(), "Shared preference TRUE");
        }
    }

    private class ProgressUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(MeterPointLocationService.UPDATE_ACTION)) {
                Log.i("AMI", "Update Progress");
                mProgressDialog.setProgress(intent.getIntExtra("progress", 0));
            } else if(intent.getAction().equals(MeterPointLocationService.UPDATE_FINISHED_ACTION)) {
                mProgressDialog.dismiss();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mProgressUpdateReceiver = new ProgressUpdateReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MeterPointLocationService.UPDATE_ACTION);
        intentFilter.addAction(MeterPointLocationService.UPDATE_FINISHED_ACTION);
        mContext.registerReceiver(mProgressUpdateReceiver, intentFilter);
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
        getMenuInflater().inflate(R.menu.activity_main_main_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint("Wyszukaj...");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
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
                SharedPreferencesHelper.remove(mContext, SharedPreferencesHelper.app_user_login_key);
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
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
