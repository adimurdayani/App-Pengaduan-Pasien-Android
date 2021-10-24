package com.auliya.pengaduanpasien.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.auliya.pengaduanpasien.R;
import com.auliya.pengaduanpasien.api.URLServer;
import com.auliya.pengaduanpasien.model.RekamMedikModel;
import com.auliya.pengaduanpasien.presentasi.RekamMedikAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RekamMedikActivity extends AppCompatActivity {

    private ImageView btn_kembali;
    private SharedPreferences preferences;
    private SwipeRefreshLayout sw_data;
    public static RecyclerView rc_data;
    private RecyclerView.LayoutManager layoutManager;
    private StringRequest getRekammedik;
    public static ArrayList<RekamMedikModel> dataRekammedik;
    private RekamMedikAdapter adapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rekam_medik);
        init();
    }
    private void init(){
        preferences = getApplication().getSharedPreferences("user", Context.MODE_PRIVATE);
        btn_kembali = findViewById(R.id.btn_kembali);
        rc_data = findViewById(R.id.list_data_rekam);
        sw_data = findViewById(R.id.refresh_data);
        searchView = findViewById(R.id.serach_data);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rc_data.setLayoutManager(layoutManager);
        rc_data.setHasFixedSize(true);

        sw_data.setOnRefreshListener(() -> {
            setGetRekammedik();
        });

        btn_kembali.setOnClickListener(v -> {
            super.onBackPressed();
            finish();
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getSearchData().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
    public void setGetRekammedik(){
        dataRekammedik = new ArrayList<>();
        sw_data.setRefreshing(true);

        getRekammedik = new StringRequest(Request.Method.GET, URLServer.GETREKAMMEDIK, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    JSONArray data = new JSONArray(object.getString("data"));
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject getData = data.getJSONObject(i);
                        RekamMedikModel getRekammedik = new RekamMedikModel();
                        getRekammedik.setId_rekam(getData.getInt("id_rekam"));
                        getRekammedik.setNo_rm(getData.getString("no_rm"));
                        getRekammedik.setNo_jaminan(getData.getString("no_jaminan"));
                        getRekammedik.setDiagnosa(getData.getString("diagnosa"));
                        dataRekammedik.add(getRekammedik);

                    }
                    adapter = new RekamMedikAdapter(this, dataRekammedik);
                    rc_data.setAdapter(adapter);
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
        koneksi.add(getRekammedik);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setGetRekammedik();
    }
}