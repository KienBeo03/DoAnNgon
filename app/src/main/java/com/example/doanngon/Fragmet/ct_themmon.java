package com.example.doanngon.Fragmet;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.doanngon.DTO.Ingredient;
import com.example.doanngon.DTO.Step;
import com.example.doanngon.R;
import com.example.doanngon.ct_monan;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.play.integrity.internal.f;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ct_themmon extends Fragment {
    LinearLayout ingredientsContainer;
    ImageButton recipe_image;
    ImageButton btnImgSua;
    EditText field;
    Button btnXoaStep;
    EditText recipe_title,recipe_description,servings,cooking_time;
    Button add_ingredient,add_step,save_button;
    LinearLayout stepsContainer;
    View view;
    int stepNumber = 0;
    ImageButton btnImgAdd;
    Uri uriTitle;
    ActivityResultLauncher<Intent> activityResultLauncher,activityResultLauncher1,activityResultLauncher2;
    Map<Integer, Uri> uriSteps = new HashMap<>();
    Map<Integer, String> stepImageUrls = new HashMap<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_ct_themmon, container, false);
        ingredientsContainer = view.findViewById(R.id.ingredients_container);
        stepsContainer = view.findViewById(R.id.steps_container);
        AnhXa();
        Button addIngredientButton = view.findViewById(R.id.add_ingredient);
        Button addStepButton = view.findViewById(R.id.add_step);
        restoreData();
        Bundle bundle = getArguments();

        if (bundle != null) {
            String receivedData = bundle.getString("dataKey");
            if (receivedData == "them"){

                save_button.setText("Thêm Món");
                //btn lưu
                save_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String title = recipe_title.getText().toString();
                        String cookingTime = cooking_time.getText().toString();
                        String serving = servings.getText().toString();

                        if (title.isEmpty()) {
                            recipe_title.setError("Tiêu đề không được để trống");
                            recipe_title.requestFocus();
                            return;
                        }

                        if (cookingTime.isEmpty()) {
                            cooking_time.setError("thời gian không được để trống");
                            cooking_time.requestFocus();
                            return;
                        }
                        if (serving.isEmpty()) {
                            servings.setError("Khẩu phần ăn không được để trống");
                            servings.requestFocus();
                            return;
                        }

                        if (recipe_image == null) {
                            Toast.makeText(getContext(), "Hình ảnh tiêu đề không được để trống", Toast.LENGTH_SHORT).show();
                            recipe_image.requestFocus();
                            return;
                        }



                        Log.e("sfsf","..........." + uriTitle);
                        if (uriTitle != null && uriSteps != null) {


                            uploadImageAndSaveData(uriTitle);
                            Toast.makeText(getContext(), "Thêm thành công món ăn", Toast.LENGTH_SHORT).show();
                            openFragment();

                        } else {
                            Toast.makeText(getContext(), "Chưa thêm món, hãy điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                        }

                    }
                });


            }
            if (receivedData == "sua"){
                String documentId = bundle.getString("ID");

                loadData(documentId);

                save_button.setText("Lưu");
                //btn lưu
                save_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (uriTitle != null && stepImageUrls != null) {
                            uploadImageAndSaveData(uriTitle);
                            openFragment();

                        } else {
                            Toast.makeText(getContext(), "Chưa thêm món, hãy điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }

        }

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            uriTitle = result.getData().getData();
                            if (uriTitle != null) {
                                // Set the selected image URI to the ImageButton
                                    recipe_image.setImageURI(uriTitle);

                            }
                        }
                    }
                });
        activityResultLauncher1 = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            Uri selectedImageUri = result.getData().getData();
                                if (btnImgAdd != null) {
                                    btnImgAdd.setImageURI(selectedImageUri);
                                    uriSteps.put(stepNumber, selectedImageUri); // Lưu URI của ảnh

                                }
                            }
                        }
                    }

        );
        activityResultLauncher2 = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            Uri selectedImageUri = result.getData().getData();

                            if (btnImgSua != null) {
                                btnImgSua.setImageURI(selectedImageUri);
                                uriSteps.put(stepNumber, selectedImageUri); // Lưu URI của ảnh

                            }
                        }
                    }
                }
        );
        // button thêm ảnh lớn
        recipe_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker(activityResultLauncher);
            }
        });
        //button thêm edttext nguyên liệu
        addIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout llIngredients = new LinearLayout(getContext());
                llIngredients.setOrientation(LinearLayout.HORIZONTAL);

                // Add the EditText field for ingredient input

                LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1.0f
                );

                addField(llIngredients, "Nguyên Liệu               ");
                // Add the delete button next to the EditText
                Button btnXoaIngredients = new Button(getContext());
                btnXoaIngredients.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_dele));
                LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(100, 100);
                btnXoaIngredients.setLayoutParams(buttonParams);
                llIngredients.addView(btnXoaIngredients);

                // Add the LinearLayout with EditText and Button to the container
                ingredientsContainer.addView(llIngredients);

                // Set OnClickListener for the delete button to remove the entire row
                btnXoaIngredients.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ingredientsContainer.removeView(llIngredients);
                    }
                });
            }
        });
        //button thêm edtttext bước làm
        addStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tvStep = new TextView(getActivity());
                btnImgAdd = new ImageButton(getActivity());
                btnImgAdd.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_add_a_photo));

                btnXoaStep = new Button(getContext());
                btnXoaStep.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.ic_dele));
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(70, 70);
                btnXoaStep.setLayoutParams(params1);
                stepNumber++;
                tvStep.setText("Bước " + stepNumber);


                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                btnImgAdd.setLayoutParams(params);
                stepsContainer.addView(tvStep);
                addField(stepsContainer, "Trộn bột cho đến khi đặc lại");
                stepsContainer.addView(btnImgAdd);
                stepsContainer.addView(btnXoaStep);
                btnXoaStep.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnImgAdd.setVisibility(View.GONE); // Ẩn button cần xóa
                        tvStep.setVisibility(View.GONE);
                        btnXoaStep.setVisibility(View.GONE);
                        field.setVisibility(View.GONE);
                        stepNumber--;
                    }
                });
                btnImgAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openImagePicker(activityResultLauncher1);

                    }
                });
            }
        });

        return view;
    }
    //thêm edittext
    private void addField(LinearLayout container, String hint) {
        field = new EditText(getActivity());
        field.setHint(hint);
        container.addView(field);
    }
    private void AnhXa(){
        recipe_image = view.findViewById(R.id.recipe_image);
        save_button = view.findViewById(R.id.save_button);
        add_step = view.findViewById(R.id.add_step);
        add_ingredient = view.findViewById(R.id.add_ingredient);
        cooking_time = view.findViewById(R.id.cooking_time);
        servings = view.findViewById(R.id.servings);
        recipe_description = view.findViewById(R.id.recipe_description);
        recipe_title = view.findViewById(R.id.recipe_title);
    }
    //Thêm dữ liệu
    private void onClickPushData(String urlTitle, Map<Integer, String> stepImageUrls){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Collecting data from the UI
        String title = recipe_title.getText().toString();
        String description = recipe_description.getText().toString();
        String cookingTime = cooking_time.getText().toString();
        String serving = servings.getText().toString();

        Map<String, Object> recipeData = new HashMap<>();
        recipeData.put("title", title);
        recipeData.put("category", description);
        recipeData.put("cookingTime", cookingTime);
        recipeData.put("serving", serving);
        if (urlTitle != null) {
            recipeData.put("imageUrl", urlTitle);
        }
        // Collecting ingredients
        int ingredientCount = ingredientsContainer.getChildCount();
        List<Ingredient> ingredients = new ArrayList<>();
        for (int i = 0; i < ingredientCount; i++) {
            View view = ingredientsContainer.getChildAt(i);
            if (view instanceof LinearLayout) {
                LinearLayout llIngredient = (LinearLayout) view;
                EditText editText = (EditText) llIngredient.getChildAt(0); // Assuming EditText is the first child
                String ingredientName = editText.getText().toString();
                ingredients.add(new Ingredient(ingredientName));
            }
        }
        recipeData.put("ingredients", ingredients);

        // Collecting steps
        int stepCount = stepsContainer.getChildCount();
        List<Step> steps = new ArrayList<>();
        int stepIndex = 1;
        for (int i = 0; i < stepCount; i++) {
            View view = stepsContainer.getChildAt(i);
            if (view instanceof EditText) {
                String stepDescription = ((EditText) view).getText().toString();
                String stepImageUrl = stepImageUrls.get(stepIndex);
                steps.add(new Step(stepDescription, stepImageUrl));
                stepIndex++;
            }
        }
        recipeData.put("steps", steps);

//         Saving to Firestore
        firestore.collection("recipes")
                .add(recipeData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String documentId = documentReference.getId();
                        recipeData.put("id", documentId);  // Thêm ID tài liệu vào dữ liệu

                        // Cập nhật tài liệu với ID tài liệu
                        documentReference.set(recipeData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getContext(), "Thêm món thành công", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Xử lý lỗi nếu cập nhật thất bại
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Xử lý lỗi nếu thêm tài liệu thất bại
                    }
                });
    }

    //mở fragment
    private void openFragment() {
        themmon fthemmon = new themmon();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.frLayout, fthemmon);
        transaction.addToBackStack(null);  // Thêm Fragment vào back stack để có thể quay lại
        transaction.commit();
    }


    private void restoreData() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference docRef = firestore.collection("recipes").document("id_recipes");

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Populate UI with the retrieved data
                        recipe_title.setText(document.getString("title"));
                        recipe_description.setText(document.getString("category"));
                        cooking_time.setText(document.getString("cookingTime"));
                        servings.setText(document.getString("serving"));

                        List<Map<String, Object>> ingredientMaps = (List<Map<String, Object>>) document.get("ingredients");
                        if (ingredientMaps != null) {
                            for (Map<String, Object> map : ingredientMaps) {
                                Ingredient ingredient = new Ingredient((String) map.get("name"));
                                addField(ingredientsContainer, ingredient.getName());
                            }
                        }

                        List<Map<String, Object>> stepMaps = (List<Map<String, Object>>) document.get("steps");
                        if (stepMaps != null) {
                            for (Map<String, Object> map : stepMaps) {
                                Step step = new Step((String) map.get("description"),(String) map.get("uriStep"));
                                addField(stepsContainer, step.getDescription());
                                addField(stepsContainer, step.getImgStep());
                            }
                        }
                    } else {
                        Toast.makeText(getActivity(), "Không có tài liệu như vậy", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Get failed with " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // mở tệp ảnh
    private void openImagePicker(ActivityResultLauncher<Intent> activityResult) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activityResult.launch(intent);
    }
    //lưu ảnh tiêu đề lên storege
    private void uploadImageAndSaveData(Uri imageUri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child("images/title/" + imageUri.getLastPathSegment());
        UploadTask uploadTask = imagesRef.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getActivity(), "Image upload failed", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageUrl = uri.toString();
                        uploadStepImagesAndSaveData(imageUrl);
                    }
                });
            }
        });
    }
    //lưu ảnh các bước lên storege
    private void uploadStepImagesAndSaveData(final String titleImageUrl) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        Bundle bundle = getArguments();
        String documentId = bundle.getString("ID");

        for (Map.Entry<Integer, Uri> entry : uriSteps.entrySet()) {
            Uri stepImageUri = entry.getValue();
            final int stepIndex = entry.getKey();
            StorageReference stepImagesRef = storageRef.child("images/steps/" + stepImageUri.getLastPathSegment());

            UploadTask uploadTask = stepImagesRef.putFile(stepImageUri);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getActivity(), "Step image upload failed", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    stepImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if (documentId != null){

                                Log.e("documentId","" + documentId);
                                stepImageUrls.put(stepIndex, uri.toString());
                                if (stepImageUrls.size() == uriSteps.size()) {
                                    onClickUpdateData(documentId,titleImageUrl,stepImageUrls);
                                }
                            }else{
                                Log.e("documentId","" + documentId);
                                stepImageUrls.put(stepIndex, uri.toString());
                                if (stepImageUrls.size() == uriSteps.size()) {
                                    onClickPushData(titleImageUrl, stepImageUrls);
                                }
                            }

                        }
                    });
                }
            });
        }
        if (documentId != null){
            Log.e("documentId sua","" + documentId);
            if (uriSteps.isEmpty()) {
                onClickUpdateData(documentId,titleImageUrl,stepImageUrls);
            }
        }else{
            Log.e("documentId","" + documentId);
            if (uriSteps.isEmpty()) {
                onClickPushData(titleImageUrl, stepImageUrls);
            }
        }

    }
    // Sửa dữ liệu
    private void onClickUpdateData(String documentId, String urlTitle, Map<Integer, String> stepImageUrls){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Collecting data from the UI
        String title = recipe_title.getText().toString();
        String description = recipe_description.getText().toString();
        String cookingTime = cooking_time.getText().toString();
        String serving = servings.getText().toString();

        Map<String, Object> recipeData = new HashMap<>();
        recipeData.put("title", title);
        recipeData.put("category", description);
        recipeData.put("cookingTime", cookingTime);
        recipeData.put("serving", serving);

        if (urlTitle != null) {
            recipeData.put("imageUrl", urlTitle);
        }
        // Collecting ingredients
        int ingredientCount = ingredientsContainer.getChildCount();
        List<Ingredient> ingredients = new ArrayList<>();
        for (int i = 0; i < ingredientCount; i++) {
            View view = ingredientsContainer.getChildAt(i);
            if (view instanceof LinearLayout) {
                LinearLayout llIngredient = (LinearLayout) view;
                EditText editText = (EditText) llIngredient.getChildAt(0); // Assuming EditText is the first child
                String ingredientName = editText.getText().toString();
                ingredients.add(new Ingredient(ingredientName));
            }
        }
        recipeData.put("ingredients", ingredients);

        // Collecting steps
        int stepCount = stepsContainer.getChildCount();
        List<Step> steps = new ArrayList<>();
        int stepIndex = 1;
        for (int i = 0; i < stepCount; i++) {
            View view = stepsContainer.getChildAt(i);
            if (view instanceof EditText) {
                String stepDescription = ((EditText) view).getText().toString();
                String stepImageUrl = stepImageUrls.get(stepIndex);
                steps.add(new Step(stepDescription, stepImageUrl));
                stepIndex++;
            }
        }
        recipeData.put("steps", steps);

//         Updating Firestore document
        firestore.collection("recipes").document(documentId)
                .update(recipeData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Update thánh công", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Chưa cập nhật được dữ liệu", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void loadData(String documentId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference docRef = firestore.collection("recipes").document(documentId);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Populate UI with the retrieved data
                        recipe_title.setText(document.getString("title"));
                        recipe_description.setText(document.getString("category"));
                        cooking_time.setText(document.getString("cookingTime"));
                        servings.setText(document.getString("serving"));

                        List<Map<String, Object>> ingredientMaps = (List<Map<String, Object>>) document.get("ingredients");
                        if (ingredientMaps != null) {
                            for (Map<String, Object> map : ingredientMaps) {
                                String ingredientName = (String) map.get("name");
                                if (ingredientName != null) {
                                    LinearLayout llIngredients = new LinearLayout(getContext());
                                    llIngredients.setOrientation(LinearLayout.HORIZONTAL);

                                    EditText ingredientTV = new EditText(getContext());
                                    ingredientTV.setText(ingredientName);
                                    LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(
                                            0,
                                            LinearLayout.LayoutParams.WRAP_CONTENT,
                                            1.0f
                                    );
                                    ingredientTV.setLayoutParams(editTextParams);
                                    llIngredients.addView(ingredientTV);

                                    // Add the delete button next to the EditText
                                    Button btnXoaIngredients = new Button(getContext());
                                    btnXoaIngredients.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.ic_dele));
                                    LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(50, 50);
                                    btnXoaIngredients.setLayoutParams(buttonParams);
                                    llIngredients.addView(btnXoaIngredients);
                                    ingredientsContainer.addView(llIngredients);

                                    btnXoaIngredients.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ingredientsContainer.removeView(llIngredients);
                                        }
                                    });
                                }
                            }
                        }

                        List<Map<String, Object>> stepMaps = (List<Map<String, Object>>) document.get("steps");
                        if (stepMaps != null) {

                            for (Map<String, Object> map : stepMaps) {

                                Step step = new Step((String) map.get("description"),(String) map.get("imgStep"));
                                TextView stepNumbertv = new TextView(getContext());
                                stepNumber++;
                                stepNumbertv.setText("Bước " + stepNumber);
                                stepNumbertv.setTextSize(20);
                                stepNumbertv.setTypeface(null, Typeface.BOLD);
                                stepsContainer.addView(stepNumbertv);

                                EditText stepDescriptionTextView = new EditText(getContext());
                                stepDescriptionTextView.setText(step.getDescription());
                                stepsContainer.addView(stepDescriptionTextView);

                                btnImgSua = new ImageButton(getContext());
                                // Kiểm tra nếu URL hình ảnh của bước không null, thì tải và hiển thị ảnh bằng Picasso
                                if (step.getImgStep() != null) {
                                    // Tạo ImageView để hiển thị hình ảnh của bước
                                    stepImageUrls.put(stepNumber,step.getImgStep());
                                    Picasso.get().load(step.getImgStep()).into(btnImgSua); // Sử dụng Picasso để tải ảnh từ URL và hiển thị lên ImageView
                                    stepsContainer.addView(btnImgSua);
                                }else{
                                    Picasso.get().load(R.drawable.ic_add_a_photo); // Sử dụng Picasso để tải ảnh từ URL và hiển thị lên ImageView
                                    stepsContainer.addView(btnImgSua);
                                }
                                // Tạo nút xóa và thêm vào stepsContainer
                                ImageButton btnDeleteStep = new ImageButton(getContext());
                                btnDeleteStep.setImageResource(R.drawable.ic_dele);
                                stepsContainer.addView(btnDeleteStep);

                                // Đặt OnClickListener cho nút xóa
                                btnDeleteStep.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        stepNumber--;
                                        // Xóa tất cả các view liên quan đến bước này khỏi stepsContainer
                                        stepsContainer.removeView(stepNumbertv);
                                        stepsContainer.removeView(stepDescriptionTextView);
                                        stepsContainer.removeView(btnImgSua);
                                        stepsContainer.removeView(btnDeleteStep);
                                    }
                                });
                                btnImgSua.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        openImagePicker(activityResultLauncher2);
                                    }
                                });
                            }
                        }

                        // Set the title image if available
                        uriTitle = Uri.parse(document.getString("imageUrl"));
                        if (uriTitle != null) {
                            Picasso.get().load(uriTitle).into(recipe_image);
                        }
                    } else {
                        Toast.makeText(getActivity(), "Không có tài liệu như vậy", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Get failed with " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

