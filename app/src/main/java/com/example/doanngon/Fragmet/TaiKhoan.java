package com.example.doanngon.Fragmet;

import static android.app.Activity.RESULT_OK;
import static android.content.Intent.getIntent;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.doanngon.Login;
import com.example.doanngon.Profile;
import com.example.doanngon.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class TaiKhoan extends Fragment {
    Button btnDangXuat,btnTaiKhoan,btnSave;
    View view;
    ImageView imgPerson;
    TextView tvUserName,txtEmail;
    private static final int REQUEST_CODE_UPDATE_PROFILE = 1;
    private FirebaseFirestore db;
    private String userId;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tai_khoan, container, false);
        // Inflate the layout for this fragment
        btnDangXuat = view.findViewById(R.id.btnLogout);
        imgPerson =  view.findViewById(R.id.imgPerson);
        tvUserName =  view.findViewById(R.id.tvUserName);
        txtEmail =  view.findViewById(R.id.txtEmail);
        btnSave = view.findViewById(R.id.btnSave);

        db = FirebaseFirestore.getInstance();

        Intent intent = getActivity().getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                userId = bundle.getString("USER_ID");
                fetchUserData(userId);
            }
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment();
            }
        });
        btnDangXuat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Login.class);
                startActivity(intent);
            }
        });
        btnTaiKhoan = view.findViewById(R.id.btnEditProfile);
        btnTaiKhoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Profile.class);
                startActivityForResult(intent, REQUEST_CODE_UPDATE_PROFILE);
            }
        });

        return view;
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_UPDATE_PROFILE && resultCode == RESULT_OK) {
            if (data != null) {

                if (data.getParcelableExtra("imageUri") instanceof Uri) {
                    String name = data.getStringExtra("name");
                    String email = data.getStringExtra("email");
                    Uri imageUri = data.getParcelableExtra("imageUri");

                    tvUserName.setText(name);
                    txtEmail.setText(email);
                    imgPerson.setImageURI(imageUri);
                } else {
                    String name = data.getStringExtra("name");
                    String email = data.getStringExtra("email");
                    String imageUri = data.getStringExtra("imageUri");

                    tvUserName.setText(name);
                    txtEmail.setText(email);
                    Picasso.get().load(imageUri).into(imgPerson);
                }
            }
        }
    }
    private void fetchUserData(String userId) {
        DocumentReference docRef = db.collection("Users").document(userId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("name");
                String email = documentSnapshot.getString("email");
                String imageUrl = documentSnapshot.getString("imageUrl");

                tvUserName.setText(name);
                txtEmail.setText(email);
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Picasso.get().load(imageUrl).into(imgPerson);
                } else {
                    imgPerson.setImageResource(R.drawable.ic_person); // Set default image
                }
            } else {
                Log.d("TaiKhoan", "No such document");
            }
        }).addOnFailureListener(e -> Log.w("TaiKhoan", "Error fetching document", e));
    }

    private void openFragment() {
        YeuThich fgmYeuThich = new YeuThich();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.frLayout, fgmYeuThich);
        transaction.addToBackStack(null);  // Thêm Fragment vào back stack để có thể quay lại
        transaction.commit();
    }

}