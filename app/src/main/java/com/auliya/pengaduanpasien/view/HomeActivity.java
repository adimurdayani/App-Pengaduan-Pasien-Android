package com.auliya.pengaduanpasien.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressLint( "MissingPermission" )
public class HomeActivity extends AppCompatActivity {

    private CardView btn_pengaduan, btn_informasi, btn_rm, btn_pengaturan;
    private TextView txt_nama;
    private SharedPreferences preferences;
    private SwipeRefreshLayout sw_data;
    public static RecyclerView rc_data;
    private RecyclerView.LayoutManager layoutManager;
    private StringRequest getPengaduan;
    public static ArrayList<PengaduanModel> dataPengaduan;
    private PengaduanAdapter adapter;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
    }

    public void init() {
        preferences = getApplication().getSharedPreferences("user", Context.MODE_PRIVATE);
        btn_informasi = findViewById(R.id.btn_informasi);
        btn_pengaduan = findViewById(R.id.btn_pengaduan);
        btn_rm = findViewById(R.id.btn_rm);
        btn_pengaturan = findViewById(R.id.btn_pengaturan);
        txt_nama = findViewById(R.id.txt_nama);
        rc_data = findViewById(R.id.list_data_pengaduan);
        sw_data = findViewById(R.id.refresh_data);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rc_data.setLayoutManager(layoutManager);
        rc_data.setHasFixedSize(true);

        txt_nama.setText(preferences.getString("nama", ""));

        sw_data.setOnRefreshListener(() -> {
            setGetPengaduan();
        });

        btn_informasi.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), InformasiActivity.class));
        });

        btn_pengaduan.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), PengaduanActivity.class));
        });

        btn_pengaturan.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), PengaturanActivity.class));
        });

        btn_rm.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), RekamMedikActivity.class));
        });

    }

    public void setGetPengaduan() {
        dataPengaduan = new ArrayList<>();
        sw_data.setRefreshing(true);

        getPengaduan = new StringRequest(Request.Method.GET, URLServer.GETPENGADUAN, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    JSONArray data = new JSONArray(object.getString("data"));
                    for (int i = 0; i < data.length(); i++) {

                        JSONObject getData = data.getJSONObject(i);

                        PengaduanModel getDataPengaduan = new PengaduanModel();
                        getDataPengaduan.setId(getData.getInt("id"));
                        getDataPengaduan.setUser_id(getData.getInt("user_id"));
                        getDataPengaduan.setGrup_id(getData.getInt("grup_id"));
                        getDataPengaduan.setSaran(getData.getString("saran"));
                        getDataPengaduan.setJudul_saran(getData.getString("judul_saran"));
                        getDataPengaduan.setCreated_at(getData.getString("created_at"));
                        dataPengaduan.add(getDataPengaduan);

                    }
                    adapter = new PengaduanAdapter(this, dataPengaduan);
                    rc_data.setAdapter(adapter);
                } else {
                    Toast.makeText(this, object.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            sw_data.setRefreshing(false);
        }, error -> {
            sw_data.setRefreshing(false);
            error.printStackTrace();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.remove(preferences.getString("token", ""));
                    editor.remove(String.valueOf(preferences.getInt("id_regis", 0)));
                    editor.remove(preferences.getString("nama", ""));
                    editor.clear();
                    editor.apply();

                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }, 60000);
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = preferences.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String generate_token = preferences.getString("token_id", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("token_generate", generate_token);
                return map;
            }
        };
        RequestQueue koneksi = Volley.newRequestQueue(this);
        koneksi.add(getPengaduan);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setGetPengaduan();
    }

}