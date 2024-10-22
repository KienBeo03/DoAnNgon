package com.example.doanngon.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanngon.DTO.Item;
import com.example.doanngon.DTO.MonAn;
import com.example.doanngon.R;
import com.example.doanngon.ct_monan;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterYeuThich extends RecyclerView.Adapter<AdapterYeuThich.YeuThichViewHolder> {
    private Context context;
    private List<Item> itemList;

    public AdapterYeuThich(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public AdapterYeuThich.YeuThichViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.custom_layout_yeuthich, parent, false);
        return new AdapterYeuThich.YeuThichViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull YeuThichViewHolder holder, int position) {
        Item item = itemList.get(position);

        holder.cus_yt_title.setText(item.getTitle());
        holder.cus_yt_cate.setText(item.getCategory());
        Picasso.get().load(item.getImageUrl()).into(holder.cus_yt_img);

        holder.item_layout_yt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAciivity(item.getId());
            }
        });

        holder.cus_yt_imgYeuThich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    String userId = user.getUid();

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference favoriteRef = db.collection("Users").document(userId).collection("favorite").document(item.getId());
                        item.setFavorite(false);
                        holder.cus_yt_imgYeuThich.setBackgroundResource(R.drawable.ic_favorite);

                        // Remove favorite from Firestore
                        favoriteRef.delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(context, "Đã xóa khỏi yêu thích", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("Favorite", "Error removing product from favorites", e);
                                    }
                                });
                } else {
                    // Handle the case when the user is not logged in
                    Log.w("Favorite", "User not logged in");
                }
            }
        });
        holder.item_layout_yt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAciivity(item.getId());
            }
        });
    }


    @Override
    public int getItemCount() {
        if (itemList != null) {
            return itemList.size();
        } else {
            return 0; // Trả về 0 nếu itemList là null
        }
    }

    public static class YeuThichViewHolder extends RecyclerView.ViewHolder {
        ImageView cus_yt_img;
        TextView cus_yt_title, cus_yt_cate;
        public RelativeLayout item_layout_yt;
        ImageButton cus_yt_imgYeuThich;

        public YeuThichViewHolder(@NonNull View itemView) {
            super(itemView);
            cus_yt_img = itemView.findViewById(R.id.cus_yt_img);
            cus_yt_title = itemView.findViewById(R.id.cus_yt_title);
            cus_yt_cate = itemView.findViewById(R.id.cus_yt_cate);
            item_layout_yt = itemView.findViewById(R.id.item_layout_yt);
            cus_yt_imgYeuThich = itemView.findViewById(R.id.cus_yt_imgYeuThich);

        }
    }

    private void openAciivity(String documentId) {
        Intent intent = new Intent(context, ct_monan.class);
        intent.putExtra("ITEM_ID", documentId); // Passing the item ID to the new activity
        context.startActivity(intent);
    }

}