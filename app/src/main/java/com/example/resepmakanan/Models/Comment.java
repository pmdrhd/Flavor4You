package com.example.resepmakanan.Models;

public class Comment {

    private String id;
    private String recipeId;
    private String parentId;     // "0" = comment utama
    private String userName;
    private float rating;
    private String commentText;
    private String createdAt;
    private int likeCount;
    private int dislikeCount;

    // state di client (tidak disimpan di DB)
    private boolean liked;
    private boolean disliked;

    // untuk UI show/hide replies
    private int replyCount;
    private boolean repliesCollapsed = false;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRecipeId() { return recipeId; }
    public void setRecipeId(String recipeId) { this.recipeId = recipeId; }

    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }

    public boolean isReply() {
        return parentId != null && !parentId.equals("0");
    }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public String getCommentText() { return commentText; }
    public void setCommentText(String commentText) { this.commentText = commentText; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

    public int getDislikeCount() { return dislikeCount; }
    public void setDislikeCount(int dislikeCount) { this.dislikeCount = dislikeCount; }

    public boolean isLiked() { return liked; }
    public void setLiked(boolean liked) { this.liked = liked; }

    public boolean isDisliked() { return disliked; }
    public void setDisliked(boolean disliked) { this.disliked = disliked; }

    public int getReplyCount() { return replyCount; }
    public void setReplyCount(int replyCount) { this.replyCount = replyCount; }

    public boolean isRepliesCollapsed() { return repliesCollapsed; }
    public void setRepliesCollapsed(boolean repliesCollapsed) { this.repliesCollapsed = repliesCollapsed; }
}
