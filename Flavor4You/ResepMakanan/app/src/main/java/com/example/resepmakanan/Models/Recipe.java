package com.example.resepmakanan.Models;

import androidx.annotation.NonNull;

public class Recipe {
    private int id;
    private String namaResep;
    private String gambar;      // <— ini yang benar
    private String bahan;
    private String instruksi;
    private String porsi;
    private String durasi;

    private float avgRating;
    private int totalComments;

    // untuk favorit
    private boolean isFavorite;

    // --- getter & setter ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNamaResep() {
        return namaResep;
    }

    public void setNamaResep(String namaResep) {
        this.namaResep = namaResep;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getBahan() {
        return bahan;
    }

    public void setBahan(String bahan) {
        this.bahan = bahan;
    }

    public String getInstruksi() {
        return instruksi;
    }

    public void setInstruksi(String instruksi) {
        this.instruksi = instruksi;
    }

    public String getPorsi() {
        return porsi;
    }

    public void setPorsi(String porsi) {
        this.porsi = porsi;
    }

    public String getDurasi() {
        return durasi;
    }

    public void setDurasi(String durasi) {
        this.durasi = durasi;
    }

    public float getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(float avgRating) {
        this.avgRating = avgRating;
    }

    public int getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(int totalComments) {
        this.totalComments = totalComments;
    }
    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) {
        this.isFavorite = favorite;
    }
}