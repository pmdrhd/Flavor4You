package com.example.resepmakanan.Fragment;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.resepmakanan.APIService.ApiConfig;
import com.example.resepmakanan.Adapters.RecipeCardAdapter;
import com.example.resepmakanan.Managers.SessionManager;
import com.example.resepmakanan.Models.Recipe;
import com.example.resepmakanan.Models.SortBottomSheet;
import com.example.resepmakanan.R;
import com.example.resepmakanan.Requests.FavoriteRequests;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class FavoriteFragment extends Fragment {

    private RecyclerView rvFav;
    private ProgressBar pbFav;
    private TextView tvEmpty;
    private ImageButton ibSort;

    private ArrayList<Recipe> favList = new ArrayList<>();
    private RecipeCardAdapter adapter;
    private RequestQueue queue;

    private int userId;
    private String selectedSort = "az"; // <-- tambahkan variable sortir

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.favorit_page, container, false);

        rvFav = view.findViewById(R.id.rvFavorites);
        pbFav = view.findViewById(R.id.pbFavorites);
        tvEmpty = view.findViewById(R.id.tvEmptyFav);
        ibSort = view.findViewById(R.id.ibSort);

        rvFav.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecipeCardAdapter(getContext(), favList, this::handleFavoriteClick);
        rvFav.setAdapter(adapter);

        queue = Volley.newRequestQueue(requireContext());
        userId = new SessionManager(requireContext()).getId();

        ibSort.setOnClickListener(v -> {
            SortBottomSheet sheet = new SortBottomSheet();
            sheet.setListener(sort -> {
                selectedSort = sort;
                loadFavoritesAndRecipes();
            }, selectedSort);
            sheet.show(getParentFragmentManager(), "sort");
        });

        loadFavoritesAndRecipes();

        return view;
    }

    private void loadFavoritesAndRecipes() {
        pbFav.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        // 1️⃣ Ambil daftar favorit user
        String urlFav = ApiConfig.URL_GET_FAVORITES + "?user_id=" + userId;
        JsonObjectRequest favRequest = new JsonObjectRequest(
                Request.Method.GET,
                urlFav,
                null,
                response -> {
                    Set<Integer> userFavorites = new HashSet<>();
                    try {
                        if (response.getBoolean("success")) {
                            JSONArray arr = response.getJSONArray("data");
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject obj = arr.getJSONObject(i);
                                userFavorites.add(obj.getInt("id"));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // 2️⃣ Setelah favorit didapat, load resep dengan sort
                    loadAllRecipes(userFavorites, selectedSort);
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(getContext(), "Gagal load favorit", Toast.LENGTH_SHORT).show();
                    loadAllRecipes(new HashSet<>(), selectedSort);
                }
        );
        queue.add(favRequest);
    }

    private void loadAllRecipes(Set<Integer> userFavorites, String sort) {
        pbFav.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        String urlRecipes = ApiConfig.URL_GET_RECIPES + "?sort=" + sort;
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET,
                urlRecipes,
                null,
                response -> {
                    pbFav.setVisibility(View.GONE);
                    favList.clear();

                    try {
                        if (!response.getBoolean("success")) {
                            tvEmpty.setText("Gagal load resep");
                            tvEmpty.setVisibility(View.VISIBLE);
                            return;
                        }

                        JSONArray arr = response.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject o = arr.getJSONObject(i);
                            int id = o.getInt("id");
                            if (!userFavorites.contains(id)) continue;

                            Recipe r = new Recipe();
                            r.setId(id);
                            r.setNamaResep(o.getString("nama_resep"));
                            r.setGambar(o.getString("gambar"));
                            r.setBahan(o.getString("bahan"));
                            r.setInstruksi(o.getString("instruksi"));
                            r.setPorsi(o.optString("porsi", ""));
                            r.setDurasi(o.optString("durasi", ""));
                            r.setFavorite(true);

                            favList.add(r);
                        }

                        if (favList.isEmpty()) {
                            tvEmpty.setText("Belum ada favorit");
                            tvEmpty.setVisibility(View.VISIBLE);
                        }

                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        tvEmpty.setText("Error parsing data");
                        tvEmpty.setVisibility(View.VISIBLE);
                    }
                },
                error -> {
                    pbFav.setVisibility(View.GONE);
                    tvEmpty.setText("Gagal load resep");
                    tvEmpty.setVisibility(View.VISIBLE);
                }
        );
        queue.add(req);
    }

    private void handleFavoriteClick(Recipe r, int pos) {
        if (!r.isFavorite()) {
            FavoriteRequests.removeFavorite(getContext(), queue, userId, r.getId(),
                    () -> {
                        favList.remove(pos);
                        adapter.notifyItemRemoved(pos);
                        if (favList.isEmpty()) tvEmpty.setVisibility(View.VISIBLE);
                    },
                    msg -> Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show()
            );
        }
    }
}
