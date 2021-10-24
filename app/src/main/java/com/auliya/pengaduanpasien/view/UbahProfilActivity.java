package com.auliya.pengaduanpasien.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.auliya.pengaduanpasien.R;
import com.auliya.pengaduanpasien.api.URLServer;
import com.auliya.pengaduanpasien.model.UserModel;
import com.auliya.pengaduanpasien.view.fragment.FragmentLogin;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UbahProfilActivity extends AppCompatActivity {

    private ImageView btn_kembali;
    private Button btn_update;
    private TextInputLayout l_nama, l_email, l_alamat, l_nohp;
    private EditText e_nama, e_email, e_alamat, e_nohp;
    private String nama, email, alamat, nohp, kelamin;
    private TextView txt_kelamin;
    private Spinner sp_kelamin;
    private ProgressDialog dialog;
    private StringRequest updateUser;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    public String id_regis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_profil);
        init();
    }

    public void init() {
        preferences = getApplication().getSharedPreferences("user", Context.MODE_PRIVATE);
        btn_kembali = findViewById(R.id.btn_kembali);
        btn_update = findViewById(R.id.btn_update);
        l_nama = findViewById(R.id.l_nama);
        l_email = findViewById(R.id.l_email);
        l_alamat = findViewById(R.id.l_alamat);
        l_nohp = findViewById(R.id.l_no_telp);
        e_nama = findViewById(R.id.e_nama);
        e_email = findViewById(R.id.e_email);
        e_alamat = findViewById(R.id.e_alamat);
        e_nohp = findViewById(R.id.e_no_telp);
        txt_kelamin = findViewById(R.id.getkelamin);
        sp_kelamin = findViewById(R.id.kelamin);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        e_nama.setText(preferences.getString("nama", ""));
        e_email.setText(preferences.getString("email", ""));
        id_regis = String.valueOf(preferences.getInt("id_regis", 0));

        btn_kembali.setOnClickListener(v -> {
            startActivity(new Intent(this, PengaturanActivity.class));
            finish();
        });

        btn_update.setOnClickListener(v -> {
            if (validasi()) {
                updateProfil();
            }
        });

        sp_kelamin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                txt_kelamin.setText(sp_kelamin.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        cekvalidasi();
    }

    private void updateProfil() {
        dialog.setMessage("Menyimpan...");
        dialog.show();

        updateUser = new StringRequest(Request.Method.POST, URLServer.UPDATE_PROFILE, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    JSONObject data = object.getJSONObject("data");
                    editor = preferences.edit();
                    editor.putString("no_hp", data.getString("no_hp"));
                    editor.putString("kelamin", data.getString("kelamin"));
                    editor.putString("alamat", data.getString("alamat"));
                    editor.apply();
                    Toast.makeText(this, "Data terimpan!", Toast.LENGTH_LONG).show();
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
                map.put("id_regis", id_regis);
                map.put("nama", nama);
                map.put("email", email);
                map.put("no_hp", nohp);
                map.put("kelamin", kelamin);
                map.put("alamat", alamat);
                return map;
            }
        };
        RequestQueue koneksi = Volley.newRequestQueue(this);
        koneksi.add(updateUser);
    }

    public void cekvalidasi() {
        getinputtext();

        e_nama.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (nama.isEmpty()) {
                    l_nama.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        e_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (email.isEmpty()) {
                    l_email.setErrorEnabled(false);
                } else if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    l_email.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        e_nohp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (nohp.isEmpty()) {
                    l_nohp.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        e_alamat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (alamat.isEmpty()) {
                    l_alamat.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private boolean validasi() {
        getinputtext();
        if (nama.isEmpty()) {
            l_nama.setErrorEnabled(true);
            l_nama.setError("Kolom nama tidak boleh kosong!");
            return false;
        }
        if (email.isEmpty()) {
            l_email.setErrorEnabled(true);
            l_email.setError("Kolom email tidak boleh kosong!");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            l_email.setErrorEnabled(true);
            l_email.setError("Format email salah!. Contoh: gunakan @example.com");
            return false;
        }
        if (nohp.isEmpty()) {
            l_nohp.setErrorEnabled(true);
            l_nohp.setError("Kolom no. telp tidak boleh kosong!");
            return false;
        }
        if (alamat.isEmpty()) {
            l_alamat.setErrorEnabled(true);
            l_alamat.setError("Kolom alamat tidak boleh kosong!");
            return false;
        }

        return true;
    }

    public void getinputtext() {
        nama = e_nama.getText().toString().trim();
        email = e_email.getText().toString().trim();
        nohp = e_nohp.getText().toString().trim();
        alamat = e_alamat.getText().toString().trim();
        kelamin = txt_kelamin.getText().toString().trim();
    }
}