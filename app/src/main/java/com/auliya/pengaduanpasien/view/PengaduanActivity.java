package com.auliya.pengaduanpasien.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
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
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.auliya.pengaduanpasien.R;
import com.auliya.pengaduanpasien.api.URLServer;
import com.auliya.pengaduanpasien.model.PengaduanModel;
import com.auliya.pengaduanpasien.presentasi.PengaduanAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.auliya.pengaduanpasien.fcm.Notifikasi.CHANNEL_1_ID;


public class PengaduanActivity extends AppCompatActivity {

    private ImageView btn_kembali;
    private Button btn_kirim;
    private TextInputLayout  l_saran;
    private EditText  e_saran;
    public String saran, judul_saran, user_id, grup_id, nama;
    private ProgressDialog dialog;
    private StringRequest kirim;
    private ArrayList<PengaduanModel> dataPengaduan;
    private SharedPreferences preferences;
    private SwipeRefreshLayout sw_data;
    public static RecyclerView rc_data = null;
    private RecyclerView.LayoutManager layoutManager;
    private StringRequest getPengaduan;
    private PengaduanAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengaduan);
        init();
        setButton();
        setGetPengaduan();
    }

    private void setButton() {
        sw_data.setOnRefreshListener(() -> {
            setGetPengaduan();
        });
    }

    public void init() {
        preferences = getApplication().getSharedPreferences("user", Context.MODE_PRIVATE);
        btn_kembali = findViewById(R.id.btn_kembali);
        btn_kirim = findViewById(R.id.btn_kirim);
        l_saran = findViewById(R.id.l_saran);
        e_saran = findViewById(R.id.e_saran);
        rc_data = findViewById(R.id.rc_data);
        sw_data = findViewById(R.id.sw_data);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        user_id = String.valueOf(preferences.getInt("id_regis", 0));
        grup_id = preferences.getString("user_id", "");
        nama = preferences.getString("nama", "");

        btn_kembali.setOnClickListener(v -> {
            onBackPressed();
        });

        btn_kirim.setOnClickListener(v -> {
            if (validasi()) {
                kirimData();
            }
        });

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rc_data.setLayoutManager(layoutManager);
        rc_data.setHasFixedSize(true);
    }

    private void kirimData() {
        dataPengaduan = new ArrayList<>();
        dialog.setMessage("Loading...");
        dialog.show();

        kirim = new StringRequest(Request.Method.POST, URLServer.KIRIM_ADUAN, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    showDialog();
                } else {
                    showError(object.getString("message"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                showError(e.toString());
            }
            dialog.dismiss();
        }, error -> {
            dialog.dismiss();
            error.printStackTrace();
            showError(error.toString());
        }) {
            @NonNull
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("user_id", user_id);
                map.put("grup_id", grup_id);
                map.put("saran", saran);
                return map;
            }
        };
        kirim.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 2000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 2000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                if (Looper.myLooper() == null) {
                    dialog.dismiss();
                    Looper.prepare();
                    String pesan = "Koneksi gagal!";
                    showError(pesan);
                }
            }
        });
        RequestQueue koneksi = Volley.newRequestQueue(this);
        koneksi.add(kirim);
    }

    public void setGetPengaduan() {
        dataPengaduan = new ArrayList<>();
        sw_data.setRefreshing(true);
        int id = preferences.getInt("id_regis", 0);
        getPengaduan = new StringRequest(Request.Method.GET, URLServer.GETPENGADUAN + id, response -> {
            if (response != null) {
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getBoolean("status")) {
                        JSONArray data = new JSONArray(object.getString("data"));
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject getData = data.getJSONObject(i);
                            PengaduanModel getDataPengaduan = new PengaduanModel();
                            getDataPengaduan.setId(getData.getInt("id"));
                            getDataPengaduan.setUser_id(getData.getInt("user_id"));
                            getDataPengaduan.setNama(getData.getString("nama"));
                            getDataPengaduan.setGrup_id(getData.getInt("grup_id"));
                            getDataPengaduan.setSaran(getData.getString("saran"));
                            getDataPengaduan.setCreated_at(getData.getString("created_at"));
                            dataPengaduan.add(getDataPengaduan);
                        }
                        adapter = new PengaduanAdapter(this, dataPengaduan);
                        rc_data.setAdapter(adapter);
                    } else {
                        showError(object.getString("message"));
                    }
                } catch (JSONException e) {
                    showError(e.toString());
                }
            } else {
                showError(null);
            }
            sw_data.setRefreshing(false);
        }, error -> {
            sw_data.setRefreshing(false);
            Log.d("respon", "err: " + error.networkResponse);
        });
        getPengaduan.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 2000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 2000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                if (Looper.myLooper() == null) {
                    Looper.prepare();
                    showError("Koneksi gagal!");

                }
            }
        });
        RequestQueue koneksi = Volley.newRequestQueue(this);
        koneksi.add(getPengaduan);
    }

    private void showDialog() {
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Sukses!")
                .setConfirmText("Oke")
                .setConfirmClickListener(sweetAlertDialog -> {
                    startActivity(new Intent(this, PengaduanActivity.class));
                    finish();
                    sweetAlertDialog.dismissWithAnimation();
                })
                .show();
    }

    private void showError(String string) {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(string)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void gettextinput() {
        saran = e_saran.getText().toString().trim();
    }

    private boolean validasi() {
        gettextinput();
        if (saran.isEmpty()) {
            l_saran.setError("Isi aduan tidak boleh kosong!");
            return true;
        }
        return true;
    }
}