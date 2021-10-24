package com.auliya.pengaduanpasien.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.auliya.pengaduanpasien.R;
import com.auliya.pengaduanpasien.api.URLServer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PengaturanActivity extends AppCompatActivity {

    private ImageView btn_kembali;
    private TextView txt_nama, txt_email, txt_no_hp, txt_kelamin;
    private SharedPreferences preferences;
    private LinearLayout btn_pengaturan, btn_bantuan, btn_tentang, btn_keluar;
    private Button btn_edit;

    private StringRequest prosesLogout;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengaturan);
        init();
    }

    public void init() {
        preferences = getApplication().getSharedPreferences("user", Context.MODE_PRIVATE);
        btn_kembali = findViewById(R.id.btn_kembali);
        txt_nama = findViewById(R.id.txt_nama);
        txt_email = findViewById(R.id.txt_email);
        txt_no_hp = findViewById(R.id.txt_no_telp);
        btn_pengaturan = findViewById(R.id.btn_pengaturan);
        btn_bantuan = findViewById(R.id.btn_bantuan);
        btn_tentang = findViewById(R.id.btn_tentang);
        btn_keluar = findViewById(R.id.btn_logout);
        btn_edit = findViewById(R.id.btn_edit);
        txt_kelamin = findViewById(R.id.txt_kelamin);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        txt_nama.setText(preferences.getString("nama", ""));
        txt_email.setText(preferences.getString("email", ""));
        txt_no_hp.setText(preferences.getString("no_hp", ""));
        txt_kelamin.setText(preferences.getString("kelamin", ""));

        btn_kembali.setOnClickListener(v -> {
            super.onBackPressed();
            finish();
        });

        btn_keluar.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Apakah anda yakin ingin keluar?");
            builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    logout();
                }
            });
            builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        });

        btn_pengaturan.setOnClickListener(v -> {
            startActivity(new Intent(this, UbahPasswordActivity.class));
        });

        btn_edit.setOnClickListener(v -> {
            startActivity(new Intent(this, UbahProfilActivity.class));
        });

        btn_bantuan.setOnClickListener(v -> {
            startActivity(new Intent(this, BantuanActivity.class));
        });

        btn_tentang.setOnClickListener(v -> {
            startActivity(new Intent(this, TentangActivity.class));
        });
    }

    private void logout() {
        dialog.setMessage("Loading...");
        dialog.show();
        prosesLogout = new StringRequest(Request.Method.GET, URLServer.LOGOUT, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.remove(preferences.getString("token", ""));
                    editor.remove(String.valueOf(preferences.getInt("id_regis", 0)));
                    editor.remove(preferences.getString("nama", ""));
                    editor.clear();
                    editor.apply();

                    Toast.makeText(this, object.getString("message"), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialog.dismiss();
        }, error -> {
            dialog.dismiss();
            error.printStackTrace();
            Toast.makeText(this, "Terjadi Masalah Koneksi", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = preferences.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(prosesLogout);
    }
}