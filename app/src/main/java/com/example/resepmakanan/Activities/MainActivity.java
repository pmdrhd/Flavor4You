package com.example.resepmakanan.Activities;

import static android.app.PendingIntent.getActivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.resepmakanan.Fragment.HomeFragment;
import com.example.resepmakanan.Fragment.SearchFragment;
import com.example.resepmakanan.Fragment.SettingFragment;
import com.example.resepmakanan.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new SettingFragment())
                .commit();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_search) {
                selectedFragment = new SearchFragment();
            } else if (itemId == R.id.nav_chat) {
                Intent intent = new Intent(MainActivity.this, AIChatActivity.class);
                startActivity(intent);
                return false;
            } else if (itemId == R.id.nav_setting) {
                selectedFragment = new SettingFragment();
            }

            if (selectedFragment != null) {
                replaceFragment(selectedFragment);
            }
            return true;
        });

        // Set default fragment
        if (savedInstanceState == null) {
            replaceFragment(new HomeFragment());
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}