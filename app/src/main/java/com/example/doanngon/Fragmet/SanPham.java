package com.example.doanngon.Fragmet;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.doanngon.Adapter.AdapterProduct;
import com.example.doanngon.Adapter.AdapterTimKiem;
import com.example.doanngon.Adapter.ItemAdapter;
import com.example.doanngon.DTO.Item;
import com.example.doanngon.DTO.MonAn;
import com.example.doanngon.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SanPham extends Fragment {
    private List<Item> productList;
    private RecyclerView gvProduct;
    private AdapterTimKiem productAdapter;
    private View view;
    private FirebaseFirestore db;
    public SanPham(List<Item> productList) {
        this.productList = productList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_san_pham, container, false);




        gvProduct = view.findViewById(R.id.gvProduct);
        gvProduct.setLayoutManager(new GridLayoutManager(getContext(), 1));
        productAdapter = new AdapterTimKiem(getContext(),productList);

        gvProduct.setAdapter(productAdapter);
        db = FirebaseFirestore.getInstance();
//        loadDataFromFirestore();
        return view;


    }
    private void loadDataFromFirestore() {
        db.collection("recipes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Item item = document.toObject(Item.class);
                                productList.add(item);

                            }
                            productAdapter.notifyDataSetChanged();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}