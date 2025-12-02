package com.example.resepmakanan.Adapters;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.resepmakanan.APIService.ApiConfig;
import com.example.resepmakanan.Activities.RecipeActivity;
import com.example.resepmakanan.Models.Recipe;
import com.example.resepmakanan.R;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class RecipeCardAdapter extends RecyclerView.Adapter<RecipeCardAdapter.ViewHolder>{
    public interface OnFavoriteClickListener {
        void onFavoriteClick(Recipe recipe, int position);
    }

    private final OnFavoriteClickListener listener;

    private Context mContext ;
    private List<Recipe> data;

    public RecipeCardAdapter(Context mContext, List<Recipe> mData, OnFavoriteClickListener listener) {
        this.mContext = mContext;
        this.data = mData;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe r = data.get(position);

        holder.tvName.setText(r.getNamaResep());

        // Porsi & durasi
        holder.tvPorsi.setText(r.getPorsi());   // contoh: "4-6 porsi"
        holder.tvDurasi.setText(r.getDurasi()); // contoh: "1 Jam"

        // Rating
        if (r.getAvgRating() > 0) {
            holder.tvRating.setText(String.format(Locale.getDefault(),
                    "%.1f", r.getAvgRating()));
        } else {
            holder.tvRating.setText("4.8"); // dummy kalau belum ada rating beneran
        }

        // ====== LOAD GAMBAR DARI SERVER PAKAI GLIDE ======
        String imageUrl = ApiConfig.IMAGE_BASE_URL + r.getGambar();
        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_launcher_background) // bebas, placeholder sementara
                .error(R.drawable.ic_launcher_background)        // kalau gagal load
                .into(holder.ivThumb);

        holder.cbFav.setOnCheckedChangeListener(null); // cegah trigger saat bind
        holder.cbFav.setChecked(r.isFavorite());

        holder.cbFav.setOnCheckedChangeListener((buttonView, isChecked) -> {
            r.setFavorite(isChecked);

            if (listener != null) {
                listener.onFavoriteClick(r, holder.getAdapterPosition());
            }
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, RecipeActivity.class);
            intent.putExtra("recipe_id", r.getId());
            intent.putExtra("title", r.getNamaResep());
            intent.putExtra("img", ApiConfig.IMAGE_BASE_URL + r.getGambar());
            intent.putExtra("cooktime", r.getDurasi());
            intent.putExtra("servings", r.getPorsi());
            mContext.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumb;
        TextView tvName;
        TextView tvPorsi, tvDurasi, tvRating;
        CheckBox cbFav;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumb = itemView.findViewById(R.id.ivThumb);
            tvName = itemView.findViewById(R.id.tvName);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvPorsi = itemView.findViewById(R.id.tvPorsi);
            tvDurasi = itemView.findViewById(R.id.tvDurasi);
            cbFav = itemView.findViewById(R.id.cbFavorite);
        }
    }
}