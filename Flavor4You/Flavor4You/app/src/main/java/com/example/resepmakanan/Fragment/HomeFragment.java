package com.example.resepmakanan.Fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.resepmakanan.APIService.ApiConfig;
import com.example.resepmakanan.Adapters.HomeCategoryAdapter;
import com.example.resepmakanan.BuildConfig;
import com.example.resepmakanan.Managers.SessionManager;
import com.example.resepmakanan.Models.CategoryItem;
import com.example.resepmakanan.Models.Recipe;
import com.example.resepmakanan.R;
import com.example.resepmakanan.Adapters.RecipeCardAdapter;
import com.example.resepmakanan.Requests.FavoriteRequests;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {

    private List<Recipe> listRecipe = new ArrayList<>();
    private List<Integer> userFavorites = new ArrayList<>();
    private JSONArray testArr;
    private RecyclerView myrv;
    private ProgressBar progressBar;
    private TextView emptyView, tvUsername;
    public String api_key = BuildConfig.SP_API_KEY;
    private RecipeCardAdapter adapter;
    private RequestQueue queue;
    RecyclerView homeCategoryRecycler;
    HomeCategoryAdapter categoryAdapter;
    private int userId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View RootView = inflater.inflate(R.layout.home_page, container, false);
        progressBar = RootView.findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.VISIBLE);
        myrv = RootView.findViewById(R.id.recycler_item);
        myrv.setLayoutManager(new GridLayoutManager(getActivity(), LinearLayoutManager.VERTICAL));
        emptyView = RootView.findViewById(R.id.emptyView);
        tvUsername = RootView.findViewById(R.id.homeUsername);

        adapter = new RecipeCardAdapter(getContext(), listRecipe, this::onFavoriteClick);
        myrv.setAdapter(adapter);

        queue = Volley.newRequestQueue(getContext());

        SessionManager session = new SessionManager(requireContext());
        userId = session.getId();

        if (session.isLoggedIn()) {
            tvUsername.setText("Hi, " + session.getUsername());
        } else {
            tvUsername.setText("Hi, Guest");
        }

        homeCategoryRecycler = RootView.findViewById(R.id.home_category_recyclerview);
        homeCategoryRecycler.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false)
        );

        categoryAdapter = new HomeCategoryAdapter(getActivity(), new ArrayList<>());
        categoryAdapter.setListener(cat -> loadByCategory(cat));

        homeCategoryRecycler.setAdapter(categoryAdapter);
        // load kategori dari API
        loadCategories();

        homeCategoryRecycler.setAdapter(categoryAdapter);

        // Load recipes
        loadFavoritesThenRecipes();

        return RootView;
    }

    private void getRandomRecipes() {
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET,
                ApiConfig.URL_GET_RECIPES,
                null,
                response -> {
                    try {
                        boolean success = response.getBoolean("success");
                        if (!success) {
                            Toast.makeText(getContext(), "Gagal load resep", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONArray arr = response.getJSONArray("data");
                        listRecipe.clear();

                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject o = arr.getJSONObject(i);

                            Recipe r = new Recipe();
                            r.setId(o.getInt("id"));
                            r.setNamaResep(o.getString("nama_resep"));
                            r.setGambar(o.getString("gambar"));
                            r.setBahan(o.getString("bahan"));
                            r.setInstruksi(o.getString("instruksi"));
                            r.setPorsi(o.optString("porsi", ""));
                            r.setDurasi(o.optString("durasi", ""));
                            r.setAvgRating((float) o.optDouble("avg_rating", 0));
                            r.setTotalComments(o.optInt("total_comments", 0));

                            r.setFavorite(userFavorites.contains(r.getId()));

                            listRecipe.add(r);
                        }
                        Collections.shuffle(listRecipe);

                        adapter.notifyDataSetChanged();

                        progressBar.setVisibility(View.GONE);
                        myrv.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error json", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(getContext(), "Error koneksi", Toast.LENGTH_SHORT).show();

                    progressBar.setVisibility(View.GONE);
                    myrv.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                }
        );
        queue.add(req);
        Log.d("HomeFragment", "Data recipes fetched");
    }

    private void loadFavoritesThenRecipes() {
        String url = ApiConfig.URL_GET_FAVORITES + "?user_id=" + userId;

        Log.d("HomeFragment", "Loading favorites for userId: " + userId);
        Log.d("HomeFragment", "URL: " + url);

        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        if (response.getBoolean("success")) {

                            JSONArray arr = response.getJSONArray("data");
                            userFavorites.clear();

                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject obj = arr.getJSONObject(i);
                                int favId = obj.getInt("id");   // <-- ambil ID resep favorit
                                userFavorites.add(favId);
                            }
                        }
                    } catch (Exception ignored) {}

                    // setelah dapat data favorit, baru load resep
                    getRandomRecipes();
                },
                error -> {
                    // fallback jika error
                    getRandomRecipes();
                    Log.d("HomeFragment", "Error: Favorite list");
                }
        );

        queue.add(req);
    }

    private void loadCategories() {

        String url = ApiConfig.URL_GET_CATEGORIES; // pastikan URL ini sesuai

        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        if (!response.getBoolean("success")) return;

                        Log.d("CAT_JSON", response.toString());
                        JSONArray arr = response.getJSONArray("data");
                        Log.d("HomeFragment", "arr size:" + arr.length());

                        List<CategoryItem> categories = new ArrayList<>();
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject o = arr.getJSONObject(i);
                            categories.add(new CategoryItem(
                                    o.getString("key"),   // untuk filter API
                                    o.getString("name")   // untuk ditampilkan
                            ));
                        }

                        // update adapter
                        Collections.shuffle(categories);
                        categoryAdapter.updateList(categories);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error Category: JSON", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(getContext(), "Gagal load categories", Toast.LENGTH_SHORT).show();
                }
        );

        queue.add(req);
    }

    private void loadByCategory(String cat) {

        progressBar.setVisibility(View.VISIBLE);

        String url = ApiConfig.URL_SEARCH_RECIPES + "?kategori=" + cat + "&user_id=" + userId;

        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                resp -> {
                    try {
                        listRecipe.clear();

                        if (!resp.getBoolean("success")) {
                            adapter.notifyDataSetChanged();
                            emptyView.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            return;
                        }

                        JSONArray arr = resp.getJSONArray("data");

                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject o = arr.getJSONObject(i);

                            Recipe r = new Recipe();
                            r.setId(o.getInt("id"));
                            r.setNamaResep(o.getString("nama_resep"));
                            r.setGambar(o.getString("gambar"));
                            r.setBahan(o.optString("bahan", ""));
                            r.setInstruksi(o.optString("instruksi", ""));
                            r.setPorsi(o.optString("porsi", ""));
                            r.setDurasi(o.optString("durasi", ""));
                            r.setAvgRating((float) o.optDouble("avg_rating", 0));
                            r.setTotalComments(o.optInt("total_comments", 0));

                            // gunakan fallback
                            int fav = o.optInt("favorite", o.optInt("is_favorite", 0));
                            r.setFavorite(fav == 1);

                            listRecipe.add(r);
                        }

                        adapter.notifyDataSetChanged();

                        emptyView.setVisibility(listRecipe.isEmpty() ? View.VISIBLE : View.GONE);
                        progressBar.setVisibility(View.GONE);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                err -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Gagal load data kategori", Toast.LENGTH_SHORT).show();
                }
        );

        queue.add(req);
    }

    public void onFavoriteClick(Recipe recipe, int position) {
        if (recipe.isFavorite()) {
            addFavorite(recipe, position);
        } else {
            removeFavorite(recipe, position);
        }
    }

    private void addFavorite(Recipe recipe, int position) {
        FavoriteRequests.addFavorite(getContext(), queue, userId, recipe.getId(),
                () -> {
                    recipe.setFavorite(true);
                    adapter.notifyItemChanged(position);
                    Toast.makeText(getContext(), "Ditambahkan ke favorit", Toast.LENGTH_SHORT).show();
                },
                msg -> Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show()
        );
    }

    private void removeFavorite(Recipe recipe, int position) {
        FavoriteRequests.removeFavorite(getContext(), queue, userId, recipe.getId(),
                () -> {
                    recipe.setFavorite(false);
                    adapter.notifyItemChanged(position);
                    Toast.makeText(getContext(), "Dihapus dari favorit", Toast.LENGTH_SHORT).show();
                },
                msg -> Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show()
        );
    }
}
