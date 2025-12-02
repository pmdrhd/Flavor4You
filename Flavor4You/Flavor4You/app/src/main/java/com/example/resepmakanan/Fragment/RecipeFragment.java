package com.example.resepmakanan.Fragment;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.resepmakanan.BuildConfig;
import com.example.resepmakanan.R;
import com.example.resepmakanan.APIService.ApiConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecipeFragment extends Fragment {

    private int recipeId;
    private JSONArray ingredientsArr;

    private TextView recipeText, recipeCookTime, recipeServings, recipeName, tvErrorMessage;
    private FrameLayout flMain;
    private ProgressBar pbMain;
    private String api_key = BuildConfig.SP_API_KEY;

    public static RecipeFragment newInstance(int recipeId) {
        RecipeFragment fragment = new RecipeFragment();
        Bundle args = new Bundle();
        args.putInt("recipe_id", recipeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            recipeId = getArguments().getInt("recipe_id", -1);
            //Toast.makeText(getContext(),recipeId,Toast.LENGTH_LONG).show();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.recipe_fragment, container, false);

        // Init views
        recipeName = view.findViewById(R.id.recipeFoodName);
        recipeText = view.findViewById(R.id.recipeText);
        recipeCookTime = view.findViewById(R.id.recipeCookTime);
        recipeServings = view.findViewById(R.id.recipeServings);

        flMain = view.findViewById(R.id.main);
        pbMain = view.findViewById(R.id.recipeProgressBar);
        tvErrorMessage = view.findViewById(R.id.tvErrorMessage);

        if (recipeId != -1) {
            getRecipeData(recipeId);
        } else {
            Toast.makeText(getContext(),"Error: recipeId not found.",Toast.LENGTH_LONG).show();
        }

        return view;
    }

    private void getRecipeData(final int recipeId) {
        pbMain.setVisibility(View.VISIBLE);
        flMain.setVisibility(View.GONE);
        tvErrorMessage.setVisibility(View.GONE);

        String url = ApiConfig.URL_GET_RECIPE_BY_ID + "?id=" + recipeId;
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    pbMain.setVisibility(View.GONE);

                    try {
                        boolean success = response.optBoolean("success", false);
                        if (!success) {
                            tvErrorMessage.setVisibility(View.VISIBLE);
                            tvErrorMessage.setText("Recipe not found.");
                            Toast.makeText(getContext(),"Recipe not found.",Toast.LENGTH_LONG).show();
                            return;
                        }

                        JSONObject recipeObj = response.getJSONObject("data");

                        // === BASIC INFO ===
                        String title = recipeObj.optString("nama_resep", "No Title");
                        String cookTime = recipeObj.optString("durasi", "-");
                        String servings = recipeObj.optString("porsi", "-");

                        // set ke UI
                        recipeName.setText(title);
                        recipeCookTime.setText(cookTime);
                        recipeServings.setText(servings);

                        // === INGREDIENTS ===
                        // API mengirim bahan dalam 1 string panjang (dipisah newline)
                        String bahanString = recipeObj.optString("bahan", "");
                        String[] ingredients = bahanString.split("\r\n|\n");

                        // Build HTML
                        StringBuilder sb = new StringBuilder("<b>Ingredients:</b><br>");
                        for (String item : ingredients) {
                            sb.append("• ").append(item).append("<br>");
                        }

                        // === INSTRUCTIONS ===
                        String instructions = recipeObj.optString("instruksi", "No instructions available.");
                        sb.append("<br><b>Instructions:</b><br>")
                                .append(instructions.replace("\n", "<br>"));

                        recipeText.setText(Html.fromHtml(sb.toString()));


                        // SHOW UI
                        flMain.setVisibility(View.VISIBLE);
                        tvErrorMessage.setVisibility(View.GONE);

                    } catch (JSONException e) {
                        Toast.makeText(getContext(),"Error parsing recipe data.",Toast.LENGTH_LONG).show();
                        tvErrorMessage.setText("Error parsing recipe data.");
                        flMain.setVisibility(View.GONE);
                        tvErrorMessage.setVisibility(View.VISIBLE);
                    }
                },

                error -> {
                    pbMain.setVisibility(View.GONE);
                    flMain.setVisibility(View.GONE);
                    tvErrorMessage.setVisibility(View.VISIBLE);

                    tvErrorMessage.setText("Failed to load recipe details.");
                    Toast.makeText(getContext(),"API ERROR.",Toast.LENGTH_LONG).show();
                    Log.e("RecipeFragment", "API ERROR: " + error.toString());
                }
        );
        queue.add(req);
    }
}
