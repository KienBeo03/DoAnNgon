package com.example.doanngon.Adapter;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanngon.DTO.Item;
import com.example.doanngon.Fragmet.ct_themmon;
import com.example.doanngon.Fragmet.themmon;
import com.example.doanngon.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder>{
    private Context context;
    private List<Item> itemList;

    public ItemAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }
    @NonNull
    @Override
    public ItemAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.custom_layout_themmon, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ItemViewHolder holder, int position) {
        Item item = itemList.get(position);

        holder.titleTextView.setText(item.getTitle());
        holder.cateTV.setText(item.getCategory());
        Picasso.get().load(item.getImageUrl()).into(holder.imageView);

        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v, holder.getAdapterPosition(),item.getId());
            }
        });
    }
    private void showPopupMenu(View view, int position,String documentId) {
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_themmon, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_edit:
                        openFragment(documentId);
                        return true;
                    case R.id.action_delete:
                        FirebaseFirestore db;
                        db = FirebaseFirestore.getInstance();
                        // Xóa tài liệu
                        db.collection("recipes").document(documentId)
                                .delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(context, "Xóa món ăn thành công", Toast.LENGTH_SHORT).show();
                                        } else {

                                            Log.w("Firestore", "Error deleting document", task.getException());
                                        }
                                    }
                                });
                        return true;
                    default:
                        return false;
                }
            }


        });
        popup.show();
    }
    @Override
    public int getItemCount() {
        if (itemList != null) {
            return itemList.size();
        } else {
            return 0; // Trả về 0 nếu itemList là null
        }
    }
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView,cateTV;
        public RelativeLayout itemLayout;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.cus_luthemon_img);
            titleTextView = itemView.findViewById(R.id.cus_luthemon_title);
            cateTV = itemView.findViewById(R.id.cus_luthemon_cate);
            itemLayout = itemView.findViewById(R.id.item_layout);

        }
    }
    private void openFragment(String documentId) {
        FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ct_themmon frg_them = new ct_themmon();
        Bundle dataID = new Bundle();
        dataID.putString("ID", documentId);
        dataID.putString("dataKey", "sua");
        frg_them.setArguments(dataID);
        fragmentTransaction.replace(R.id.frLayout, frg_them);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    private void fetchDataFromFirestore(String documentId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("recipes").document(documentId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}
