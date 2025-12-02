package com.example.resepmakanan.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resepmakanan.Models.CategoryItem;
import com.example.resepmakanan.R;

import java.util.ArrayList;
import java.util.List;

public class SearchCategoryAdapter extends RecyclerView.Adapter<SearchCategoryAdapter.MyViewHolder> {

    public interface OnCategorySelected {
        void onSelected(String selectedKey);
    }

    Context context;
    List<CategoryItem> categoryList;
    String selectedKey = "";
    OnCategorySelected listener;

    public SearchCategoryAdapter(Context context, List<CategoryItem> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    public void setListener(SearchCategoryAdapter.OnCategorySelected listener) {
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category_search, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        CategoryItem item = categoryList.get(position);
        holder.tvCategory.setText(item.name); // TAMPILKAN "name"

        boolean isSelected = item.key.equals(selectedKey);

        // Apply UI state
        int primaryColor = ContextCompat.getColor(context, R.color.primary);
        int defaultBg   = ContextCompat.getColor(context, R.color.categoryDefault);
        int white       = ContextCompat.getColor(context, android.R.color.white);
        int black       = ContextCompat.getColor(context, android.R.color.black);

        holder.cardContainer.setCardBackgroundColor(isSelected ? primaryColor : defaultBg);
        holder.tvCategory.setTextColor(isSelected ? white : black);

        holder.itemView.setOnClickListener(v -> {

            // Re-check state AFTER click
            if (isSelected) selectedKey = "";
            else selectedKey = item.key;

            // Listener ke fragment
            if (listener != null) listener.onSelected(selectedKey);

            // Update tampilan item yang diklik
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    // Update list kategori tanpa ubah referensi list (agar tidak crash)
    public void updateList(List<CategoryItem> newList) {
        this.categoryList = newList;
        selectedKey = "";
        notifyDataSetChanged();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory;
        CardView cardContainer;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvCategory     = itemView.findViewById(R.id.categorySearchText);
            cardContainer  = itemView.findViewById(R.id.categorySearchCard);
        }
    }
}
