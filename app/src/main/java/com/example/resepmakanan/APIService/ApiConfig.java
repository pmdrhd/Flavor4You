package com.example.resepmakanan.APIService;

import static com.example.resepmakanan.BuildConfig.API_IP_ADDRESS;

public class ApiConfig {
    public static final String BASE_URL = "http://" + API_IP_ADDRESS + "/recipe_api/";

    public static final String URL_GET_RECIPES      = BASE_URL + "recipes/get_recipes.php";
    public static final String URL_ADD_FAVORITE     = BASE_URL + "favorites/add_favorite.php";
    public static final String URL_REMOVE_FAVORITE  = BASE_URL + "favorites/remove_favorite.php";
    public static final String URL_SEARCH_RECIPES = BASE_URL;
    public static final String URL_GET_RECIPE_BY_ID = BASE_URL + "recipes/get_recipe_by_id.php";
    public static final String URL_GET_FAVORITES = BASE_URL + "favorites/get_favorites.php";

    // buat gambar
    public static final String IMAGE_BASE_URL = BASE_URL + "uploads/";
}
