package com.example.resepmakanan.AIChat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resepmakanan.R;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_USER = 1;
    private static final int TYPE_BOT  = 2;

    private final List<Message> data = new ArrayList<>();

    @Override
    public int getItemViewType(int position) {
        return data.get(position).getSender() == Message.Sender.USER ? TYPE_USER : TYPE_BOT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_USER) {
            View v = inf.inflate(R.layout.item_message_user, parent, false);
            return new UserVH(v);
        } else {
            View v = inf.inflate(R.layout.item_message_bot, parent, false);
            return new BotVH(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message m = data.get(position);
        if (holder instanceof UserVH) {
            ((UserVH) holder).txt.setText(m.getText());
        } else if (holder instanceof BotVH) {
            ((BotVH) holder).txt.setText(m.getText());
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void add(Message m) {
        data.add(m);
        notifyItemInserted(data.size() - 1);
    }


    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    static class UserVH extends RecyclerView.ViewHolder {
        final TextView txt;
        UserVH(@NonNull View itemView) {
            super(itemView);
            txt = itemView.findViewById(R.id.txtUser);
        }
    }

    static class BotVH extends RecyclerView.ViewHolder {
        final TextView txt;
        BotVH(@NonNull View itemView) {
            super(itemView);
            txt = itemView.findViewById(R.id.txtBot);
        }
    }
}
