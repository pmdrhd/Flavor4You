package com.example.resepmakanan.Fragment;

import static com.example.resepmakanan.Requests.FavoriteRequests.addFavorite;
import static com.example.resepmakanan.Requests.FavoriteRequests.removeFavorite;

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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.resepmakanan.Adapters.HomeCategoryAdapter;
import com.example.resepmakanan.Adapters.SearchCategoryAdapter;
import com.example.resepmakanan.BuildConfig;
import com.example.resepmakanan.Managers.SessionManager;
import com.example.resepmakanan.Models.Recipe;
import com.example.resepmakanan.R;
import com.example.resepmakanan.Adapters.RecipeCardAdapter;
import com.example.resepmakanan.APIService.ApiConfig;
import com.example.resepmakanan.Requests.FavoriteRequests;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerView, categoryRV;
    private ProgressBar progressBar;
    private int userId;
    private TextView emptyView;
    private EditText searchInput;
    private ImageButton searchButton;
    private List<Recipe> listRecipe = new ArrayList<>();
    private RecipeCardAdapter adapter;
    private RequestQueue queue;
    public String api_key = BuildConfig.SP_API_KEY; // put your key here
    SearchCategoryAdapter categoryAdapter;

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

        adapter = new RecipeCardAdapter(getContext(), listRecipe, this::onFavoriteClick);
        recyclerView.setAdapter(adapter);

        searchButton.setOnClickListener(v -> {
            String query = searchInput.getText().toString().trim();
            if (!TextUtils.isEmpty(query)) {
                searchRecipes(query);
            }
        });

        ArrayList<String> categories = new ArrayList<>(Arrays.asList(
                "Breakfast",
                "Lunch",
                "Dinner",
                "Snack",
                "Dessert",
                "Beverage"
        ));

        categoryRV = root.findViewById(R.id.search_category_recylcerview);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        categoryRV.setLayoutManager(gridLayoutManager);
        categoryAdapter = new SearchCategoryAdapter(getActivity(), categories);
        categoryRV.setAdapter(categoryAdapter);

        SessionManager session = new SessionManager(requireContext());
        userId = session.getId();

        adapter = new RecipeCardAdapter(getContext(), listRecipe, (recipe, position) -> {
            RequestQueue queue = Volley.newRequestQueue(requireContext());

            if (recipe.isFavorite()) {
                FavoriteRequests.addFavorite(
                        getContext(), queue, userId, recipe.getId(),
                        () -> {},
                        msg -> {}
                );
            } else {
                FavoriteRequests.removeFavorite(
                        getContext(), queue, userId, recipe.getId(),
                        () -> {},
                        msg -> {}
                );
            }
        });
        return root;
    }

    public void onFavoriteClick(Recipe recipe, int position) {
        if (recipe.isFavorite()) {
            removeFavorite(recipe, position);
        } else {
            addFavorite(recipe, position);
        }
    }

    private void addFavorite(Recipe recipe, int position) {
        FavoriteRequests.addFavorite(getContext(), queue, userId, recipe.getId(),
                () -> {
                    recipe.setFavorite(true);
                    adapter.notifyItemChanged(position);
                },
                msg -> Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show()
        );
    }

    private void removeFavorite(Recipe recipe, int position) {
        FavoriteRequests.removeFavorite(getContext(), queue, userId, recipe.getId(),
                () -> {
                    recipe.setFavorite(false);
                    adapter.notifyItemChanged(position);
                },
                msg -> Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show()
        );
    }

    private void searchRecipes(String query) {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setAlpha(0);
        emptyView.setVisibility(View.GONE);
        categoryRV.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        String url = ApiConfig.URL_SEARCH_RECIPES + "?q=" + query;

        queue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        boolean success = response.getBoolean("success");
                        if (!success) {
                            progressBar.setVisibility(View.GONE);
                            emptyView.setVisibility(View.VISIBLE);
                            return;
                        }

                        listRecipe.clear();
                        JSONArray arr = response.getJSONArray("data");

                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);

                            Recipe r = new Recipe();
                            r.setId(obj.optInt("id"));
                            r.setNamaResep(obj.optString("nama_resep"));
                            r.setGambar(obj.optString("gambar"));
                            r.setPorsi(obj.optString("porsi"));
                            r.setDurasi(obj.optString("durasi"));
                            r.setAvgRating((float) obj.optDouble("avg_rating", 0));
                            r.setTotalComments(obj.optInt("total_comments", 0));
                            r.setFavorite(obj.optBoolean("favorite", false));

                            listRecipe.add(r);
                        }

                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setAlpha(1);

                        if (listRecipe.isEmpty()) {
                            emptyView.setVisibility(View.VISIBLE);
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
                }
        );

        queue.add(req);
    }
}
