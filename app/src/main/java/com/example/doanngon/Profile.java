package com.example.doanngon;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 101;

    private TextView tvUserName, tvUserHandle, tvUserID;
    private EditText tvFullName, edtEmail, edtLocation, edtPhone;
    private ImageButton btnAddImage;
    private Button btnUpdate;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseUser currentUser;
    private Uri imageUri;
    String imageUrlResu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        tvUserName = findViewById(R.id.tvUserName);
        tvUserHandle = findViewById(R.id.tvUserHandle);
        tvUserID = findViewById(R.id.tvUserID);
        tvFullName = findViewById(R.id.tvFullName);
        edtEmail = findViewById(R.id.edtEmail);
        edtLocation = findViewById(R.id.edtLocation);
        edtPhone = findViewById(R.id.edtPhone);
        btnAddImage = findViewById(R.id.btnAddImage);
        btnUpdate = findViewById(R.id.btnUpdate);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();


        loadUserData("clA8rOKvCmZH72duUR0SV6WpVNK2");


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null) {
                    uploadImageAndSaveData("clA8rOKvCmZH72duUR0SV6WpVNK2");
                    String updatedName = tvFullName.getText().toString();
                    String updatedEmail = edtEmail.getText().toString();
                    Uri updatedImageUri = imageUri;

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("name", updatedName);
                    resultIntent.putExtra("email", updatedEmail);
                    resultIntent.putExtra("imageUri", updatedImageUri);
                    setResult(RESULT_OK, resultIntent);

                    finish();
                } else {
                    updateUserData("clA8rOKvCmZH72duUR0SV6WpVNK2", null);
                    String updatedName = tvFullName.getText().toString();
                    String updatedEmail = edtEmail.getText().toString();
                    String updatedImageUri = imageUrlResu;

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("name", updatedName);
                    resultIntent.putExtra("email", updatedEmail);
                    resultIntent.putExtra("imageUri", updatedImageUri);
                    setResult(RESULT_OK, resultIntent);

                    finish();

                }
            }
        });
        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
    }
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(btnAddImage);
        }
    }
    private void loadUserData(String userId) {
        db.collection("Users").document(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String name = document.getString("name");
                                String userName = document.getString("userName");
                                String email = document.getString("email");
                                String address = document.getString("address");
                                String phone = document.getString("phone");
                                imageUrlResu = document.getString("imageUrl");

                                tvUserName.setText(name);
                                tvUserHandle.setText(userName);
                                tvUserID.setText(userName); // Assuming userName is unique
                                tvFullName.setText(name);
                                edtEmail.setText(email);
                                edtLocation.setText(address);
                                edtPhone.setText(phone);

                                if (imageUrlResu != null && !imageUrlResu.isEmpty()) {
                                    Glide.with(Profile.this).load(imageUrlResu).into(btnAddImage);
                                }
                            } else {
                                Toast.makeText(Profile.this, "User data not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Profile.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void uploadImageAndSaveData(final String userId) {
        if (imageUri != null) {
            StorageReference fileReference = storage.getReference("profile_images").child(userId + ".jpg");
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();
                                    updateUserData(userId, imageUrl);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Profile.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // Update user data in Firestore
    private void updateUserData(String userId, @Nullable String imageUrl) {
        String name = tvFullName.getText().toString();
        String email = edtEmail.getText().toString();
        String address = edtLocation.getText().toString();
        String phone = edtPhone.getText().toString();

        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);
        user.put("address", address);
        user.put("phone", phone);
        if (imageUrl != null) {
            user.put("imageUrl", imageUrl);
        }

        db.collection("Users").document(userId)
                .update(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Profile.this, "User data updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Profile.this, "Error updating user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Handle storage permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openFileChooser();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}