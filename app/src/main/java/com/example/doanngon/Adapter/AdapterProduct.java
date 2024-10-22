package com.example.doanngon.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

public class AdapterProduct extends RecyclerView.Adapter<AdapterProduct.ViewHolder> {
    private List<MonAn> productList;
    private Context context;


    public AdapterProduct(Context context,List<MonAn> productList) {
        this.productList = productList;
        this.context = context;

    }
    @NonNull
    @Override
    public AdapterProduct.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.custom_layout_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterProduct.ViewHolder holder, int position) {
        MonAn product = productList.get(position);


        Picasso.get().load(product.getimageUrl()).into(holder.imgProduct);
        Picasso.get().load(R.drawable.ic_person);
        holder.userName.setText("Kien Beo");
        holder.productName.setText(product.gettitle());
        holder.time.setText(product.getcookingTime());
        holder.imgSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    String userId = user.getUid();

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference favoriteRef = db.collection("Users").document(userId).collection("favorite").document(product.getId());

                    if (!product.isFavorite()) {
                        product.setFavorite(true);
                        holder.imgSave.setBackgroundResource(R.drawable.ic_favorite_border);

//                         Save favorite to Firestore
                        Map<String, Object> favoriteProduct = new HashMap<>();
                        favoriteProduct.put("id", product.getId());
                        favoriteProduct.put("title", product.gettitle()); // Add any other relevant fields

                        favoriteRef.set(favoriteProduct)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(context, "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("Favorite", "Error adding product to favorites", e);
                                    }
                                });
                    } else {
                        product.setFavorite(false);
                        holder.imgSave.setBackgroundResource(R.drawable.ic_favorite);

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
                    }
                } else {
                    // Handle the case when the user is not logged in
                    Log.w("Favorite", "User not logged in");
                }
            }
        });
        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAciivity(product.getId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageButton imgProduct;
        public ImageView imgAvatar;
        public TextView userName;
        public TextView productName;
        public TextView time;
        public ImageButton imgSave;
        public LinearLayout itemLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.cus_imgProduct);
            imgAvatar = itemView.findViewById(R.id.cus_imgAvata);
            userName = itemView.findViewById(R.id.cus_txtUserName);
            productName = itemView.findViewById(R.id.cus_txtNameProduct);
            time = itemView.findViewById(R.id.cus_txtTime);
            imgSave = itemView.findViewById(R.id.cus_imgSave);
            itemLayout = itemView.findViewById(R.id.cus_lu_monan);
        }
    }

    private void openAciivity(String documentId) {
        Intent intent = new Intent(context, ct_monan.class);
        intent.putExtra("ITEM_ID", documentId); // Passing the item ID to the new activity
        context.startActivity(intent);
    }
}

