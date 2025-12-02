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
import com.example.resepmakanan.Adapters.RecipeCardAdapter;
import com.example.resepmakanan.Adapters.SearchCategoryAdapter;
import com.example.resepmakanan.BuildConfig;
import com.example.resepmakanan.Managers.SessionManager;
import com.example.resepmakanan.Models.CategoryItem;
import com.example.resepmakanan.Models.Recipe;
import com.example.resepmakanan.Models.SortBottomSheet;
import com.example.resepmakanan.R;
import com.example.resepmakanan.APIService.ApiConfig;
import com.example.resepmakanan.Requests.FavoriteRequests;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchFragment extends Fragment {

    private RecyclerView rvResult, rvCat;
    private ProgressBar pb;
    private EditText etSearch;
    private ImageButton btnSearch, btnSort, btnClear;
    private TextView tvEmpty, tvTopCat;

    private List<Recipe> listRecipe = new ArrayList<>();
    private RecipeCardAdapter adapter;

    private SearchCategoryAdapter catAdapter;
    private List<String> selectedCategories = new ArrayList<>();
    private String selectedSort = "az";

    private RequestQueue queue;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle saved) {
        View v = inflater.inflate(R.layout.search_page, parent, false);

        pb = v.findViewById(R.id.pbSearch);
        etSearch = v.findViewById(R.id.etNameInput);
        btnSearch = v.findViewById(R.id.btnSearch);
        btnSort = v.findViewById(R.id.ibSort);
        btnClear = v.findViewById(R.id.btnClear);

        tvEmpty = v.findViewById(R.id.emptyViewSearch);
        tvTopCat = v.findViewById(R.id.tvTopCat);

        queue = Volley.newRequestQueue(requireContext());
        SessionManager s = new SessionManager(requireContext());
        userId = s.getId();

        rvResult = v.findViewById(R.id.rvResult);
        rvResult.setLayoutManager(new GridLayoutManager(getActivity(), LinearLayoutManager.VERTICAL));

        adapter = new RecipeCardAdapter(getContext(), listRecipe, this::onFavoriteClick);
        rvResult.setAdapter(adapter);

        rvCat = v.findViewById(R.id.rvCategory);
        rvCat.setLayoutManager(new GridLayoutManager(getActivity(), 2)); // tetap 2 kolom grid

        catAdapter = new SearchCategoryAdapter(getContext(), new ArrayList<>());
        catAdapter.setListener(cat -> {
            selectedCategories.clear();
            selectedCategories.add(cat); // hanya satu kategori, kalau mau multi, bisa list.add()
            loadRecipes();
        });
        rvCat.setAdapter(catAdapter);

        btnSearch.setOnClickListener(i -> {
            selectedCategories.clear();
            loadRecipes();
        });

        btnSort.setOnClickListener(i -> {
            SortBottomSheet sheet = new SortBottomSheet();
            sheet.setListener(sort -> {
                selectedSort = sort;
                loadRecipes();
            }, selectedSort);
            sheet.show(getParentFragmentManager(), "sort");
        });

        btnClear.setOnClickListener(i -> etSearch.setText(""));

        loadCategories(); // ambil kategori dari server

        return v;
    }

    private void loadCategories() {
        rvCat.setVisibility(View.GONE);
        pb.setVisibility(View.VISIBLE);

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
                        catAdapter.updateList(categories);

                        rvCat.setVisibility(View.VISIBLE);
                        pb.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error Category: JSON", Toast.LENGTH_SHORT).show();
                        rvCat.setVisibility(View.GONE);
                        tvEmpty.setVisibility(View.VISIBLE);
                    }
                },
                error -> {
                    Toast.makeText(getContext(), "Gagal load categories", Toast.LENGTH_SHORT).show();
                    rvCat.setVisibility(View.GONE);
                    tvEmpty.setVisibility(View.VISIBLE);
                }
        );
        queue.add(req);
    }

    private void loadRecipes() {
        pb.setVisibility(View.VISIBLE);
        rvCat.setVisibility(View.GONE);
        tvTopCat.setText("Results");
        tvEmpty.setVisibility(View.GONE);

        String q = etSearch.getText().toString().trim();
        StringBuilder url = new StringBuilder(ApiConfig.URL_SEARCH_RECIPES + "?user_id=" + userId);

        if (!q.isEmpty()) url.append("&search=").append(q);
        if (!selectedCategories.isEmpty()) url.append("&kategori=").append(TextUtils.join(",", selectedCategories));
        url.append("&sort=").append(selectedSort);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url.toString(), null,
                resp -> {
                    try {
                        listRecipe.clear();
                        if (!resp.getBoolean("success")) {
                            adapter.notifyDataSetChanged();
                            pb.setVisibility(View.GONE);
                            return;
                        }
                        JSONArray arr = resp.getJSONArray("data");

                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject o = arr.getJSONObject(i);
                            Recipe r = new Recipe();
                            r.setId(o.getInt("id"));
                            r.setNamaResep(o.getString("nama_resep"));
                            r.setGambar(o.getString("gambar"));
                            r.setPorsi(o.optString("porsi"));
                            r.setDurasi(o.optString("durasi"));
                            r.setAvgRating((float) o.optDouble("avg_rating", 0));
                            r.setTotalComments(o.optInt("total_comments", 0));
                            r.setFavorite(o.getInt("favorite") == 1);
                            listRecipe.add(r);
                        }

                        if (arr.length() == 0) {
                            tvEmpty.setVisibility(View.VISIBLE);
                        }

                        adapter.notifyDataSetChanged();
                        pb.setVisibility(View.GONE);
                        rvResult.setVisibility(View.VISIBLE);

                    } catch (Exception e) {
                        pb.setVisibility(View.GONE);
                        e.printStackTrace();
                        tvEmpty.setVisibility(View.VISIBLE);
                    }
                }, err -> {
            pb.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);
        });
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
