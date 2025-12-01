package com.example.resepmakanan.Requests;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.example.resepmakanan.APIService.ApiConfig;

import java.util.HashMap;
import java.util.Map;

public class FavoriteRequests {

    public interface SimpleCallback {
        void onDone();
    }

    public interface ErrorCallback {
        void onError(String message);
    }

    public static void addFavorite(Context ctx,
                                   RequestQueue queue,
                                   int userId,
                                   int recipeId,
                                   SimpleCallback onOk,
                                   ErrorCallback onError) {

        StringRequest req = new StringRequest(
                Request.Method.POST,
                ApiConfig.URL_ADD_FAVORITE,
                response -> {
                    // bisa cek JSON kalau mau, untuk simple kita anggap success
                    if (onOk != null) onOk.onDone();
                },
                error -> {
                    if (onError != null) onError.onError("Gagal tambah favorite");
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> p = new HashMap<>();
                p.put("user_id", String.valueOf(userId));
                p.put("recipe_id", String.valueOf(recipeId));
                return p;
            }
        };

        queue.add(req);
    }

    public static void removeFavorite(Context ctx,
                                      RequestQueue queue,
                                      int userId,
                                      int recipeId,
                                      SimpleCallback onOk,
                                      ErrorCallback onError) {

        StringRequest req = new StringRequest(
                Request.Method.POST,
                ApiConfig.URL_REMOVE_FAVORITE,
                response -> {
                    if (onOk != null) onOk.onDone();
                },
                error -> {
                    if (onError != null) onError.onError("Gagal hapus favorite");
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> p = new HashMap<>();
                p.put("user_id", String.valueOf(userId));
                p.put("recipe_id", String.valueOf(recipeId));
                return p;
            }
        };

        queue.add(req);
    }
}
