package com.auliya.pengaduanpasien.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.auliya.pengaduanpasien.R;
import com.auliya.pengaduanpasien.api.URLServer;
import com.auliya.pengaduanpasien.model.PengaduanModel;
import com.auliya.pengaduanpasien.presentasi.PengaduanAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DetailPengaduan extends AppCompatActivity {

    private ImageView btn_kembali;
    private String id_pengaduan;
    private TextView txt_judulsaran, txt_saran, txt_email, txt_nama, txt_alamat, txt_tanggal,txt_judulsaran2, txt_balasan, txt_tanggal2;
    private SharedPreferences preferences;
    private StringRequest getIdPengaduan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pengaduan);
        init();
    }

    public void init() {
        preferences = getApplication().getSharedPreferences("user", Context.MODE_PRIVATE);
        btn_kembali = findViewById(R.id.btn_kembali);
        txt_alamat = findViewById(R.id.alamat);
        txt_nama = findViewById(R.id.nama);
        txt_email = findViewById(R.id.email);
        txt_judulsaran = findViewById(R.id.judul_saran);
        txt_saran = findViewById(R.id.saran);
        txt_tanggal = findViewById(R.id.tanggal);
        txt_judulsaran2 = findViewById(R.id.judul_saran2);
        txt_balasan = findViewById(R.id.jawaban);
        txt_tanggal2 = findViewById(R.id.tanggal2);

        id_pengaduan = getIntent().getStringExtra("id_pengaduan");

        btn_kembali.setOnClickListener(v -> {
            super.onBackPressed();
            finish();
        });

    }

    public void setGetIdPengaduan() {
        getIdPengaduan = new StringRequest(Request.Method.GET, URLServer.GETPENGADUANID + id_pengaduan, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    JSONObject data = object.getJSONObject("data");
                    txt_nama.setText(data.getString("nama"));
                    txt_email.setText(data.getString("email"));
                    txt_alamat.setText(data.getString("alamat"));
                    txt_judulsaran.setText(data.getString("judul_saran"));
                    txt_saran.setText(data.getString("saran"));
                    txt_tanggal.setText(data.getString("created_at"));

                    txt_judulsaran2.setText(data.getString("judul_saran"));
                    txt_balasan.setText(data.getString("jawaban_saran"));
                    txt_tanggal2.setText(data.getString("created_at"));
                } else {
                    Toast.makeText(this, object.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            error.printStackTrace();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = preferences.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }

        };
        RequestQueue koneksi = Volley.newRequestQueue(this);
        koneksi.add(getIdPengaduan);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setGetIdPengaduan();
    }
}