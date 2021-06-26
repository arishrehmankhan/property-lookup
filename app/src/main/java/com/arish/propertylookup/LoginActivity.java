package com.arish.propertylookup;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.arish.propertylookup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class LoginActivity extends AppCompatActivity {

    private TextInputLayout textInputEmail;
    private TextInputLayout textInputPassword;

    private EditText editTextEmail;
    private EditText editTextPassword;

    private TextView textViewForgotPassword;

    private Button loginButton;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeStatusBarColor();
        setContentView(R.layout.activity_login);

        textInputEmail = findViewById(R.id.email_text_input);
        textInputPassword = findViewById(R.id.password_text_input);

        editTextEmail = findViewById(R.id.email_edit_text);
        editTextPassword = findViewById(R.id.password_edit_text);

        textViewForgotPassword = findViewById(R.id.forgot_password_text_view);

        loginButton = findViewById(R.id.login_button);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogBox();
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

    public void onLoginClick(View View){
        startActivity(new Intent(this,RegisterActivity.class));
        overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
        finish();
    }

    private void loginUser() {

        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        if(validateInput(email, password)) {

            final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, R.style.MyAlertDialogStyle);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Logging in...");
            progressDialog.show();

            loginButton.setEnabled(false);

            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (!task.isSuccessful()) {

                        Toast.makeText(LoginActivity.this, "Incorrect email of password!", Toast.LENGTH_SHORT).show();

                        loginButton.setEnabled(true);
                        progressDialog.dismiss();

                    } else {

                        if (firebaseAuth.getCurrentUser().isEmailVerified()) {

                            firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid())
                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                                        Log.d("awer", "fdsa");


                                        loginButton.setEnabled(true);

                                        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Prefs", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("userName", documentSnapshot.get("name").toString());
                                        editor.putString("userEmail", documentSnapshot.get("email").toString());
                                        editor.putString("userMobile", documentSnapshot.get("mobile").toString());
                                        editor.putString("userAddress", documentSnapshot.get("address").toString());
                                        editor.apply();

                                        loginButton.setEnabled(true);
                                        progressDialog.dismiss();
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finish();
                                    }

                                }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    loginButton.setEnabled(true);
                                    progressDialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "Error in signing in: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });

                        } else {

                            Toast.makeText(LoginActivity.this, "Your email is not verified.\nPlease verify it to log in.", Toast.LENGTH_SHORT).show();
                            openEmailVerificationDialogBox();
                            loginButton.setEnabled(true);
                            progressDialog.dismiss();

                        }


                    }

                }
            });

        }

    }

    private boolean validateInput(String email, String password) {

        textInputEmail.setError(null);
        textInputPassword.setError(null);

        if(TextUtils.isEmpty(email)) {
            textInputEmail.setError("Please enter your email");
            return false;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            textInputEmail.setError("Please enter a valid email");
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

        return true;

    }

    private void openDialogBox() {

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.alertbox_forgot_password, null);
        final TextInputEditText emailInput = alertLayout.findViewById(R.id.editTextEmail);

        emailInput.setText(editTextEmail.getText());

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", null);
        alert.setPositiveButton("Send Link", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String email = emailInput.getText().toString();

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                    Toast.makeText(LoginActivity.this, "Please enter a valid email address!", Toast.LENGTH_LONG).show();

                } else {

                    final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, R.style.MyAlertDialogStyle);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Sending Link...");
                    progressDialog.show();

                    firebaseAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Failed to send reset email!\nReason: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                    progressDialog.dismiss();
                                }
                            });

                }

            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    private void openEmailVerificationDialogBox() {

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.alertbox_email_verification, null);
        final TextInputEditText emailInput = alertLayout.findViewById(R.id.editTextEmail);

        emailInput.setText(editTextEmail.getText());

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                firebaseAuth.signOut();
            }
        });

        alert.setPositiveButton("Send Verification Link", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String email = emailInput.getText().toString();

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                    Toast.makeText(LoginActivity.this, "Please enter a valid email address!", Toast.LENGTH_LONG).show();

                } else {

                    final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, R.style.MyAlertDialogStyle);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Sending Link...");
                    progressDialog.show();

                    firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "We have sent you instructions to verify your email.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Failed to send verification email!\nReason: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                            progressDialog.dismiss();
                        }
                    });

                }
                firebaseAuth.signOut();
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
