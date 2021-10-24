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
import com.auliya.pengaduanpasien.model.RekamMedikModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DetailRekamMedik extends AppCompatActivity {
    private ImageView btn_kembali;
    private TextView txt_norm, txt_nojk, txt_diagnosa, txt_jenisobat, txt_tgllahir;
    public String id_rekam;
    private SharedPreferences preferences;
    private StringRequest getIdRekamMedik;
    private SwipeRefreshLayout sw_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_rekam_medik);
        init();
    }

    public void init() {
        preferences = getApplication().getSharedPreferences("user", Context.MODE_PRIVATE);
        btn_kembali = findViewById(R.id.btn_kembali);
        txt_diagnosa = findViewById(R.id.diagnosa);
        txt_jenisobat = findViewById(R.id.jenis_obat);
        txt_nojk = findViewById(R.id.no_jk);
        txt_norm = findViewById(R.id.no_rm);
        txt_tgllahir = findViewById(R.id.tgl_lahir);
        sw_data = findViewById(R.id.sw_data);

        id_rekam = getIntent().getStringExtra("id_rekam");

        sw_data.setOnRefreshListener(() -> {
            setGetIdRekamMedik();
        });

        btn_kembali.setOnClickListener(v -> {
            super.onBackPressed();
            finish();
        });
    }

    public void setGetIdRekamMedik() {
        sw_data.setRefreshing(true);
        getIdRekamMedik = new StringRequest(Request.Method.GET, URLServer.GETREKAMMEDIKID + id_rekam, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    JSONObject data = object.getJSONObject("data");
                    txt_tgllahir.setText(data.getString("tgl_lahir"));
                    txt_diagnosa.setText(data.getString("diagnosa"));
                    txt_norm.setText(data.getString("no_rm"));
                    txt_nojk.setText(data.getString("no_jaminan"));
                    txt_jenisobat.setText(data.getString("jenis_obat"));
//                    txt_tanggal.setText(data.getString("created_at"));
                } else {
                    Toast.makeText(this, object.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            sw_data.setRefreshing(false);
        }, error -> {
            error.printStackTrace();
            sw_data.setRefreshing(false);
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
        koneksi.add(getIdRekamMedik);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setGetIdRekamMedik();
    }
}