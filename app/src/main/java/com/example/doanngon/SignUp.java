package com.example.doanngon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doanngon.DTO.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {
    EditText edtPass,edtFullName,edtEmail,edtUserName,edtCPass;

    TextView txtLogin;
    Button btnRegister;
    private FirebaseAuth mAuth;
    ImageView imageView;
    Uri imageUri;
    StorageReference storageRef;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        AnhXa();
        String text = txtLogin.getText().toString();
        mAuth = FirebaseAuth.getInstance();
        SpannableString spannableString = new SpannableString(text);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                // Đây là nơi bạn thực hiện hành động khi nhấp vào văn bản.
                // Ví dụ: mở một layout khác.
                Intent intent = new Intent(SignUp.this, Login.class);
                startActivity(intent);
            }
        };
        // Áp dụng ClickableSpan cho phần văn bản cần nhấp.
        spannableString.setSpan(clickableSpan, 22, 31, 0);
        txtLogin.setText(spannableString);
        txtLogin.setMovementMethod(LinkMovementMethod.getInstance());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("profile_images");
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtFullName.getText().toString().trim();
                String username = edtUserName.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String password = edtPass.getText().toString().trim();
                String confirmPassword = edtCPass.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    edtFullName.setError("Vui lòng nhập tên");
                    return;
                }

                if (TextUtils.isEmpty(username)) {
                    edtUserName.setError("Vui lòng nhập tên đăng nhập");
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    edtEmail.setError("Vui lòng nhập email");
                    return;
                }


                if (TextUtils.isEmpty(password)) {
                    edtPass.setError("Vui lòng nhập mật khẩu");
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    edtCPass.setError("Mật khẩu không khớp");
                    return;
                }
                Toast.makeText(SignUp.this, "Đã Ấn", Toast.LENGTH_SHORT).show();

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    String userId = mAuth.getCurrentUser().getUid();


                                    if (imageUri != null) {
                                        uploadImage(userId, username, password, name, email, "address");
                                    } else {
                                        saveUserToFirestore(userId, new User("", username, password, name, email, "address", ""));
                                    }
                                } else {
                                    Toast.makeText(SignUp.this, "Registration failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


            }
        });
    }

    void AnhXa(){
        edtPass = findViewById(R.id.edtPassW);
        edtCPass = findViewById(R.id.edtCPass);
        edtUserName= findViewById(R.id.edtUserName1);
        txtLogin= findViewById(R.id.txtLogin);
        btnRegister = findViewById(R.id.btnRegister);
        edtFullName = findViewById(R.id.edtFullName);
        edtEmail = findViewById(R.id.edtEmail);
        imageView = findViewById(R.id.imgAvata);
    }
    private void saveUserToFirestore(String userId, User user) {

        DocumentReference newUserRef = db.collection("Users").document(userId);
        user.setUserID(newUserRef.getId());

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("userID", user.getUserID());
        userMap.put("userName", user.getUserName());
        userMap.put("passWord", user.getPassWord());
        userMap.put("name", user.getName());
        userMap.put("email", user.getEmail());
        userMap.put("address", user.getAddress());
        userMap.put("imageUrl", user.getImageUrl());

        db.collection("Users").document(userId).set(userMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUp.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignUp.this, "Đăng ký thật bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void uploadImage(final String userID, final String userName, final String password, final String fullName, final String email, final String address) {

        final StorageReference fileReference = storageRef.child(userID + ".jpg");

        fileReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();

                                User user = new User(userID, userName, password, fullName, email, address, imageUrl);
                                saveUserToFirestore(userID, user);
                            }
                        });
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(SignUp.this, "Failed to upload image", Toast.LENGTH_SHORT).show());
    }



}