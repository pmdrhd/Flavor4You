package com.example.resepmakanan.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.resepmakanan.R;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_page);

        Button startButton = findViewById(R.id.welcomeButton);
        startButton.setOnClickListener(v -> {
            // go to your real main activity with bottom nav
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // prevent going back to welcome page
        });
    }
}
