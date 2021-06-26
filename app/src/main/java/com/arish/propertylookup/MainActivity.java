package com.arish.propertylookup;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.arish.propertylookup.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        firebaseAuth = FirebaseAuth.getInstance();

        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        bottomNavigationView.setSelectedItemId(R.id.home);

        changeStatusBarColor();

    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {

                        case R.id.user_posts:
                            // if user is not logged in goto login activity
                            if(firebaseAuth.getCurrentUser() == null || !firebaseAuth.getCurrentUser().isEmailVerified()) {
                                goToLoginActivity();
                                return false;
                            }
                            selectedFragment = new UserPostsFragment();
                            break;

                        case R.id.home:
                            selectedFragment = new HomeFragment();
                            break;

                        case R.id.profile:
                            // if user is not logged in goto login activity
                            if(firebaseAuth.getCurrentUser() == null || !firebaseAuth.getCurrentUser().isEmailVerified()) {
                                goToLoginActivity();
                                return false;
                            }
                            selectedFragment = new ProfileFragment();
                            break;

                        default:
                            selectedFragment = new HomeFragment();
                    }

                    // replacing fragment
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout,
                            selectedFragment).commit();

                    return true;
                }
            };

    private void goToLoginActivity() {

        Toast.makeText(MainActivity.this, "You need to login first!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));

    }

    // returns bottom navigation view, it is used in fragments - HomeFragment, UserPostsFragment, etc.
    public BottomNavigationView getNav() {
        return bottomNavigationView;
    }

}