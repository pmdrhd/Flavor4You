package com.example.resepmakanan.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resepmakanan.Models.CategoryItem;
import com.example.resepmakanan.R;

import java.util.List;

public class HomeCategoryAdapter extends RecyclerView.Adapter<HomeCategoryAdapter.MyViewHolder> {

    Context context;
    List<CategoryItem> categoryList;

    // Kategori dipilih -> KEY
    String selectedKey = "";

    OnCategorySelected listener;

    public interface OnCategorySelected {
        void onSelected(String selectedKey);
    }

    public void setListener(OnCategorySelected listener) {
        this.listener = listener;
    }

    public HomeCategoryAdapter(Context context, List<CategoryItem> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    /** ================= UPDATE LIST ================== */
    public void updateList(List<CategoryItem> newList) {
        Log.d("CAT", "Jumlah kategori diterima: " + newList.size());

        this.categoryList = newList;
        selectedKey = "";
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category_home, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        CategoryItem item = categoryList.get(position);

        // TAMPILKAN "name"
        holder.tvCategory.setText(item.name);

        boolean isSelected = item.key.equals(selectedKey);

        holder.tvCategory.setBackgroundColor(
                isSelected ? Color.parseColor("#c49060") : Color.WHITE
        );

        holder.itemView.setOnClickListener(v -> {

            if (isSelected) selectedKey = "";
            else selectedKey = item.key;

            notifyDataSetChanged();

            if (listener != null) listener.onSelected(selectedKey);
        });
    }

    @Override
    public int getItemCount() {
        return categoryList != null ? categoryList.size() : 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory;
        CardView cardContainer;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.categoryHomeText);
            cardContainer = itemView.findViewById(R.id.categoryHomeCard);
        }
    }
}
