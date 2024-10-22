package com.example.doanngon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doanngon.DTO.Step;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

public class ct_monan extends AppCompatActivity {
    private TextView recipeTitle;
    private TextView recipeCategory;
    private TextView cookingTime;
    private TextView servings;
    private LinearLayout ingredientsContainer;
    private LinearLayout stepsContainer;
    ImageView imgMonAn;
    int stepNumber = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ct_monan);

        // Initialize UI elements
        recipeTitle = findViewById(R.id.recipe_title);
        recipeCategory = findViewById(R.id.recipe_category);
        cookingTime = findViewById(R.id.cooking_time);
        servings = findViewById(R.id.servings);
        ingredientsContainer = findViewById(R.id.ingredients_container);
        stepsContainer = findViewById(R.id.steps_container);
        String documentId = getIntent().getStringExtra("ITEM_ID");
        imgMonAn = findViewById(R.id.ct_imgMonAn);
        // Call restoreData with the document ID
        restoreData(documentId);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Kích hoạt nút Back
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);



    }
    public boolean onSupportNavigateUp() {
        onBackPressed(); // Quay lại Activity trước đó khi nhấn nút Back
        return true;
    }
    private void restoreData(String documentId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference docRef = firestore.collection("recipes").document(documentId);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Populate UI with the retrieved data
                        recipeTitle.setText(document.getString("title"));
                        recipeCategory.setText(document.getString("category"));
                        cookingTime.setText("Thời gian: " + document.getString("cookingTime"));
                        servings.setText("Khẩu phần: " + document.getString("serving") + "người");

                        List<Map<String, Object>> ingredientMaps = (List<Map<String, Object>>) document.get("ingredients");
                        if (ingredientMaps != null) {
                            for (Map<String, Object> map : ingredientMaps) {
                                String ingredientName = (String) map.get("name");
                                addField(ingredientsContainer, ingredientName);
                            }
                        }

                        List<Map<String, Object>> stepMaps = (List<Map<String, Object>>) document.get("steps");
                        if (stepMaps != null) {
                            for (Map<String, Object> map : stepMaps) {
                                TextView stepNumbertv = new TextView(ct_monan.this);
                                stepNumbertv.setText("Bước " + stepNumber);
                                stepNumbertv.setTypeface(null, Typeface.BOLD);
                                stepsContainer.addView(stepNumbertv);
                                stepNumber++;
                                Step step = new Step((String) map.get("description"),(String) map.get("imgStep"));
                                // Tạo TextView để hiển thị mô tả của bước
                                TextView stepDescriptionTextView = new TextView(ct_monan.this);
                                stepDescriptionTextView.setText(step.getDescription());
                                stepsContainer.addView(stepDescriptionTextView);

                                // Kiểm tra nếu URL hình ảnh của bước không null, thì tải và hiển thị ảnh bằng Picasso
                                if (step.getImgStep() != null) {
                                    // Tạo ImageView để hiển thị hình ảnh của bước
                                    ImageView stepImageView = new ImageView(ct_monan.this);
                                    Picasso.get().load(step.getImgStep()).into(stepImageView); // Sử dụng Picasso để tải ảnh từ URL và hiển thị lên ImageView
                                    stepsContainer.addView(stepImageView);
                                }
                            }
                        }


                        // Set the title image if available
                        String imageUrl = document.getString("imageUrl");

                        if (imageUrl != null) {
                            Picasso.get().load(imageUrl).into(imgMonAn);
                        }
                    } else {
                        Toast.makeText(ct_monan.this, "No such document exists", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ct_monan.this, "Get failed with " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void addField(LinearLayout container, String text) {
        TextView textView = new TextView(this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setText(text);
        textView.setPadding(0, 8, 0, 8);
        container.addView(textView);
    }
}