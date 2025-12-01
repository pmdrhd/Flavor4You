package com.example.resepmakanan.Activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resepmakanan.AIChat.ApiClient;
import com.example.resepmakanan.AIChat.ChatAdapter;
import com.example.resepmakanan.AIChat.Message;
import com.example.resepmakanan.BuildConfig;
import com.example.resepmakanan.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AIChatActivity extends AppCompatActivity {

    private static final String MODEL = "llama-4-scout-17b-16e-instruct"; // ganti kalau perlu
    private static final int TYPE_SPEED_MS = 14;       // kecepatan “ngetik” (ms/char)

    private RecyclerView rv;
    private ChatAdapter adapter;
    private EditText edt;
    private ImageButton btn, btnReturn;

    // ==== AUTOSCROLL (tanpa FAB) ====
    private LinearLayoutManager layoutManager;
    private boolean autoScroll = true; // true kalau user dekat bottom
    // =================================

    private final ExecutorService io = Executors.newSingleThreadExecutor();
    private ApiClient api;
    private final List<ApiClient.ChatMessage> history = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aichat_page);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rv  = findViewById(R.id.rvChat);
        edt = findViewById(R.id.edtMessage);
        btn = findViewById(R.id.btnSend);

        btnReturn = findViewById(R.id.return_icon);
        btnReturn.setOnClickListener(view -> finish());

        adapter = new ChatAdapter();
        layoutManager = new LinearLayoutManager(this);
        // opsional: mulai dari bawah biar feel chat
        layoutManager.setStackFromEnd(true);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(adapter);

        if (savedInstanceState == null) {

            String welcome = "Halo, saya OLI — chatbot yang bisa bantu kamu mengenal lebih banyak resep. " +
                    "Tanya aja misal: \"cara masak ayam kecap\" atau \"resep nasi goreng\" 😉";
            adapter.add(new Message(welcome, Message.Sender.BOT));
            rv.scrollToPosition(adapter.getItemCount() - 1);
        }

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int last = layoutManager.findLastVisibleItemPosition();
                autoScroll = (last >= adapter.getItemCount() - 2);
            }
        });

        // Init API
        String baseUrl = BuildConfig.AI_BASE_URL;
        String apiKey  = BuildConfig.AI_API_KEY;
        api = new ApiClient(baseUrl, apiKey);

        // system prompt (optional)
        history.add(new ApiClient.ChatMessage("system",
                "You are OLI, a helpful cooking and recipe assistant. Answer briefly, step-by-step when needed."));

        btn.setOnClickListener(v -> onSend());
    }

    private void onSend() {
        final String userText = edt.getText().toString().trim();
        if (TextUtils.isEmpty(userText)) return;

        adapter.add(new Message(userText, Message.Sender.USER));
        if (autoScroll) smoothScrollToBottom();
        edt.setText("");

        history.add(new ApiClient.ChatMessage("user", userText));

        final Message botMsg = new Message("", Message.Sender.BOT);
        adapter.add(botMsg);
        final int botPos = adapter.getItemCount() - 1;
        if (autoScroll) smoothScrollToBottom();
        btn.setEnabled(false);

        io.execute(() -> {
            try {
                String reply = api.sendChat(MODEL, history);
                history.add(new ApiClient.ChatMessage("assistant", reply));

                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < reply.length(); i++) {
                    sb.append(reply.charAt(i));
                    final String partial = sb.toString();
                    runOnUiThread(() -> {
                        botMsg.setText(partial);
                        adapter.notifyItemChanged(botPos);
                    });
                    try { Thread.sleep(TYPE_SPEED_MS); } catch (InterruptedException ignored) {}
                }
            } catch (IOException e) {
                runOnUiThread(() -> {
                    botMsg.setText("[Network error: " + e.getMessage() + "]");
                    adapter.notifyItemChanged(botPos);
                });
            } finally {
                runOnUiThread(() -> {
                    if (autoScroll) smoothScrollToBottom();
                    btn.setEnabled(true);
                });
            }
        });
    }

    private void smoothScrollToBottom() {
        int lastPos = Math.max(0, adapter.getItemCount() - 1);
        rv.smoothScrollToPosition(lastPos);
    }
}
