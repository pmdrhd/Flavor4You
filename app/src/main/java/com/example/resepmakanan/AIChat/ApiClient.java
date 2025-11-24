package com.example.resepmakanan.AIChat;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiClient {

    public static class ChatRequest {
        @SerializedName("model") public String model;
        @SerializedName("messages") public List<ChatMessage> messages = new ArrayList<>();
        @SerializedName("temperature") public Float temperature;
    }

    public static class ChatMessage {
        @SerializedName("role") public String role;
        @SerializedName("content") public String content;
        public ChatMessage(String role, String content) {
            this.role = role; this.content = content;
        }
    }

    public static class ChatResponse {
        public static class Choice {
            public MessageObj message;
        }
        public static class MessageObj {
            public String role;
            public String content;
        }
        public List<Choice> choices;
    }

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();
    private final String baseUrl;
    private final String apiKey;

    public ApiClient(String baseUrl, String apiKey) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    public String sendChat(String model, List<ChatMessage> history) throws IOException {
        ChatRequest req = new ChatRequest();
        req.model = model;
        req.messages = history;
        req.temperature = 0.7f;

        String json = gson.toJson(req);
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        Request http = new Request.Builder()
                .url(baseUrl + "/v1/chat/completions")
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response resp = client.newCall(http).execute()) {
            if (!resp.isSuccessful()) {
                String err = resp.body() != null ? resp.body().string() : "";
                throw new IOException("HTTP " + resp.code() + " - " + err);
            }
            String respBody = resp.body() != null ? resp.body().string() : "";
            ChatResponse cr = gson.fromJson(respBody, ChatResponse.class);
            if (cr != null && cr.choices != null && !cr.choices.isEmpty() && cr.choices.get(0).message != null) {
                return cr.choices.get(0).message.content;
            }
            return "(empty response)";
        }
    }
}
