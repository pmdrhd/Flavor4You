package com.example.resepmakanan.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.resepmakanan.Managers.SessionManager;
import com.example.resepmakanan.R;

public class WelcomeActivity extends AppCompatActivity {
    Boolean isLoggedIn, isRemember;
    Button btnRegister, btnStart;
    LinearLayout llLoginOption;
    String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_page);

        //Prevents SystemBar and NavigationBar overlapping
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        SessionManager session = new SessionManager(this);

        isRemember = session.isRemembered();
        isLoggedIn = session.isLoggedIn();

        username = session.getUsername();

        btnRegister = findViewById(R.id.createAccountButton);
        btnStart = findViewById(R.id.welcomeButton);
        llLoginOption = findViewById(R.id.logInOption);

        btnRegister.setOnClickListener(view -> {
            Intent intent = new Intent(WelcomeActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        llLoginOption.setOnClickListener(view -> {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        if (isRemember) {
            btnRegister.setVisibility(View.GONE);
            llLoginOption.setVisibility(View.GONE);
            btnStart.setVisibility(View.VISIBLE);

            btnStart.setOnClickListener(v -> {
                // go to your real main activity with bottom nav
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // prevent going back to welcome page
            });
        }
    }
}
