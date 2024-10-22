package com.example.doanngon.Adapter;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanngon.DTO.Item;
import com.example.doanngon.Fragmet.ct_themmon;
import com.example.doanngon.R;
import com.example.doanngon.ct_monan;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterTimKiem extends RecyclerView.Adapter<AdapterTimKiem.TimKiemViewHolder> {
    private Context context;
    private List<Item> itemList;

    public AdapterTimKiem(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public AdapterTimKiem.TimKiemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.custom_layout_themmon, parent, false);
        return new TimKiemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimKiemViewHolder holder, int position) {
        Item item = itemList.get(position);

        holder.titleTextView.setText(item.getTitle());
        holder.cateTV.setText(item.getCategory());
        Picasso.get().load(item.getImageUrl()).into(holder.imageView);

        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
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

    public static class TimKiemViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView, cateTV;
        public RelativeLayout itemLayout;

        public TimKiemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.cus_luthemon_img);
            titleTextView = itemView.findViewById(R.id.cus_luthemon_title);
            cateTV = itemView.findViewById(R.id.cus_luthemon_cate);
            itemLayout = itemView.findViewById(R.id.item_layout);

        }
    }

    private void openAciivity(String documentId) {
        Intent intent = new Intent(context, ct_monan.class);
        intent.putExtra("ITEM_ID", documentId); // Passing the item ID to the new activity
        context.startActivity(intent);
    }

}
