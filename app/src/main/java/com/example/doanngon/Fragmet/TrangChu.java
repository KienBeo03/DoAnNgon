package com.example.doanngon.Fragmet;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.ImageButton;

import com.example.doanngon.Adapter.AdapterPhoto;
import com.example.doanngon.Adapter.AdapterProduct;
import com.example.doanngon.Adapter.ItemAdapter;
import com.example.doanngon.DTO.Item;
import com.example.doanngon.DTO.MonAn;
import com.example.doanngon.DTO.Photo;
import com.example.doanngon.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;


public class TrangChu extends Fragment {

    private ViewPager viewPager;
    private CircleIndicator circleIndicator;
    private AdapterPhoto adapterPhoto;
    private View view;
    List<Photo> mListPhoto;
    Timer mTimer;

    ImageButton ibtnGa,ibtnHaiSan,ibtnLon,ibtnBo,ibtnCa,ibtnRauCu;
    private static final String TAG = "";
    private List<MonAn> productList;

    private AdapterProduct adapterProduct;

    Animation animation;
    private RecyclerView recyclerView;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_trang_chu, container, false);
        AnhXa();
        slideMore();
        autoSlideImage();
        autoZoom();
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        productList = new ArrayList<>();

        adapterProduct = new AdapterProduct(getContext(),productList);
        recyclerView.setAdapter(adapterProduct);


        fetchDataFromFirestore();


        return view;

    }
    private void fetchDataFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("recipes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                MonAn product = document.toObject(MonAn.class);
                                productList.add(product);
                            }
                            adapterProduct.notifyDataSetChanged();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
    private void slideMore(){
        viewPager = view.findViewById(R.id.vPager);
        circleIndicator = view.findViewById(R.id.crcSlide);
        mListPhoto = getListPhoto();
        adapterPhoto = new AdapterPhoto(getContext(),mListPhoto);
        viewPager.setAdapter(adapterPhoto);
        circleIndicator.setViewPager(viewPager);
        adapterPhoto.registerDataSetObserver(circleIndicator.getDataSetObserver());
    }
    private List<Photo> getListPhoto() {
        List<Photo> list = new ArrayList<>();
        list.add(new Photo(R.drawable.slide1));
        list.add(new Photo(R.drawable.slide2));
        list.add(new Photo(R.drawable.slide3));
        list.add(new Photo(R.drawable.slide4));
        return list;
    }
    private void autoSlideImage(){
        if(mListPhoto == null || mListPhoto.isEmpty() || viewPager == null){
            return;
        }
        if(mTimer == null){
            mTimer = new Timer();
        }
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        int currentItem = viewPager.getCurrentItem();
                        int totalitem = mListPhoto.size() - 1;
                        if(currentItem < totalitem){
                            currentItem ++;
                            viewPager.setCurrentItem(currentItem);
                        }else {
                            viewPager.setCurrentItem(0);
                        }
                    }
                });
            }
        },500,5000);
    }
    private void AnhXa(){
        ibtnGa = view.findViewById(R.id.btnGa);
        ibtnHaiSan = view.findViewById(R.id.btnHaiSan);
        ibtnLon = view.findViewById(R.id.btnLon);
        ibtnBo = view.findViewById(R.id.btnBo);
        ibtnCa = view.findViewById(R.id.btnCa);
        ibtnRauCu = view.findViewById(R.id.btnRauCu);
        animation = AnimationUtils.loadAnimation(getContext(),R.anim.ami_zoom);


    }
    private int[] imageButtonsIds = {R.id.btnGa, R.id.btnHaiSan, R.id.btnLon, R.id.btnBo, R.id.btnCa, R.id.btnRauCu};
    private int currentImageButtonIndex = 0;
    ImageButton previousImageButton;
    private void autoZoom(){
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.ami_zoom);
        ImageButton currentImageButton = view.findViewById(imageButtonsIds[currentImageButtonIndex]);
        previousImageButton = null;

        if (currentImageButtonIndex > 0) {
            previousImageButton = view.findViewById(imageButtonsIds[currentImageButtonIndex - 1]);
        }

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Do nothing on start
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (previousImageButton != null) {
                    previousImageButton.clearAnimation();
                }
                if (currentImageButtonIndex < imageButtonsIds.length - 1) {
                    currentImageButtonIndex++;
                    autoZoom(); // Gọi đệ quy để chuyển sang image button tiếp theo
                }else {
                    // Nếu đã chuyển hết tất cả image button, quay lại image button đầu tiên
                    currentImageButtonIndex = 0;
                    autoZoom(); // Gọi đệ quy để bắt đầu lại từ image button đầu tiên
                }
            }


            @Override
            public void onAnimationRepeat(Animation animation) {
                // Do nothing on repeat
            }
        });

        currentImageButton.startAnimation(animation);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mTimer != null){
            mTimer.cancel();
            mTimer = null;
        }
    }




}