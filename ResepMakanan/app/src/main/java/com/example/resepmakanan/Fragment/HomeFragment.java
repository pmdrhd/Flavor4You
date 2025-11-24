package com.example.resepmakanan.Fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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

public class HomeFragment extends Fragment {

    private List<Recipe> listRecipe = new ArrayList<>();
    private JSONArray testArr;
    private RecyclerView myrv;
    private ProgressBar progressBar;
    private TextView emptyView;
    public String api_key = BuildConfig.SP_API_KEY;

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
        getRandomRecipes();

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        // Inflate the layout for this fragment
        return RootView;
    }

    private void getRandomRecipes() {
        String URL = "https://api.spoonacular.com/recipes/random?number=40&apiKey="+api_key;
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest( Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            testArr = (JSONArray) response.get("recipes");
                            Log.i("the res is:", String.valueOf(testArr));
                            for (int i = 0; i < testArr.length(); i++) {
                                JSONObject jsonObject1;
                                jsonObject1 = testArr.getJSONObject(i);
                                listRecipe.add(new Recipe(jsonObject1.optString("id"),
                                        jsonObject1.optString("title"), jsonObject1.optString("image"),
                                        Integer.parseInt(jsonObject1.optString("servings")),
                                        Integer.parseInt(jsonObject1.optString("readyInMinutes" ))));
                            }
                            RecyclerViewAdapter myAdapter = new RecyclerViewAdapter(getContext(), listRecipe);
                            myrv.setAdapter(myAdapter);
                            progressBar.setVisibility(View.GONE);
                            myrv.setVisibility(View.VISIBLE);
                            emptyView.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("the res is error:", error.toString());
                        progressBar.setVisibility(View.GONE);
                        myrv.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }
}
