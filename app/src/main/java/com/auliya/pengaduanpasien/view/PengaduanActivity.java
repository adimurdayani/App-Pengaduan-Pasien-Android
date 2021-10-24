package com.auliya.pengaduanpasien.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.auliya.pengaduanpasien.R;
import com.auliya.pengaduanpasien.api.URLServer;
import com.auliya.pengaduanpasien.model.PengaduanModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.auliya.pengaduanpasien.fcm.Notifikasi.CHANNEL_1_ID;


public class PengaduanActivity extends AppCompatActivity {

    private ImageView btn_kembali;
    private Button btn_kirim;
    private TextInputLayout l_judul, l_saran;
    private EditText e_judul, e_saran;
    public String saran, judul_saran, user_id, grup_id, nama;
    private ProgressDialog dialog;
    private StringRequest kirim;
    private ArrayList<PengaduanModel> dataPengaduan;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengaduan);
        init();
    }

    public void init() {
        preferences = getApplication().getSharedPreferences("user", Context.MODE_PRIVATE);
        btn_kembali = findViewById(R.id.btn_kembali);
        btn_kirim = findViewById(R.id.btn_kirim);
        l_judul = findViewById(R.id.l_judul_saran);
        l_saran = findViewById(R.id.l_saran);
        e_judul = findViewById(R.id.e_judul_saran);
        e_saran = findViewById(R.id.e_saran);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        user_id = String.valueOf(preferences.getInt("id_regis", 0));
        grup_id = preferences.getString("user_id", "");
        nama = preferences.getString("nama", "");

        btn_kembali.setOnClickListener(v -> {
            super.onBackPressed();
            finish();
        });

        btn_kirim.setOnClickListener(v -> {
            if (validasi()) {
                kirimData();

            }
        });
    }

    private void kirimData() {
        dataPengaduan = new ArrayList<>();
        dialog.setMessage("Loading...");
        dialog.show();

        kirim = new StringRequest(Request.Method.POST, URLServer.KIRIM_ADUAN, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    JSONObject data = object.getJSONObject("data");
                    PengaduanModel postPengaduan = new PengaduanModel();
                    postPengaduan.setUser_id(data.getInt("user_id"));
                    postPengaduan.setGrup_id(data.getInt("grup_id"));
                    postPengaduan.setSaran(data.getString("saran"));
                    postPengaduan.setJudul_saran(data.getString("judul_saran"));

                    HomeActivity.dataPengaduan.add(0, postPengaduan);
                    Objects.requireNonNull(HomeActivity.rc_data.getAdapter()).notifyItemInserted(0);
                    HomeActivity.rc_data.getAdapter().notifyDataSetChanged();

                    finish();

                    Toast.makeText(this, "Pengaduan terkirim!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Message: " + object.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "Message: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        }, error -> {
            dialog.dismiss();
            error.printStackTrace();
            Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
                HashMap<String, String> map = new HashMap<>();
                map.put("user_id", user_id);
                map.put("grup_id", grup_id);
                map.put("saran", saran);
                map.put("judul_saran", judul_saran);
                return map;
            }
        };
        RequestQueue koneksi = Volley.newRequestQueue(this);
        koneksi.add(kirim);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cekvalidasi();
    }

    public void gettextinput() {

        judul_saran = e_judul.getText().toString().trim();
        saran = e_saran.getText().toString().trim();
    }

    public void cekvalidasi() {

        gettextinput();


        e_judul.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (judul_saran.isEmpty()) {
                    l_judul.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        e_saran.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (saran.isEmpty()) {
                    l_saran.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    private boolean validasi() {
        gettextinput();

        if (judul_saran.isEmpty()) {
            l_judul.setErrorEnabled(true);
            l_judul.setError("Alamat tidak boleh kosong!");
            return false;
        }
        if (saran.isEmpty()) {
            l_saran.setErrorEnabled(true);
            l_saran.setError("Alamat tidak boleh kosong!");
            return false;
        }
        return true;
    }
}