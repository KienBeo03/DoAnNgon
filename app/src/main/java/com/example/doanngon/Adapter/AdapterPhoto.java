package com.example.doanngon.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.doanngon.DTO.Photo;
import com.example.doanngon.R;

import java.util.List;

public class AdapterPhoto extends PagerAdapter {

    private Context mContext;
    private List<Photo> mListPhoto;

    public AdapterPhoto(Context mContext, List<Photo> mListPhoto) {
        this.mContext = mContext;
        this.mListPhoto = mListPhoto;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.iteam_photo,container,false);
        ImageView imageView = view.findViewById(R.id.imgViewSlide);

        Photo photo = mListPhoto.get(position);
        if(photo != null){
            Glide.with(mContext).load(photo.getResourceId()).into(imageView);
        }
        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        if(mListPhoto != null){
            return mListPhoto.size();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
