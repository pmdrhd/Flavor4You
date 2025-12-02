package com.example.resepmakanan.Activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.resepmakanan.Managers.SessionManager;
import com.example.resepmakanan.R;
import com.example.resepmakanan.BuildConfig;

import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    EditText usernameInput, emailInput, passwordInput;
    TextView txtLogin;
    ImageButton btnBack;
    CardView btnRegister;
    CheckBox cbRemember,cbVisibility;
    String API_IP_ADDRESS = BuildConfig.API_IP_ADDRESS;
    String URL = "http://" + API_IP_ADDRESS + "/recipe_api/users/register.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page);

        //Prevents SystemBar and NavigationBar overlapping
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        usernameInput = findViewById(R.id.usernameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        btnRegister = findViewById(R.id.cvRegister);
        txtLogin = findViewById(R.id.txtLogin);
        btnBack = findViewById(R.id.return_icon);
        cbRemember = findViewById(R.id.checkboxRemember);
        cbVisibility = findViewById(R.id.cbVisibility);

        cbVisibility.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Show password
                passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                // Hide password
                passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            Typeface poppins = ResourcesCompat.getFont(this, R.font.poppins);
            passwordInput.setTypeface(poppins);
            // Optional: keep cursor at the end
            passwordInput.setSelection(passwordInput.getText().length());
        });

        btnBack.setOnClickListener(v -> finish());

        btnRegister.setOnClickListener(v -> attemptRegister());

        txtLogin.setOnClickListener(v -> {
            Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        });
    }

    private void attemptRegister() {

        String username = usernameInput.getText().toString().trim();
        String email    = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Isi semua field", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest req = new StringRequest(Request.Method.POST, URL,
                response -> {
                    Log.d("REGISTER_DEBUG", "Raw Response: " + response);
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            Toast.makeText(this, "Berhasil daftar!", Toast.LENGTH_SHORT).show();

                            int userId = obj.getInt("user_id");

                            SessionManager sm = new SessionManager(this);

                            // data user selalu disimpan
                            sm.saveUser(userId, username, email);

                            // status remember hanya jika dicentang
                            sm.setRememberMe(cbRemember.isChecked());

                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    if (error.networkResponse != null) {
                        Log.e("REGISTER_DEBUG", "code: " + error.networkResponse.statusCode);
                        Log.e("REGISTER_DEBUG", "data: " + new String(error.networkResponse.data));
                    } else {
                        Log.e("REGISTER_DEBUG", "networkResponse null", error);
                    }
                    Toast.makeText(this, "Error: " + error.toString(), Toast.LENGTH_LONG).show();
                }
        ) {
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> map = new java.util.HashMap<>();
                map.put("username", username);
                map.put("email", email);
                map.put("password", password);
                return map;
            }
        };

        Volley.newRequestQueue(this).add(req);
    }
}