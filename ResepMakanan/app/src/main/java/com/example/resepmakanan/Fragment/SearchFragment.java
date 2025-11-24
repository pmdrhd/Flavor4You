package com.example.resepmakanan.Fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.resepmakanan.BuildConfig;
import com.example.resepmakanan.Models.Recipe;
import com.example.resepmakanan.R;
import com.example.resepmakanan.RecyclerView.RecyclerViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyView;
    private EditText searchInput;
    private ImageButton searchButton;
    private List<Recipe> listRecipe = new ArrayList<>();
    private RecyclerViewAdapter adapter;
    public String api_key = BuildConfig.SP_API_KEY; // put your key here

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.search_page, container, false);

        recyclerView = root.findViewById(R.id.recycler_search);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), LinearLayoutManager.VERTICAL));
        progressBar = root.findViewById(R.id.progressBarSearch);
        emptyView = root.findViewById(R.id.emptyViewSearch);
        searchInput = root.findViewById(R.id.searchInput);
        searchButton = root.findViewById(R.id.searchButton);

        adapter = new RecyclerViewAdapter(getContext(), listRecipe);
        recyclerView.setAdapter(adapter);

        searchButton.setOnClickListener(v -> {
            String query = searchInput.getText().toString().trim();
            if (!TextUtils.isEmpty(query)) {
                searchRecipes(query);
            }
        });

        return root;
    }

    private void searchRecipes(String query) {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setAlpha(0);
        emptyView.setVisibility(View.GONE);

        String url = "https://api.spoonacular.com/recipes/complexSearch?query="
                + query + "&number=20&addRecipeInformation=true&apiKey=" + api_key;

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        listRecipe.clear();
                        JSONArray results = response.getJSONArray("results");

                        for (int i = 0; i < results.length(); i++) {
                            JSONObject obj = results.getJSONObject(i);
                            listRecipe.add(new Recipe(
                                    obj.optString("id"),
                                    obj.optString("title"),
                                    obj.optString("image"),
                                    obj.optInt("servings",0),
                                    obj.optInt("readyInMinutes", 0)
                            ));
                        }

                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setAlpha(1);

                        if (listRecipe.isEmpty()) {
                            emptyView.setVisibility(View.VISIBLE);
                        } else {
                            emptyView.setVisibility(View.GONE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                    }
                },
                error -> {
                    Log.e("SearchFragment", "Error: " + error.toString());
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setAlpha(0);
                    emptyView.setVisibility(View.VISIBLE);
                });

        requestQueue.add(jsonObjectRequest);
    }
}
