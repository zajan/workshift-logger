package com.ajna.workshiftlogger.activities;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ajna.workshiftlogger.R;
import com.ajna.workshiftlogger.database.FactorsContract;
import com.ajna.workshiftlogger.fragments.ActiveShiftFragment;
import com.ajna.workshiftlogger.fragments.ClientsListFragment;
import com.ajna.workshiftlogger.fragments.NewClientFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        ClientsListFragment.OnFragmentInteractionListener,
        NewClientFragment.OnFragmentInteractionListener,
        ActiveShiftFragment.OnFragmentInteractionListener{
    private static final String TAG = "MainActivity";

    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;
    private ActionBar actionBar;
    private boolean mToolBarNavigationListenerIsRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);

        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState != null) {
            resolveUpButtonWithFragmentStack();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_main, ActiveShiftFragment.newInstance())
                    .commit();
            navigationView.setCheckedItem(R.id.nav_shifts);
        }

        ContentResolver contentResolver = getContentResolver();

        Cursor cursor = contentResolver.query(FactorsContract.CONTENT_URI, null, null, null, null);
        if(cursor != null && cursor.getCount() >0){
            cursor.moveToFirst();
            do {
                String id = String.valueOf(cursor.getLong(cursor.getColumnIndex(FactorsContract.Columns._ID)));
                String hours = String.valueOf(cursor.getLong(cursor.getColumnIndex(FactorsContract.Columns.START_HOUR)));
                String value = String.valueOf(cursor.getLong(cursor.getColumnIndex(FactorsContract.Columns.VALUE)));
                String clientId = String.valueOf(cursor.getLong(cursor.getColumnIndex(FactorsContract.Columns.CLIENT_ID)));
                Log.d(TAG, "onCreate: ---------------");
                Log.d(TAG, "onCreate: client id: " + clientId  + " id: " + id + " h: " + hours + " v: " +value);
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            if (cursor == null){
                Log.d(TAG, "onCreate: cursor == null");
            } else {
                Log.d(TAG, "onCreate: cursor.getCount() = " + cursor.getCount());
            }
        }


    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: starts");
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int backStackAmount = getSupportFragmentManager().getBackStackEntryCount();
            Log.d(TAG, "onBackPressed: backstack amount = " + backStackAmount);

            if(backStackAmount >= 1) {
                getSupportFragmentManager().popBackStack();
                if (backStackAmount == 1) {
                    showUpButton(false);
                }
            }
             else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            // TODO
            case R.id.nav_shifts:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_main, ActiveShiftFragment.newInstance())
                        .commit();
                break;
            case R.id.nav_projects:
                break;
            case R.id.nav_clients:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_main, ClientsListFragment.newInstance())
                        .commit();
                break;
            case R.id.nav_invoices:
                break;
            case R.id.nav_settings:
                break;
            case R.id.nav_about:
                break;
            default:
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void resolveUpButtonWithFragmentStack() {
        showUpButton(getSupportFragmentManager().getBackStackEntryCount() > 0);
    }

    private void showUpButton(boolean show) {
        Log.d(TAG, "showUpButton: starts");
        Log.d(TAG, "showUpButton: show = " + show);
        if (show) {
            toggle.setDrawerIndicatorEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            if (!mToolBarNavigationListenerIsRegistered) {
                Log.d(TAG, "showUpButton: !mToolBarNavigationListenerIsRegistered");
                toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onBackPressed();
                    }
                });
                mToolBarNavigationListenerIsRegistered = true;
            }
        } else {
            Log.d(TAG, "showUpButton: mToolBarNavigationListenerIsRegistered");

            actionBar.setDisplayHomeAsUpEnabled(false);
            toggle.setDrawerIndicatorEnabled(true);
            toggle.setToolbarNavigationClickListener(null);
            mToolBarNavigationListenerIsRegistered = false;

        }
    }

    @Override
    public void onClientClicked(String name) {
        Log.d(TAG, "onNewClientClicked: starts");
        toggle.setDrawerIndicatorEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (!mToolBarNavigationListenerIsRegistered) {
            toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            mToolBarNavigationListenerIsRegistered = true;
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_main, NewClientFragment.newInstance(name))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onNewClientClicked() {
        Log.d(TAG, "onNewClientClicked: starts");
        onClientClicked(null);
    }

    @Override
    public void onSaveClicked() {
        int backStackAmount = getSupportFragmentManager().getBackStackEntryCount();

        if(backStackAmount >= 1) {
            getSupportFragmentManager().popBackStack();
            if (backStackAmount == 1) {
                showUpButton(false);
            }
        }
        else {
            super.onBackPressed();
        }
    }
}