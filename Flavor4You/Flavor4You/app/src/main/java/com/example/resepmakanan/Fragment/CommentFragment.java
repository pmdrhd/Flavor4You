package com.example.resepmakanan.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.resepmakanan.Adapters.CommentAdapter;
import com.example.resepmakanan.Managers.SessionManager;
import com.example.resepmakanan.Models.Comment;
import com.example.resepmakanan.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentFragment extends Fragment implements CommentAdapter.OnCommentActionListener {

    private RecyclerView rvComments;
    private CommentAdapter adapter;

    private List<Comment> commentList = new ArrayList<>();
    private List<Comment> allComments = new ArrayList<>();

    private LinearLayout llMain;
    private ProgressBar pbMain;
    private TextView tvErrorMessage;
    private EditText etComment;
    private ImageButton btnSend;
    private RatingBar rbUserRating;

    private int recipeId;          // sementara hardcode
    private String replyParentId = "0";     // 0 = comment utama

    private static final String BASE_URL = "http://10.0.2.2/recipe_api/comments/";
    private static final String URL_GET  = BASE_URL + "get_comments.php";
    private static final String URL_ADD  = BASE_URL + "add_comment.php";
    private static final String URL_VOTE = BASE_URL + "vote_comment.php";

    private RequestQueue queue;

    public static CommentFragment newInstance(int recipeId) {
        CommentFragment f = new CommentFragment();
        Bundle b = new Bundle();
        b.putInt("recipe_id", recipeId);
        f.setArguments(b);
        return f;
    }

    public CommentFragment() {
        // constructor kosong wajib
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_comment, container, false);

        if (getArguments() != null) {
            recipeId = getArguments().getInt("recipe_id", -1);
        }

        queue = Volley.newRequestQueue(requireContext());

        rvComments = view.findViewById(R.id.rvComments);
        etComment = view.findViewById(R.id.etComment);
        btnSend = view.findViewById(R.id.btnSend);
        rbUserRating = view.findViewById(R.id.rbUserRating);

        llMain = view.findViewById(R.id.main);
        pbMain = view.findViewById(R.id.commentProgressBar);
        tvErrorMessage = view.findViewById(R.id.tvErrorMessage);

        adapter = new CommentAdapter(requireContext(), commentList, this);
        rvComments.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvComments.setAdapter(adapter);

        loadComments();

        btnSend.setOnClickListener(v -> {
            String text = etComment.getText().toString().trim();
            float rating = rbUserRating.getRating();

            if ("0".equals(replyParentId) && rating <= 0) {
                Toast.makeText(requireContext(), "Pilih rating dulu ya.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (text.isEmpty()) {
                Toast.makeText(requireContext(), "Isi komentarnya dulu.", Toast.LENGTH_SHORT).show();
                return;
            }

            SessionManager sm = new SessionManager(requireContext());
            String username = sm.getUsername();
            int userId = sm.getId();

            addComment(userId, username, rating, text, replyParentId);
        });

        return view;
    }

    /* ========== LOAD COMMENTS ========== */

    private void loadComments() {
        String url = URL_GET + "?recipe_id=" + recipeId;

        pbMain.setVisibility(View.VISIBLE);
        llMain.setVisibility(View.GONE);
        tvErrorMessage.setVisibility(View.GONE);

        StringRequest req = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            JSONArray arr = obj.getJSONArray("data");
                            List<Comment> all = new ArrayList<>();

                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject c = arr.getJSONObject(i);
                                Comment cm = new Comment();
                                cm.setId(c.getString("id"));
                                cm.setRecipeId(c.getString("recipe_id"));
                                cm.setParentId(c.optString("parent_id", "0"));
                                cm.setUserName(c.getString("user_name"));
                                cm.setRating((float) c.getDouble("rating"));
                                cm.setCommentText(c.getString("comment_text"));
                                cm.setCreatedAt(c.getString("created_at"));
                                cm.setLikeCount(c.optInt("like_count", 0));
                                cm.setDislikeCount(c.optInt("dislike_count", 0));
                                all.add(cm);
                            }

                            allComments.clear();
                            allComments.addAll(all);

                            commentList = buildOrderedList(allComments);
                            adapter.setData(commentList);

                            pbMain.setVisibility(View.GONE);
                            llMain.setVisibility(View.VISIBLE);
                            tvErrorMessage.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Parse error", Toast.LENGTH_SHORT).show();

                        pbMain.setVisibility(View.GONE);
                        llMain.setVisibility(View.GONE);
                        tvErrorMessage.setVisibility(View.VISIBLE);
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(requireContext(), "Network error (GET)", Toast.LENGTH_SHORT).show();

                    pbMain.setVisibility(View.GONE);
                    llMain.setVisibility(View.GONE);
                    tvErrorMessage.setVisibility(View.VISIBLE);
                    tvErrorMessage.setText("Network Error (GET)");
                });

        queue.add(req);
    }

    /**
     * Urutkan:
     *  - comment utama (parent_id = 0) diurutkan:
     *      like_count desc, dislike_count asc, created_at desc
     *  - setiap reply muncul tepat di bawah parent (urut created_at asc)
     *  - kalau parent di-collapse, reply disembunyikan
     */
    private List<Comment> buildOrderedList(List<Comment> all) {
        List<Comment> top = new ArrayList<>();
        Map<String, List<Comment>> replies = new HashMap<>();

        // pisahkan parent & reply
        for (Comment c : all) {
            if (c.isReply()) {
                List<Comment> list = replies.getOrDefault(c.getParentId(), new ArrayList<>());
                list.add(c);
                replies.put(c.getParentId(), list);
            } else {
                top.add(c);
            }
        }

        // hitung jumlah reply tiap parent
        for (Comment parent : top) {
            List<Comment> childList = replies.get(parent.getId());
            parent.setReplyCount(childList == null ? 0 : childList.size());
        }

        // sort parent: like desc, dislike asc, created_at desc
        Collections.sort(top, (c1, c2) -> {
            int likeDiff = Integer.compare(c2.getLikeCount(), c1.getLikeCount());
            if (likeDiff != 0) return likeDiff;

            int dislikeDiff = Integer.compare(c1.getDislikeCount(), c2.getDislikeCount());
            if (dislikeDiff != 0) return dislikeDiff;

            return c2.getCreatedAt().compareTo(c1.getCreatedAt());
        });

        // sort replies by created_at asc
        for (List<Comment> list : replies.values()) {       
            Collections.sort(list, Comparator.comparing(Comment::getCreatedAt));
        }

        // flatten ke list final
        List<Comment> ordered = new ArrayList<>();
        for (Comment parent : top) {
            ordered.add(parent);

            List<Comment> childList = replies.get(parent.getId());
            if (childList != null && !parent.isRepliesCollapsed()) {
                ordered.addAll(childList);
            }
        }

        return ordered;
    }

    /* ========== ADD COMMENT / REPLY ========== */

    private void addComment(int userId, String name, float rating, String text, String parentId) {
        StringRequest req = new StringRequest(Request.Method.POST, URL_ADD,
                response -> {
                    etComment.setText("");
                    if ("0".equals(parentId)) {
                        rbUserRating.setRating(0f);
                    }
                    replyParentId = "0";
                    etComment.setHint("Write your comment");
                    loadComments();
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(requireContext(), "Gagal kirim comment", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("recipe_id", String.valueOf(recipeId));
                params.put("parent_id", parentId);
                params.put("user_id", String.valueOf(userId));   // ⬅ penting
                params.put("user_name", name);                  // ⬅ penting
                params.put("rating", String.valueOf(rating));
                params.put("comment_text", text);
                return params;
            }
        };

        queue.add(req);
    }

    /* ========== VOTE (LIKE / DISLIKE) ========== */

    private void sendVote(Comment comment, int deltaLike, int deltaDislike) {
        if (deltaLike == 0 && deltaDislike == 0) return;

        StringRequest req = new StringRequest(Request.Method.POST, URL_VOTE,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        boolean ok = obj.optBoolean("success", false);

                        if (ok) {
                            // sukses di server → resort list berdasar like/dislike terbaru
                            commentList = buildOrderedList(allComments);
                            adapter.setData(commentList);
                        } else {
                            rollbackVote(comment, deltaLike, deltaDislike);
                            Toast.makeText(requireContext(),
                                    "Gagal update vote: " + obj.optString("message", ""),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        rollbackVote(comment, deltaLike, deltaDislike);
                        Toast.makeText(requireContext(), "Error parse vote", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    rollbackVote(comment, deltaLike, deltaDislike);
                    Toast.makeText(requireContext(), "Gagal update vote (network)", Toast.LENGTH_SHORT).show();
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> p = new HashMap<>();
                p.put("comment_id", comment.getId());
                p.put("delta_like", String.valueOf(deltaLike));
                p.put("delta_dislike", String.valueOf(deltaDislike));
                return p;
            }
        };

        queue.add(req);
    }

    private void rollbackVote(Comment c, int deltaLike, int deltaDislike) {
        if (deltaLike != 0) {
            c.setLikeCount(Math.max(0, c.getLikeCount() - deltaLike));
            c.setLiked(!c.isLiked());
        }
        if (deltaDislike != 0) {
            c.setDislikeCount(Math.max(0, c.getDislikeCount() - deltaDislike));
            c.setDisliked(!c.isDisliked());
        }
        int idx = commentList.indexOf(c);
        if (idx >= 0) adapter.notifyItemChanged(idx);
    }

    /* ========== CALLBACK DARI ADAPTER ========== */

    @Override
    public void onVoteChanged(Comment comment, int likeDelta, int dislikeDelta) {
        // update list master (allComments) juga
        int idx = allComments.indexOf(comment);
        if (idx >= 0) {
            allComments.set(idx, comment);
        }
        sendVote(comment, likeDelta, dislikeDelta);
    }

    @Override
    public void onReplyClicked(Comment comment) {
        replyParentId = comment.getId();
        etComment.requestFocus();
        etComment.setHint("Reply to " + comment.getUserName());
        rbUserRating.setRating(0f); // reply tidak perlu rating
    }

    @Override
    public void onToggleReplies(Comment parent) {
        parent.setRepliesCollapsed(!parent.isRepliesCollapsed());
        commentList = buildOrderedList(allComments);
        adapter.setData(commentList);
    }
}
