package com.example.resepmakanan.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resepmakanan.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder>{

    Context context;
    List<String> categoryList;

    public CategoryAdapter(Context context, List<String> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tvCategory.setText(categoryList.get(position));
        // Jika ingin ditambah onClick, bisa di sini
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory;
        CardView cardContainer;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.categoryText);
            cardContainer = itemView.findViewById(R.id.categoryCard);
        }
    }
}
