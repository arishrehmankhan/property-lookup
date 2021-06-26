package com.arish.propertylookup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arish.propertylookup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private TextView userNameTV;
    private TextView userEmailTV;
    private TextView userMobileTV;
    private TextView userAddressTV;

    private LinearLayout logoutLayout;
    private LinearLayout feedbackLayout;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        Toolbar toolbar = view.findViewById(R.id.profile_toolbar);

        toolbar.setTitle("Your Profile");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).getNav().setSelectedItemId(R.id.home);
                getFragmentManager().beginTransaction().replace(R.id.main_frame_layout,
                        new HomeFragment()).commit();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        userNameTV = view.findViewById(R.id.user_name_text_view);
        userEmailTV = view.findViewById(R.id.user_email_text_view);
        userMobileTV = view.findViewById(R.id.user_mobile_text_view);
        userAddressTV = view.findViewById(R.id.user_address_text_view);

        feedbackLayout = view.findViewById(R.id.feedback_layout);
        logoutLayout = view.findViewById(R.id.logout_layout);

        final SharedPreferences sharedPreferences = getContext().getSharedPreferences("Prefs", MODE_PRIVATE);

        userNameTV.setText(getGreetings() + sharedPreferences.getString("userName", ""));
        userEmailTV.setText(sharedPreferences.getString("userEmail", ""));
        userMobileTV.setText(sharedPreferences.getString("userMobile", ""));
        userAddressTV.setText(sharedPreferences.getString("userAddress", ""));

        feedbackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), FeedbackActivity.class);
                intent.putExtra("userID", firebaseAuth.getCurrentUser().getUid());
                intent.putExtra("userName", sharedPreferences.getString("userName", ""));
                startActivity(intent);
            }
        });

        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                Toast.makeText(v.getContext(), "Logged out", Toast.LENGTH_SHORT).show();
                ((MainActivity)getActivity()).getNav().setSelectedItemId(R.id.home);
                getFragmentManager().beginTransaction().replace(R.id.main_frame_layout,
                        new HomeFragment()).addToBackStack(null).commit();
            }
        });

        return view;
    }

    private String getGreetings() {

        String greeting = "Good ";

        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if(timeOfDay < 12) {
            greeting += "Morning ";
        } else if(timeOfDay < 16) {
            greeting += "Afternoon ";
        } else if(timeOfDay < 21) {
            greeting += "Evening ";
        } else {
            greeting = "Hope you had a great day ";
        }

        return greeting;
    }

}