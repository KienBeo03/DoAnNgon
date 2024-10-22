package com.example.doanngon.Fragmet;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanngon.Adapter.AdapterYeuThich;
import com.example.doanngon.DTO.Item;
import com.example.doanngon.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class YeuThich extends Fragment {
    private View view;
    private RecyclerView gvYeuThich;
    private List<Item> productList;
    private AdapterYeuThich adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_yeu_thich, container, false);

        initViews();
        setupRecyclerView();
        loadFavorites();

        return view;
    }

    private void initViews() {
        gvYeuThich = view.findViewById(R.id.gvYeuThich);
    }

    private void setupRecyclerView() {
        gvYeuThich.setLayoutManager(new LinearLayoutManager(getContext()));
        gvYeuThich.setHasFixedSize(true);

        productList = new ArrayList<>();
        adapter = new AdapterYeuThich(getContext(), productList);
        gvYeuThich.setAdapter(adapter);
    }

    private void loadFavorites() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference favoritesRef = db.collection("Users").document(userId).collection("favorite");

            favoritesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        List<String> favoriteIds = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            String productId = document.getString("id");
                            if (productId != null) {
                                favoriteIds.add(productId);
                                Log.e("du lieu", favoriteIds +"");
                            }
                        }
                        // Call method to fetch details of favorite items using favoriteIds
                        fetchFavoriteItems(favoriteIds);
                    } else {
                        Log.w("Favorite", "Error getting documents.", task.getException());
                    }
                }
            });
        }
    }

    private void fetchFavoriteItems(List<String> favoriteIds) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference productsRef = db.collection("recipes");

        Tasks.whenAllComplete(fetchProductTasks(productsRef, favoriteIds)).addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
            @Override
            public void onComplete(@NonNull Task<List<Task<?>>> task) {
                if (task.isSuccessful()) {
                    for (Task<?> productTask : task.getResult()) {
                        if (productTask.isSuccessful()) {
                            DocumentSnapshot document = (DocumentSnapshot) productTask.getResult();
                            Item item = document.toObject(Item.class);
                            if (item != null) {
                                productList.add(item);
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Log.w("FavoriteItems", "Error fetching product details.", task.getException());
                }
            }
        });
    }

    private List<Task<DocumentSnapshot>> fetchProductTasks(CollectionReference productsRef, List<String> favoriteIds) {
        List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
        for (String id : favoriteIds) {
            tasks.add(productsRef.document(id).get());
        }
        return tasks;
    }
}
