package com.example.resepmakanan.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resepmakanan.Models.Comment;
import com.example.resepmakanan.R;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    public interface OnCommentActionListener {
        void onVoteChanged(Comment comment, int likeDelta, int dislikeDelta);
        void onReplyClicked(Comment comment);
        void onToggleReplies(Comment parent);
    }

    private final Context context;
    private List<Comment> commentList;
    private final OnCommentActionListener listener;

    public CommentAdapter(Context context, List<Comment> commentList, OnCommentActionListener listener) {
        this.context = context;
        this.commentList = commentList;
        this.listener = listener;
    }

    public void setData(List<Comment> list) {
        this.commentList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment c = commentList.get(position);

        holder.tvName.setText(c.getUserName());
        holder.tvDate.setText(c.getCreatedAt());
        holder.tvComment.setText(c.getCommentText());
        holder.ratingBar.setRating(c.getRating());

        holder.tvLike.setText(String.valueOf(c.getLikeCount()));
        holder.tvDislike.setText(String.valueOf(c.getDislikeCount()));

        // Style utk reply vs parent
        if (c.isReply()) {
            holder.ratingBar.setVisibility(View.GONE);
            holder.replyBar.setVisibility(View.VISIBLE);

            LinearLayout.LayoutParams lp =
                    (LinearLayout.LayoutParams) holder.contentWrapper.getLayoutParams();
            lp.setMargins(dp(12), dp(4), dp(8), dp(4));
            holder.contentWrapper.setLayoutParams(lp);

            holder.tvComment.setTextSize(12);
            holder.tvToggleReplies.setVisibility(View.GONE);
        } else {
            holder.ratingBar.setVisibility(View.VISIBLE);
            holder.replyBar.setVisibility(View.GONE);

            LinearLayout.LayoutParams lp =
                    (LinearLayout.LayoutParams) holder.contentWrapper.getLayoutParams();
            lp.setMargins(dp(0), dp(4), dp(8), dp(4));
            holder.contentWrapper.setLayoutParams(lp);

            holder.tvComment.setTextSize(13);

            if (c.getReplyCount() > 0) {
                holder.tvToggleReplies.setVisibility(View.VISIBLE);
                if (c.isRepliesCollapsed()) {
                    holder.tvToggleReplies.setText("Show replies (" + c.getReplyCount() + ")");
                } else {
                    holder.tvToggleReplies.setText("Hide replies");
                }
            } else {
                holder.tvToggleReplies.setVisibility(View.GONE);
            }
        }

        updateVoteUi(holder, c);

        holder.btnLike.setOnClickListener(v -> {
            if (listener == null) return;

            int likeDelta = 0;
            int dislikeDelta = 0;

            if (!c.isLiked()) {
                // from none / dislike -> like
                c.setLiked(true);
                c.setLikeCount(c.getLikeCount() + 1);
                likeDelta = 1;

                if (c.isDisliked()) {
                    c.setDisliked(false);
                    if (c.getDislikeCount() > 0) {
                        c.setDislikeCount(c.getDislikeCount() - 1);
                        dislikeDelta = -1;
                    }
                }
            } else {
                // from like -> none
                c.setLiked(false);
                if (c.getLikeCount() > 0) {
                    c.setLikeCount(c.getLikeCount() - 1);
                    likeDelta = -1;
                }
            }

            updateVoteUi(holder, c);
            listener.onVoteChanged(c, likeDelta, dislikeDelta);
        });

        holder.btnDislike.setOnClickListener(v -> {
            if (listener == null) return;

            int likeDelta = 0;
            int dislikeDelta = 0;

            if (!c.isDisliked()) {
                // none / like -> dislike
                c.setDisliked(true);
                c.setDislikeCount(c.getDislikeCount() + 1);
                dislikeDelta = 1;

                if (c.isLiked()) {
                    c.setLiked(false);
                    if (c.getLikeCount() > 0) {
                        c.setLikeCount(c.getLikeCount() - 1);
                        likeDelta = -1;
                    }
                }
            } else {
                // dislike -> none
                c.setDisliked(false);
                if (c.getDislikeCount() > 0) {
                    c.setDislikeCount(c.getDislikeCount() - 1);
                    dislikeDelta = -1;
                }
            }

            updateVoteUi(holder, c);
            listener.onVoteChanged(c, likeDelta, dislikeDelta);
        });

        holder.btnReply.setOnClickListener(v -> {
            if (listener != null) listener.onReplyClicked(c);
        });

        holder.tvToggleReplies.setOnClickListener(v -> {
            if (listener != null && !c.isReply()) {
                listener.onToggleReplies(c);
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentList == null ? 0 : commentList.size();
    }

    private int dp(int value) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (value * scale + 0.5f);
    }

    private void updateVoteUi(CommentViewHolder h, Comment c) {
        h.tvLike.setText(String.valueOf(c.getLikeCount()));
        h.tvDislike.setText(String.valueOf(c.getDislikeCount()));

        h.btnLike.setAlpha(c.isLiked() ? 1.0f : 0.4f);
        h.btnDislike.setAlpha(c.isDisliked() ? 1.0f : 0.4f);
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        LinearLayout rootItem;
        LinearLayout contentWrapper;
        View replyBar;
        ImageView imgAvatar;
        TextView tvName, tvDate, tvComment;
        RatingBar ratingBar;
        TextView tvLike, tvDislike, btnReply, tvToggleReplies;
        ImageButton btnLike, btnDislike;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            rootItem        = itemView.findViewById(R.id.rootItem);
            contentWrapper  = itemView.findViewById(R.id.contentWrapper);
            replyBar        = itemView.findViewById(R.id.replyBar);
            imgAvatar       = itemView.findViewById(R.id.imgAvatar);
            tvName          = itemView.findViewById(R.id.tvName);
            tvDate          = itemView.findViewById(R.id.tvDate);
            tvComment       = itemView.findViewById(R.id.tvComment);
            ratingBar       = itemView.findViewById(R.id.ratingBar);
            tvLike          = itemView.findViewById(R.id.tvLike);
            tvDislike       = itemView.findViewById(R.id.tvDislike);
            btnLike         = itemView.findViewById(R.id.btnLike);
            btnDislike      = itemView.findViewById(R.id.btnDislike);
            btnReply        = itemView.findViewById(R.id.btnReply);
            tvToggleReplies = itemView.findViewById(R.id.tvToggleReplies);
        }
    }
}
