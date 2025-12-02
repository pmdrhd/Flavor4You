package com.example.resepmakanan.Activities;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.resepmakanan.APIService.ApiConfig;
import com.example.resepmakanan.Fragment.CommentFragment;
import com.example.resepmakanan.Fragment.RecipeFragment;
import com.example.resepmakanan.Managers.SessionManager;
import com.example.resepmakanan.Models.Recipe;
import com.example.resepmakanan.R;
import com.example.resepmakanan.Requests.FavoriteRequests;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

public class RecipeActivity extends AppCompatActivity {

    private int recipeId, userId;
    private ImageView imgRecipe;
    private CheckBox cbFavorite;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_page);

        queue = Volley.newRequestQueue(this);

        // Prevens SystemBar overlapping
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        // Ambil data dari intent
        recipeId = getIntent().getIntExtra("recipe_id", -1);

        // Session user
        SessionManager session = new SessionManager(this);
        userId = session.getId();

        // UI references
        imgRecipe = findViewById(R.id.recipeFoodImage);
        cbFavorite = findViewById(R.id.cbFavorite);

        // Back button
        ImageButton returnButton = findViewById(R.id.returnToPageButton);
        returnButton.setOnClickListener(v -> finish());

        // Navigation buttons
        TextView navRecipe = findViewById(R.id.nav_recipe);
        TextView navReview = findViewById(R.id.nav_review);

        navRecipe.setSelected(true);
        loadFragment(RecipeFragment.newInstance(recipeId));

        navRecipe.setOnClickListener(v -> {
            navRecipe.setSelected(true);
            navReview.setSelected(false);
            loadFragment(RecipeFragment.newInstance(recipeId));
        });

        navReview.setOnClickListener(v -> {
            navRecipe.setSelected(false);
            navReview.setSelected(true);
            loadFragment(CommentFragment.newInstance(recipeId));
        });

        // Fetch recipe details
        fetchRecipeById(recipeId);

        // Fetch favorite status dari server
        fetchFavoriteStatus();

        // Set listener checkbox
        cbFavorite.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Tambah favorite
                FavoriteRequests.addFavorite(this, queue, userId, recipeId,
                        () -> Toast.makeText(this, "Ditambahkan ke favorit", Toast.LENGTH_SHORT).show(),
                        msg -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                );
            } else {
                // Hapus favorite
                FavoriteRequests.removeFavorite(this, queue, userId, recipeId,
                        () -> Toast.makeText(this, "Dihapus dari favorit", Toast.LENGTH_SHORT).show(),
                        msg -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.recipe_fragment_container, fragment)
                .commit();
    }

    private void fetchRecipeById(int id) {
        String url = "http://10.0.2.2/recipe_api/recipes/get_recipe_by_id.php?id=" + id;

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (!response.getBoolean("success")) return;

                        JSONObject recipe = response.getJSONObject("data");

                        String imgPath = recipe.getString("gambar");
                        String imageUrl = imgPath != null && !imgPath.isEmpty()
                                ? ApiConfig.IMAGE_BASE_URL + imgPath
                                : null;

                        if (imageUrl != null) {
                            Glide.with(this)
                                    .load(imageUrl)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .placeholder(R.drawable.ic_launcher_background) // bisa ganti sesuai placeholder
                                    .error(R.drawable.ic_launcher_background)
                                    .into(imgRecipe);
                        } else {
                            imgRecipe.setImageResource(R.drawable.martabak);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()

        );
        queue.add(req);
    }

    private void fetchFavoriteStatus() {
        String url = "http://10.0.2.2/recipe_api/recipes/is_favorite.php?user_id=" + userId + "&recipe_id=" + recipeId;

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            boolean isFav = response.getBoolean("favorite");

                            // Pause listener sementara
                            cbFavorite.setOnCheckedChangeListener(null);
                            cbFavorite.setChecked(isFav);
                            // Pasang lagi listener
                            cbFavorite.setOnCheckedChangeListener((buttonView, checked) -> {
                                if (checked) {
                                    FavoriteRequests.addFavorite(this, queue, userId, recipeId,
                                            () -> Toast.makeText(this, "Ditambahkan ke favorit", Toast.LENGTH_SHORT).show(),
                                            msg -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                                    );
                                } else {
                                    FavoriteRequests.removeFavorite(this, queue, userId, recipeId,
                                            () -> Toast.makeText(this, "Dihapus dari favorit", Toast.LENGTH_SHORT).show(),
                                            msg -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                                    );
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // fallback: biarkan checkbox false
                }
        );

        queue.add(req);
    }
}
