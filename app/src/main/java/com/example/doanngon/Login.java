package com.example.doanngon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doanngon.Fragmet.TaiKhoan;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class Login extends AppCompatActivity {
    EditText edtPass,edtUser;
    CheckBox cbShowPass;
    TextView txtForgotPass,txtSignUp;
    Button btnLogin;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AnhXa();
        firebaseAuth = FirebaseAuth.getInstance();
        cbShowPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Nếu được kiểm tra, hiển thị mật khẩu.
                    edtPass.setTransformationMethod(null);
                } else {
                    // Nếu không được kiểm tra, ẩn mật khẩu.
                    edtPass.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });


        String text = txtForgotPass.getText().toString();

        SpannableString spannableString = new SpannableString(text);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                // Đây là nơi bạn thực hiện hành động khi nhấp vào văn bản.
                // Ví dụ: mở một layout khác.
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        };
        // Áp dụng ClickableSpan cho phần văn bản cần nhấp.
        spannableString.setSpan(clickableSpan, 0, 15, 0);
        txtForgotPass.setText(spannableString);
        txtForgotPass.setMovementMethod(LinkMovementMethod.getInstance());


        String textSignUp = txtSignUp.getText().toString();

        SpannableString spannableString1 = new SpannableString(textSignUp);
        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                // Đây là nơi bạn thực hiện hành động khi nhấp vào văn bản.
                // Ví dụ: mở một layout khác.
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        };

        // Áp dụng ClickableSpan cho phần văn bản cần nhấp.
        spannableString1.setSpan(clickableSpan1, 24, 31, 0);
        txtSignUp.setText(spannableString1);
        txtSignUp.setMovementMethod(LinkMovementMethod.getInstance());

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    private void loginUser() {
        String username = edtUser.getText().toString().trim();
        String password = edtPass.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(Login.this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            if (password.length() < 6) {
                                Toast.makeText(Login.this, "Mật khẩu quá ngắn", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(Login.this, "Đăng nhập thất bại", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            // Retrieve the user ID
                            String userId = firebaseAuth.getCurrentUser().getUid();
                            Toast.makeText(Login.this, "Đăng nhập thành công", Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(Login.this, MainActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("USER_ID", userId);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    }
                });
    }




    void AnhXa(){
        edtPass = findViewById(R.id.edtPassWord);
        edtUser= findViewById(R.id.edtUserName);
        cbShowPass= findViewById(R.id.cbShowPass);
        txtForgotPass= findViewById(R.id.txtForgotPass);
        txtSignUp= findViewById(R.id.txtSignUp);
        btnLogin = findViewById(R.id.btnLogin);
    }
}