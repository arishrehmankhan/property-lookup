package com.arish.propertylookup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.arish.propertylookup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hsalf.smilerating.SmileRating;

import java.util.HashMap;
import java.util.Map;

public class FeedbackActivity extends AppCompatActivity {

    private Toolbar toolbar;

    public EditText feedbackMessage;
    public Button feedbackSubmitBtn;
    public SmileRating smileRating;

    public String feedbackRatingText;
    public int feedbackRatingValue;

    public FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        changeStatusBarColor();

        toolbar = findViewById(R.id.feedback_toolbar);

        toolbar.setTitle("Feedback");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        firebaseFirestore = FirebaseFirestore.getInstance();

        feedbackMessage = findViewById(R.id.feedback_message);
        feedbackSubmitBtn = findViewById(R.id.submit_button);

        feedbackRatingText = "Not Selected";
        feedbackRatingValue = 0;

        smileRating = findViewById(R.id.smile_rating);
        smileRating.setOnSmileySelectionListener(new SmileRating.OnSmileySelectionListener() {
            @Override
            public void onSmileySelected(int smiley, boolean reselected) {

                switch (smiley) {
                    case SmileRating.BAD:
                        feedbackRatingText = "BAD";
                        feedbackRatingValue = 2;
                        break;

                    case SmileRating.GOOD:
                        feedbackRatingText = "GOOD";
                        feedbackRatingValue = 4;
                        break;


                    case SmileRating.GREAT:
                        feedbackRatingText = "GREAT";
                        feedbackRatingValue = 5;
                        break;


                    case SmileRating.OKAY:
                        feedbackRatingText = "OKAY";
                        feedbackRatingValue = 3;
                        break;


                    case SmileRating.TERRIBLE:
                        feedbackRatingText = "TERRIBLE";
                        feedbackRatingValue = 1;
                        break;

                }
            }


        });

        feedbackSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(feedbackMessage.getText().toString().equals("")){
                    feedbackMessage.setError("Please type something");
                    return;
                }

                final ProgressDialog progressDialog = new ProgressDialog(FeedbackActivity.this,
                        R.style.MyAlertDialogStyle);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Submitting...");
                progressDialog.show();

                Map<String, Object> postMap = new HashMap<>();
                postMap.put("userId", getIntent().getStringExtra("userID"));
                postMap.put("userName", getIntent().getStringExtra("userName"));
                postMap.put("feedbackMessage", feedbackMessage.getText().toString());
                postMap.put("feedbackRatingText", feedbackRatingText);
                postMap.put("feedbackRatingValue", feedbackRatingValue);

                firebaseFirestore.collection("Feedbacks").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){

                            Toast.makeText(FeedbackActivity.this, "Thanks for your valuable feedback", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(FeedbackActivity.this, MainActivity.class));

                        } else {

                            Toast.makeText(FeedbackActivity.this, "Some error occurred, please try again", Toast.LENGTH_SHORT).show();

                        }
                        progressDialog.dismiss();
                    }
                });
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
}