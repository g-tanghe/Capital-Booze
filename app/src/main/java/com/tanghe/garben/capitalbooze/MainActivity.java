package com.tanghe.garben.capitalbooze;

import android.content.res.Configuration;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class MainActivity extends AppCompatActivity implements
        AboutFragment.OnAboutFragmentInteractionListener,
        LogInFragment.OnLogInFragmentInteractionListener,
        DrinkFragment.OnDrinkFragmentInteractionListener,
        OrderFragment.OnOrderFragmentInteractionListener,
        CountersFragment.OnCountersFragmentInteractionListener,
        PricesFragment.OnPricesFragmentInteractionListener {

    public boolean isAdmin = false;

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Firebase.setAndroidContext(this);
        // Get a reference to our posts
        Firebase ref = new Firebase("");
        // Attach an listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d("FireBase", "Data changed");
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d("Firebas", "The read failed: " + firebaseError.getMessage());
            }
        });

        LogInFragment.setArgument(MainActivity.this);
        Drink.setArgument(MainActivity.this);
        CountersFragment.setArgument(MainActivity.this);
        PricesFragment.setArgument(MainActivity.this);
        OrderFragment.setArgument(MainActivity.this);

        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (isAdmin) {
            findViewById(R.id.nav_counters_fragment).setVisibility(View.VISIBLE);
            findViewById(R.id.nav_drink_fragment).setVisibility(View.VISIBLE);
        }
        // Find our drawer view
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        // Setup drawer view
        setupDrawerContent(nvDrawer);
        drawerToggle = new ActionBarDrawerToggle(this, mDrawer, R.string.drawer_open,  R.string.drawer_close);
        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);

        // Default fragment
        fragmentManager.beginTransaction().replace(R.id.container, new AboutFragment()).commit();
        setTitle(getString(R.string.nav_about));
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    private void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;
        switch(menuItem.getItemId()) {
            case R.id.nav_drink_fragment:
                fragmentClass = DrinkFragment.class;
                break;
            case R.id.nav_order_fragment:
                fragmentClass = OrderFragment.class;
                break;
            case R.id.nav_counters_fragment:
                fragmentClass = CountersFragment.class;
                break;
            case R.id.nav_prices_fragment:
                fragmentClass = PricesFragment.class;
                break;
            case R.id.nav_about_fragment:
                fragmentClass = AboutFragment.class;
                break;
            case R.id.nav_log_in_fragment:
                fragmentClass = LogInFragment.class;
                break;
            case R.id.nav_exit:
                finish();
                System.exit(0);
            default:
                fragmentClass = AboutFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    // `onPostCreate` called when activity start-up is complete after `onStart()`
    // NOTE! Make sure to override the method with only a single `Bundle` argument
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAboutNextPressed() {
        fragmentManager.beginTransaction().replace(R.id.container, new LogInFragment()).commit();
        setTitle(getString(R.string.nav_log_in));
    }

    @Override
    public void onLogInBackPressed() {
        fragmentManager.beginTransaction().replace(R.id.container, new AboutFragment()).commit();
        setTitle(getString(R.string.nav_about));
    }

    @Override
    public void onLogInNextPressed() {
        if (isAdmin) {
            fragmentManager.beginTransaction().replace(R.id.container, new DrinkFragment()).commit();
            setTitle(getString(R.string.nav_drink));
        } else {
            fragmentManager.beginTransaction().replace(R.id.container, new PricesFragment()).commit();
            setTitle(getString(R.string.nav_prices));
        }
    }

    @Override
    public void onDrinkBackPressed() {
        fragmentManager.beginTransaction().replace(R.id.container, new LogInFragment()).commit();
        setTitle(getString(R.string.nav_log_in));
    }

    @Override
    public void onDrinkNextPressed() {
        fragmentManager.beginTransaction().replace(R.id.container, new OrderFragment()).commit();
        setTitle(getString(R.string.nav_order));
    }

    @Override
    public void onOrderBackPressed() {
        fragmentManager.beginTransaction().replace(R.id.container, new DrinkFragment()).commit();
        setTitle(getString(R.string.nav_drink));
    }

    @Override
    public void onOrderNextPressed() {
        fragmentManager.beginTransaction().replace(R.id.container, new CountersFragment()).commit();
        setTitle(getString(R.string.nav_counters));
    }

    @Override
    public void onCountersBackPressed() {
        fragmentManager.beginTransaction().replace(R.id.container, new OrderFragment()).commit();
        setTitle(getString(R.string.nav_order));
    }

    @Override
    public void onCountersNextPressed() {
        fragmentManager.beginTransaction().replace(R.id.container, new PricesFragment()).commit();
        setTitle(getString(R.string.nav_prices));
    }

    @Override
    public void onPricesBackPressed() {
        if (isAdmin) {
            fragmentManager.beginTransaction().replace(R.id.container, new CountersFragment()).commit();
            setTitle(getString(R.string.nav_counters));
        }
        else {
            fragmentManager.beginTransaction().replace(R.id.container, new LogInFragment()).commit();
            setTitle(getString(R.string.nav_log_in));
        }
    }

    @Override
    public void onOrder() {
        Log.d("debug", "Order set");
    }
}