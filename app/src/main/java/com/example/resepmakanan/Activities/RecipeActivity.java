package com.example.resepmakanan.Activities;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.resepmakanan.BuildConfig;
import com.example.resepmakanan.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecipeActivity extends AppCompatActivity {

    private TextView foodName, recipeText, recipeCookTime, recipeServings;
    private ImageView img;
    private JSONArray ingredientsArr;
    public String api_key = BuildConfig.SP_API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_page);

        // Init views
        foodName = findViewById(R.id.recipeFoodName);
        recipeText = findViewById(R.id.recipeText);
        img = findViewById(R.id.recipeFoodImage);
        recipeCookTime = findViewById(R.id.recipeCookTime);
        recipeServings = findViewById(R.id.recipeServings);

        // Get data from intent
        String recipeId = getIntent().getStringExtra("id");
        String title = getIntent().getStringExtra("title");
        String imageUrl = getIntent().getStringExtra("img");
        String cookTime = getIntent().getStringExtra("cooktime");
        String servings = getIntent().getStringExtra("servings");

        ImageButton returnButton = findViewById(R.id.returnToPageButton);
        returnButton.setOnClickListener(v -> finish());

        // Set title immediately
        if (title != null) foodName.setText(title);
        if (cookTime != null) recipeCookTime.setText(cookTime);
        if (servings != null) recipeServings.setText(servings);

        // Load image if available
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).into(img);
        } else {
            img.setImageResource(R.drawable.martabak); // fallback image
        }

        // Fetch recipe details from API
        if (recipeId != null) {
            getRecipeData(recipeId);
        }
    }

    private void getRecipeData(final String recipeId) {
        String URL = "https://api.spoonacular.com/recipes/" + recipeId + "/information?apiKey=" + api_key;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                response -> {
                    try {
                        // ✅ Set cook time & servings directly from response
                        int readyInMinutes = response.optInt("readyInMinutes", -1);
                        int servings = response.optInt("servings", -1);

                        if (readyInMinutes != -1) {
                            recipeCookTime.setText(readyInMinutes + " mins");
                        }
                        if (servings != -1) {
                            recipeServings.setText(servings + " people");
                        }

                        // Ingredients
                        ingredientsArr = response.getJSONArray("extendedIngredients");
                        StringBuilder ingredientsBuilder = new StringBuilder("<b>Ingredients:</b><br>");
                        for (int i = 0; i < ingredientsArr.length(); i++) {
                            JSONObject jsonObject1 = ingredientsArr.getJSONObject(i);
                            ingredientsBuilder.append("• ")
                                    .append(jsonObject1.optString("original"))
                                    .append("<br>");
                        }

                        // Instructions
                        String instructions = response.optString("instructions", "");
                        if (instructions.isEmpty()) {
                            String msg = "Unfortunately, the recipe you were looking for was not found. " +
                                    "To view the original recipe click on the link below:<br>" +
                                    "<a href=" + response.get("spoonacularSourceUrl") + ">"
                                    + response.get("spoonacularSourceUrl") + "</a>";
                            recipeText.setMovementMethod(LinkMovementMethod.getInstance());
                            recipeText.setText(Html.fromHtml(msg));
                        } else {
                            ingredientsBuilder.append("<br><b>Instructions:</b><br>").append(instructions);
                            recipeText.setText(Html.fromHtml(ingredientsBuilder.toString()));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        recipeText.setText("Error parsing recipe details.");
                    }
                },
                error -> {
                    Log.i("RecipeActivity", "the res is error: " + error.toString());
                    recipeText.setText("Failed to load recipe details.");
                }
        );
        requestQueue.add(jsonObjectRequest);
    }
}
