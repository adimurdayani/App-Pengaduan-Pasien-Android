package com.auliya.pengaduanpasien.api;

public class URLServer {
    public static final String BASE_URL = "http://10.0.2.2/rekdikpepas/api/";
    public static final String LOGIN = BASE_URL + "auth/login";
    public static final String LOGOUT = BASE_URL + "auth/logout";
    public static final String REGISTER = BASE_URL + "auth/register";
    public static final String UPDATE_PROFILE = BASE_URL + "user/profil";
    public static final String UPDATE_PASSWORD = BASE_URL + "user/password";
    public static final String KIRIM_ADUAN = BASE_URL + "pengaduan/kirim";
    public static final String GETPENGADUAN = BASE_URL + "pengaduan";
    public static final String GETPENGADUANID = BASE_URL + "pengaduan?id=";
    public static final String GETREKAMMEDIK = BASE_URL + "rekammedik";
    public static final String GETREKAMMEDIKID = BASE_URL + "rekammedik?id_rekam=";
}
