package com.example.resepmakanan.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resepmakanan.Activities.RecipeActivity;
import com.example.resepmakanan.Models.Recipe;
import com.example.resepmakanan.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>{
    private Context mContext ;
    private List<Recipe> mData ;

    public RecyclerViewAdapter(Context mContext, List<Recipe> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.item_recipe,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.tv_recipe_title.setText(mData.get(position).getTitle());
        holder.tv_ready_in_mins.setText(mData.get(position).getReadyInMins() + " Minutes");
        holder.tv_amount_of_dishes.setText(mData.get(position).getServings() + " Servings");
        if (mData.get(position).getImage().isEmpty()) {
            holder.img_recipe_thumbnail.setImageResource(R.drawable.martabak);
        } else{
            Picasso.get().load(mData.get(position).getImage()).into(holder.img_recipe_thumbnail);
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, RecipeActivity.class);
                intent.putExtra("id",mData.get(position).getId());
                intent.putExtra("title",mData.get(position).getTitle());
                intent.putExtra("img",mData.get(position).getImage());
                intent.putExtra("cooktime",mData.get(position).getReadyInMins());
                intent.putExtra("servings",mData.get(position).getServings());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_recipe_title,tv_ready_in_mins,tv_amount_of_dishes;
        ImageView img_recipe_thumbnail;
        CardView cardView ;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_recipe_title = (TextView) itemView.findViewById(R.id.foodName) ;
            tv_recipe_title.setSelected(true);
            tv_recipe_title.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            tv_recipe_title.setSingleLine(true);
            img_recipe_thumbnail = (ImageView) itemView.findViewById(R.id.foodImage);
            tv_ready_in_mins = (TextView) itemView.findViewById(R.id.cookTime);
            tv_amount_of_dishes = (TextView) itemView.findViewById(R.id.servings);
            cardView = (CardView) itemView.findViewById(R.id.item_recipe_id);
        }
    }
}