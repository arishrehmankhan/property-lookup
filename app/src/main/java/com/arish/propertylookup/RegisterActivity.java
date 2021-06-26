package com.arish.propertylookup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.arish.propertylookup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout textInputName;
    private TextInputLayout textInputEmail;
    private TextInputLayout textInputMobile;
    private TextInputLayout textInputAddress;
    private TextInputLayout textInputPassword;
    private TextInputLayout textInputConfirmPassword;

    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextMobile;
    private EditText editTextAddress;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private Button registerButton;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeStatusBarColor();
        setContentView(R.layout.activity_register);

        textInputName = findViewById(R.id.name_text_input);
        textInputEmail = findViewById(R.id.email_text_input);
        textInputMobile = findViewById(R.id.mobile_text_input);
        textInputAddress = findViewById(R.id.address_text_input);
        textInputPassword = findViewById(R.id.password_text_input);
        textInputConfirmPassword = findViewById(R.id.confirm_password_text_input);

        editTextName = findViewById(R.id.name_edit_text);
        editTextEmail = findViewById(R.id.email_edit_text);
        editTextMobile = findViewById(R.id.mobile_edit_text);
        editTextAddress = findViewById(R.id.address_edit_text);
        editTextPassword = findViewById(R.id.password_edit_text);
        editTextConfirmPassword = findViewById(R.id.confirm_password_edit_text);
        registerButton = findViewById(R.id.register_button);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.register_bk_color));
        }
    }

    public void onLoginClick(View view){
        startActivity(new Intent(this,LoginActivity.class));
        overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);
        finish();
    }

    private void registerUser() {

        final String name = editTextName.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        final String mobile = editTextMobile.getText().toString().trim();
        final String address = editTextAddress.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        if(validateInput(name, email, mobile, address, password, confirmPassword)) {

            final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this, R.style.MyAlertDialogStyle);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Signing up...");
            progressDialog.show();

            registerButton.setEnabled(false);

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (!task.isSuccessful()) {

                                Toast.makeText(RegisterActivity.this, "Sign up failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                            } else {

                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("id", firebaseAuth.getCurrentUser().getUid());
                                userMap.put("name", name);
                                userMap.put("email", email);
                                userMap.put("mobile", mobile);
                                userMap.put("address", address);
                                userMap.put("timestamp", FieldValue.serverTimestamp());

                                // save user details in sharedPreferences
                                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Prefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("userName", name);
                                editor.putString("userEmail", email);
                                editor.putString("userMobile", mobile);
                                editor.putString("userAddress", address);
                                editor.apply();

                                // save user details in database
                                firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid()).set(userMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful()) {

                                                firebaseAuth.getCurrentUser().sendEmailVerification();
                                                Toast.makeText(RegisterActivity.this, "Verify your email by clicking on the link sent to your email.\nThen login to the app.", Toast.LENGTH_LONG).show();
                                                registerButton.setEnabled(true);
                                                progressDialog.dismiss();
                                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                                finish();

                                            } else {

                                                Toast.makeText(RegisterActivity.this, "Error in saving data", Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                });

                            }

                            registerButton.setEnabled(true);
                            progressDialog.dismiss();

                        }
                    });

        }
    }

    private boolean validateInput(String name, String email, String mobile, String address, String password, String confirmPassword) {

        textInputName.setError(null);
        textInputEmail.setError(null);
        textInputMobile.setError(null);
        textInputAddress.setError(null);
        textInputPassword.setError(null);
        textInputConfirmPassword.setError(null);

        if(TextUtils.isEmpty(name)) {
            textInputName.setError("Please enter your name");
            return false;
        }

        if(TextUtils.isEmpty(email)) {
            textInputEmail.setError("Please enter your email");
            return false;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            textInputEmail.setError("Please enter a valid email");
            return false;
        }

        if(TextUtils.isEmpty(mobile)) {
            textInputMobile.setError("Please enter your mobile number");
            return false;
        }

        if(mobile.length() != 10) {
            textInputMobile.setError("Please enter valid mobile number");
            return false;
        }

        if(TextUtils.isEmpty(address)) {
            textInputMobile.setError("Please enter your current address");
            return false;
        }

        if(TextUtils.isEmpty(password)) {
            textInputPassword.setError("Please enter a password");
            return false;
        }

        if(password.length() < 6) {
            textInputPassword.setError("Password must be longer than 5 characters");
            return false;
        }

        if(TextUtils.isEmpty(confirmPassword)) {
            textInputConfirmPassword.setError("Please enter same password");
            return false;
        }

        if(!TextUtils.equals(password, confirmPassword)) {
            textInputConfirmPassword.setError("Confirm password should be same as password");
            return false;
        }

        return true;
    }

}
