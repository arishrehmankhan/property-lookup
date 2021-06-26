package com.arish.propertylookup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.arish.propertylookup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddPostActivity extends AppCompatActivity {

    private static final int PICK_IMG = 1;
    static final Integer READ_EXST = 0x4;

    private TextView sellPropertyTV;
    private TextView buyPropertyTV;

    private MaterialCheckBox rentCheckBox;

    private String buyOrSellProperty = "sell";
    private String rentProperty = "false";

    private Spinner propertyTypeSpinner;

    private LinearLayout propertyImagesLayout;

    private ImageView selectImagesButton;

    private RecyclerView selectedImagesRecyclerView;
    private SelectedImagesRecyclerViewAdapter selectedImagesRecyclerViewAdapter;

    private TextInputLayout textInputHnoStreet;
    private TextInputLayout textInputLocality;
    private TextInputLayout textInputCity;
    private TextInputLayout textInputPrice;
    private TextInputLayout textInputAge;
    private TextInputLayout textInputOtherDetails;

    private EditText hnoStreetEditText;
    private EditText localityEditText;
    private EditText cityEditText;

    private LinearLayout propertyPriceLayout;

    private EditText priceEditText;

    private LinearLayout propertyAgeLayout;

    private EditText ageEditText;

    private EditText otherDetailsEditText;

    private Button postPropertyButton;

    private ArrayList<Uri> selectedImagesURIList = new ArrayList();

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        changeStatusBarColor();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        
        if (firebaseAuth.getCurrentUser() == null || !firebaseAuth.getCurrentUser().isEmailVerified()) {

            Toast.makeText(this, "You need to be logged in.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();

        }

        // Setting Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sell Property");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sellPropertyTV = findViewById(R.id.sellPropertyTV);
        buyPropertyTV = findViewById(R.id.buyPropertyTV);

        rentCheckBox = findViewById(R.id.rent_checkbox);

        propertyTypeSpinner = findViewById(R.id.propertyTypeSpinner);

        propertyImagesLayout = findViewById(R.id.propertyImagesLayout);

        selectImagesButton = findViewById(R.id.selectImagesButton);
        selectedImagesRecyclerView = findViewById(R.id.selectedImagesRecyclerView);

        textInputHnoStreet = findViewById(R.id.textInputHnoStreet);
        textInputLocality = findViewById(R.id.textInputLocality);
        textInputCity = findViewById(R.id.textInputCity);

        propertyPriceLayout = findViewById(R.id.propertyPriceLayout);

        propertyAgeLayout = findViewById(R.id.propertyAgeLayout);

        textInputPrice = findViewById(R.id.textInputPrice);
        textInputAge = findViewById(R.id.textInputAge);
        textInputOtherDetails = findViewById(R.id.textInputOtherDetails);

        hnoStreetEditText = findViewById(R.id.hnoStreetEditText);
        localityEditText = findViewById(R.id.localityEditText);
        cityEditText = findViewById(R.id.cityEditText);
        priceEditText = findViewById(R.id.priceEditText);
        ageEditText = findViewById(R.id.ageEditText);
        otherDetailsEditText = findViewById(R.id.otherDetailsEditText);

        postPropertyButton = findViewById(R.id.postPropertyButton);

        sellPropertyTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                buyPropertyTV.setTextColor(Color.parseColor("#000000"));
                buyPropertyTV.setBackgroundResource(R.drawable.textview_border);

                sellPropertyTV.setTextColor(Color.parseColor("#ffffff"));
                sellPropertyTV.setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.colorAccent));

                propertyImagesLayout.setVisibility(View.VISIBLE);
                textInputHnoStreet.setVisibility(View.VISIBLE);
                propertyAgeLayout.setVisibility(View.VISIBLE);

                buyOrSellProperty = "sell";

            }
        });

        buyPropertyTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                buyPropertyTV.setTextColor(Color.parseColor("#ffffff"));
                buyPropertyTV.setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.colorAccent));

                sellPropertyTV.setTextColor(Color.parseColor("#000000"));
                sellPropertyTV.setBackgroundResource(R.drawable.textview_border);

                propertyImagesLayout.setVisibility(View.GONE);
                textInputHnoStreet.setVisibility(View.GONE);
                propertyAgeLayout.setVisibility(View.GONE);

                buyOrSellProperty = "buy";
            }
        });

        rentCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {

                    textInputPrice.setHint("Asking Price (₹/Month)");
                    rentProperty = "true";

                } else {

                    textInputPrice.setHint("Asking Price (₹)");
                    rentProperty = "false";

                }
            }
        });

        selectedImagesRecyclerViewAdapter = new SelectedImagesRecyclerViewAdapter(selectedImagesURIList);
        selectedImagesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        selectedImagesRecyclerView.setAdapter(selectedImagesRecyclerViewAdapter);

        ArrayAdapter<CharSequence> propertyTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.property_type_array, android.R.layout.simple_spinner_item);
        propertyTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        propertyTypeSpinner.setAdapter(propertyTypeAdapter);

        selectImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXST)) {

                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    startActivityForResult(intent, PICK_IMG);

                }
            }
        });

        postPropertyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postProperty();
            }
        });

    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMG) {
            if (resultCode == RESULT_OK && data != null) {
                if (data.getClipData() == null) {
                    selectedImagesURIList.add(data.getData());
                } else  {
                    int count = data.getClipData().getItemCount();

                    if (count > 10) {
                        Toast.makeText(this, "You can select upto 10 images only", Toast.LENGTH_SHORT).show();
                    }

                    int CurrentImageSelect = 0;

                    while (CurrentImageSelect < count && CurrentImageSelect < 10) {
                        Uri imageuri = data.getClipData().getItemAt(CurrentImageSelect).getUri();
                        selectedImagesURIList.add(imageuri);
                        CurrentImageSelect++;
                    }
                }
                selectedImagesRecyclerViewAdapter.notifyDataSetChanged();
            }

        }

    }

    private void postProperty() {

        final String propertyType = propertyTypeSpinner.getSelectedItem().toString();
        final String hnoStreetName = hnoStreetEditText.getText().toString();
        final String locality = localityEditText.getText().toString();
        final String city = cityEditText.getText().toString();

        Double price = 0.0;
        if(!priceEditText.getText().toString().isEmpty()) {
            price = Double.valueOf(priceEditText.getText().toString());
        }

        Double age = 0.0;
        if(!ageEditText.getText().toString().isEmpty()) {
            age = Double.valueOf(ageEditText.getText().toString());
        }

        final String otherDetails = otherDetailsEditText.getText().toString();

        if(validateInput(locality, city, price, age,  otherDetails)) {

            final ProgressDialog progressDialog = new ProgressDialog(AddPostActivity.this, R.style.MyAlertDialogStyle);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Posting Property...");
            progressDialog.show();


            final SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Prefs", MODE_PRIVATE);

            Map<String, Object> postMap = new HashMap<>();
            postMap.put("userID", firebaseAuth.getCurrentUser().getUid());
            postMap.put("buyOrSell", buyOrSellProperty);
            postMap.put("rent", rentProperty);
            postMap.put("propertyType", propertyType);
            postMap.put("hnoStreetName", hnoStreetName);
            postMap.put("locality", locality);
            postMap.put("city", city);
            postMap.put("cityLowercase", city.toLowerCase());
            postMap.put("price", price);
            postMap.put("age", age);
            postMap.put("otherDetails", otherDetails);
            postMap.put("userName", sharedPreferences.getString("userName", ""));
            postMap.put("userEmail", sharedPreferences.getString("userEmail", ""));
            postMap.put("userMobile", sharedPreferences.getString("userMobile", ""));
            postMap.put("userAddress", sharedPreferences.getString("userAddress", ""));
            postMap.put("timestamp", FieldValue.serverTimestamp());

            if(buyOrSellProperty.equals("buy")) {
                ArrayList<String> images = new ArrayList<>();
                images.add("none");
                postMap.put("images", images);
            }

            // Storing post details to firebase firestore
            firebaseFirestore.collection("Posts").add(postMap)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {

                            if(task.isSuccessful()) {

                                if(buyOrSellProperty.equals("sell")) {

                                    // Stroing images to firebase database
                                    final String documentId = task.getResult().getId();

                                    final ArrayList<String> uploadedImagesURLs = new ArrayList<>();

                                    StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child("PostImages").child(documentId);

                                    for (int upload_count = 0; upload_count < selectedImagesURIList.size(); upload_count++) {

                                        Uri IndividualImage = selectedImagesURIList.get(upload_count);
                                        final StorageReference ImageName = ImageFolder.child("Images" + IndividualImage.getLastPathSegment());

                                        ImageName.putFile(IndividualImage).addOnSuccessListener(
                                                new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                        ImageName.getDownloadUrl().addOnSuccessListener(
                                                                new OnSuccessListener<Uri>() {
                                                                    @Override
                                                                    public void onSuccess(Uri uri) {
                                                                        uploadedImagesURLs.add(String.valueOf(uri));

                                                                        if (uploadedImagesURLs.size() == selectedImagesURIList.size()) {

                                                                            // Adding images urls to post
                                                                            firebaseFirestore.collection("Posts").document(documentId).update("images", uploadedImagesURLs)
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {
                                                                                            Toast.makeText(AddPostActivity.this, "Property Posted", Toast.LENGTH_SHORT).show();
                                                                                            progressDialog.dismiss();
                                                                                            finish();
                                                                                        }
                                                                                    });
                                                                        }

                                                                    }
                                                                }
                                                        );
                                                    }
                                                }
                                        );
                                    }
                                } else {
                                    Toast.makeText(AddPostActivity.this, "Property Posted", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    finish();
                                }

                            } else {

                                Toast.makeText(AddPostActivity.this, "Error in posting property : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }

    }

    private boolean validateInput(String locality, String city, Double price, Double age, String otherDetails) {

        boolean ret = true;

        textInputLocality.setError(null);
        textInputCity.setError(null);
        textInputPrice.setError(null);
        textInputAge.setError(null);
        textInputOtherDetails.setError(null);

        if(TextUtils.isEmpty(city)) {
            textInputCity.setError("Please fill this field");
            ret = false;
        }

        if(TextUtils.isEmpty(otherDetails)) {
            textInputOtherDetails.setError("Please fill this field");
            ret = false;
        }

        if(buyOrSellProperty.equals("sell")) {

            if(selectedImagesURIList.size() == 0) {
                Toast.makeText(this, "Please select some pictures of your property", Toast.LENGTH_SHORT).show();
                ret = false;
            }

            if(TextUtils.isEmpty(locality)) {
                textInputLocality.setError("Please fill this field");
                ret = false;
            }

            if(price == 0.0) {
                textInputPrice.setError("Please fill this field");
                ret = false;
            }

            if(age == 0.0) {
                textInputAge.setError("Please fill this field");
                ret = false;
            }

        }

        return ret;
    }

    private boolean askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(AddPostActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(AddPostActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again

                ActivityCompat.requestPermissions(AddPostActivity.this, new String[]{permission}, requestCode);
                Toast.makeText(AddPostActivity.this, "Storage permission is required to select images.", Toast.LENGTH_LONG).show();

            } else {

                ActivityCompat.requestPermissions(AddPostActivity.this, new String[]{permission}, requestCode);
            }
            return false;
        } else {
            return true;
        }
    }

}