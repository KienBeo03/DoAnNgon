package com.example.doanngon;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.doanngon.DTO.Item;
import com.example.doanngon.Fragmet.SanPham;
import com.example.doanngon.Fragmet.TaiKhoan;
import com.example.doanngon.Fragmet.ThongBao;
import com.example.doanngon.Fragmet.TrangChu;
import com.example.doanngon.Fragmet.themmon;
import com.example.doanngon.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Toolbar mToolbar;

    EditText edtSeach;
    private List<Item> productList;
    private List<Item> productListSP;
    private FirebaseFirestore db;

    ActivityMainBinding binding;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        productList = new ArrayList<>();
        productListSP = new ArrayList<>();
        mToolbar = findViewById(R.id.tbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Hướng dẫn nấu ăn");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        edtSeach = findViewById(R.id.edtSeach);
        edtSeach.setVisibility(View.GONE);
        if (savedInstanceState == null) {
            replaceFragmet(new TrangChu());
        }
        binding.btomNav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.mnHome:
                    getSupportActionBar().setTitle("Hướng dẫn nấu ăn");
                    getSupportActionBar().setDisplayShowTitleEnabled(true);
                    edtSeach.setVisibility(View.GONE);
                    getSupportActionBar().show();
                    replaceFragmet(new TrangChu());
                    break;
                case R.id.mnSanPham:
                    filter("");
                    getSupportActionBar().setDisplayShowTitleEnabled(false);
                    edtSeach.setVisibility(View.VISIBLE);
                    getSupportActionBar().show();
                    replaceFragmet(new SanPham(productListSP));
                    break;
                case R.id.mnThem:
                    getSupportActionBar().hide();
                    replaceFragmet(new themmon());
                    break;
                case R.id.mnThongBao:
                    getSupportActionBar().hide();
                    replaceFragmet(new ThongBao());
                    break;

                case R.id.mnTaiKhoan:
                    getSupportActionBar().hide();
                    replaceFragmet(new TaiKhoan());
                    break;

            }

            return true;
        });

        edtSeach.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                filter(charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }




    private void filter(String text) {
        db.collection("recipes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        productListSP.clear();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Item item = document.toObject(Item.class);

                                if(productListSP == null){
                                    productListSP.add(item);
                                }
                                if (item.getTitle().toLowerCase().contains(text.toLowerCase())){
                                    productListSP.add(item);
                                }
                            }
                            replaceFragmet(new SanPham(productListSP));

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
    private void replaceFragmet(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frLayout,fragment);
        fragmentTransaction.commit();
    }
}
