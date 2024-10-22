package com.example.doanngon.Fragmet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanngon.Adapter.ItemAdapter;
import com.example.doanngon.DTO.Item;
import com.example.doanngon.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class themmon extends Fragment {
    private static final String TAG = "";
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private List<Item> itemList;
    View view;
    Button btnThemMon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_themmon, container, false);
        btnThemMon = view.findViewById(R.id.btnThemMon);
        btnThemMon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment();
            }
        });
        recyclerView = view.findViewById(R.id.rvListMonAn);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        itemList = new ArrayList<>();
        itemAdapter = new ItemAdapter(getContext(), itemList);
        recyclerView.setAdapter(itemAdapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("recipes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Item item = document.toObject(Item.class);
                                itemList.add(item);

                            }
                            itemAdapter.notifyDataSetChanged();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
        return view;
    }


    private void openFragment() {
        ct_themmon fCtThemmon = new ct_themmon();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("dataKey", "them");
        fCtThemmon.setArguments(bundle);
        transaction.replace(R.id.frLayout, fCtThemmon);

        transaction.addToBackStack(null);  // Thêm Fragment vào back stack để có thể quay lại
        transaction.commit();
    }


}