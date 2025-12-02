package com.example.resepmakanan.AIChat;

public class Message {
    public enum Sender { USER, BOT }

    private String text;
    private final Sender sender;

    public Message(String text, Sender sender) {
        this.text = text;
        this.sender = sender;
    }

    public String getText()   { return text; }
    public Sender getSender() { return sender; }
    public void setText(String t) { this.text = t; }
}
