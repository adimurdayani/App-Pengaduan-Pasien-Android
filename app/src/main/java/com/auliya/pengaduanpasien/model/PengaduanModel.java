package com.auliya.pengaduanpasien.model;

public class PengaduanModel {
    private int id, user_id, grup_id;
    private String saran, judul_saran, jawaban_saran, created_at;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getGrup_id() {
        return grup_id;
    }

    public void setGrup_id(int grup_id) {
        this.grup_id = grup_id;
    }

    public String getSaran() {
        return saran;
    }

    public void setSaran(String saran) {
        this.saran = saran;
    }

    public String getJudul_saran() {
        return judul_saran;
    }

    public void setJudul_saran(String judul_saran) {
        this.judul_saran = judul_saran;
    }

    public String getJawaban_saran() {
        return jawaban_saran;
    }

    public void setJawaban_saran(String jawaban_saran) {
        this.jawaban_saran = jawaban_saran;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
