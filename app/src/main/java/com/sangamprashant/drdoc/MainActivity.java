package com.sangamprashant.drdoc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    HomeFragment homeFragment = new HomeFragment();
    UploadFragment uploadFragment = new UploadFragment();
    MessageFragment messageFragment = new MessageFragment();
    StoreFragment storeFragment = new StoreFragment();
    SettingsFragment settingsFragment = new SettingsFragment();
    CurrentUser currentUser = CurrentUser.getInstance();
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.screenToRender, homeFragment).commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.home_nav) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.screenToRender, homeFragment).commit();
                    return true;
                }
                if (item.getItemId() == R.id.message_nav) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.screenToRender, messageFragment).commit();
                    return true;
                }
                if (item.getItemId() == R.id.upload_nav) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.screenToRender, uploadFragment).commit();
                    return true;
                }
                if (item.getItemId() == R.id.store_nav) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.screenToRender, storeFragment).commit();
                    return true;
                }
                if (item.getItemId() == R.id.setting_nav) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.screenToRender, settingsFragment).commit();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_nav_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.LogOut) {
            // Clear the shared preferences and log out the user
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            currentUser.clearUser();

            Toast.makeText(this, "Logged out successfully.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, LogInActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
