package com.example.resepmakanan.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.resepmakanan.BuildConfig;
import com.example.resepmakanan.Managers.SessionManager;
import com.example.resepmakanan.R;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    EditText usernameInput, passwordInput;
    ImageButton btnBack;
    TextView gotoRegister;
    CardView cvLogin;
    CheckBox cbRemember;
    String API_IP_ADDRESS = BuildConfig.API_IP_ADDRESS;
    String URL = "http://" + API_IP_ADDRESS + "/recipe_api/users/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        //Prevents SystemBar and NavigationBar overlapping
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        // Bind views
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        btnBack = findViewById(R.id.return_icon);
        gotoRegister = findViewById(R.id.gotoRegister);
        cvLogin = findViewById(R.id.cvLogin);
        cbRemember = findViewById(R.id.checkboxRemember);

        // tombol kembali
        btnBack.setOnClickListener(v -> finish());

        // tombol menuju register
        gotoRegister.setOnClickListener(v -> {
                startActivity(new Intent(this, RegisterActivity.class));
                finish();
        });

        // tombol login (cardView)
        cvLogin.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String identifier = usernameInput.getText().toString().trim();
        String password   = passwordInput.getText().toString().trim();

        if (identifier.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Isi semua field", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest req = new StringRequest(Request.Method.POST, URL,
                response -> {
                    Log.d("REGISTER_DEBUG", "Raw Response: " + response);
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {

                            JSONObject user = obj.getJSONObject("data");

                            int id = user.getInt("id");
                            String username = user.getString("username");
                            String email = user.getString("email");

                            if (cbRemember.isChecked()) {
                                new SessionManager(this).saveUser(id, username, email);
                            }

                            startActivity(new Intent(this, MainActivity.class));
                            finish();

                        } else {
                            Toast.makeText(this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show()
        ) {
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> map = new java.util.HashMap<>();
                map.put("identifier", identifier);
                map.put("password", password);
                return map;
            }
        };

        Volley.newRequestQueue(this).add(req);
    }
}
