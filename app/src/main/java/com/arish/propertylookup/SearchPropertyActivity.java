package com.arish.propertylookup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.arish.propertylookup.R;

import java.text.DecimalFormat;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class SearchPropertyActivity extends AppCompatActivity {

    private FrameLayout searchFrameLayout;
    private EditText searchLocationET;
    private CrystalRangeSeekbar rangeSeekbar;
    private TextView minPriceTV;
    private TextView maxPriceTV;
    private Spinner searchPropertyTypeSpinner;
    private CircularProgressButton searchButton;

    private double minPrice;
    private double maxPrice;

    private String minSuffix;
    private String maxSuffix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_property);

        changeStatusBarColor();

        searchFrameLayout = findViewById(R.id.search_frame_layout);

        // Setting Toolbar
        Toolbar toolbar = findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search Property");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchLocationET = findViewById(R.id.search_location_edit_text);

        // Setting Seekbar
        rangeSeekbar = findViewById(R.id.price_range_select);

        minPriceTV = findViewById(R.id.min_price_text_view);
        maxPriceTV = findViewById(R.id.max_price_text_view);

        rangeSeekbar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {

                int minValueInt = Integer.parseInt(minValue.toString());
                int maxValueInt = Integer.parseInt(maxValue.toString());

                minPrice = Double.parseDouble(minValue.toString());
                maxPrice = Double.parseDouble(maxValue.toString());

                minSuffix = "";
                maxSuffix = "";

                DecimalFormat minFormatter = new DecimalFormat("0");
                DecimalFormat maxFormatter = new DecimalFormat("0");

                if (minValueInt == 0) {
                    minPrice = 0.0;
                    minFormatter = new DecimalFormat("0");
                    minSuffix = "";
                } else if (minValueInt < 20) {
                    minPrice = 5 * (double) minValueInt;
                    minFormatter = new DecimalFormat("0");
                    minSuffix = " Lacs";
                } else if (minValueInt < 40) {
                    minPrice = (5 * (double) minValueInt) / 100;
                    minFormatter = new DecimalFormat("0.00");
                    minSuffix = " Crores";
                } else if (minValueInt < 70) {
                    minPrice = (5 * (double) 40) / 100 + (10 * (double) (minValueInt - 40)) / 100;
                    minFormatter = new DecimalFormat("0.00");
                    minSuffix = " Crores";
                } else if (minValueInt < 90) {
                    minPrice = (5 * (double) 100) / 100 + (25 * (double) (minValueInt - 70)) / 100;
                    minFormatter = new DecimalFormat("0.00");
                    minSuffix = " Crores";
                } else if (minValueInt < 100) {
                    minPrice = (5 * (double) 200) / 100 + (10 * (double) (minValueInt - 90));
                    minFormatter = new DecimalFormat("0");
                    minSuffix = " Crores";
                } else {
                    minPrice = (double) 100;
                    minSuffix = "+ Crores";
                }

                if (maxValueInt == 0) {
                    maxPrice = 0.0;
                    maxFormatter = new DecimalFormat("0");
                    maxSuffix = "";
                } else if (maxValueInt < 20) {
                    maxPrice = 5 * (double) maxValueInt;
                    maxFormatter = new DecimalFormat("0");
                    maxSuffix = " Lacs";
                } else if (maxValueInt < 40) {
                    maxPrice = (5 * (double) maxValueInt) / 100;
                    maxFormatter = new DecimalFormat("0.00");
                    maxSuffix = " Crores";
                } else if (maxValueInt < 70) {
                    maxPrice = (5 * (double) 40) / 100 + (10 * (double) (maxValueInt - 40)) / 100;
                    maxFormatter = new DecimalFormat("0.00");
                    maxSuffix = " Crores";
                } else if (maxValueInt < 90) {
                    maxPrice = (5 * (double) 100) / 100 + (25 * (double) (maxValueInt - 70)) / 100;
                    maxFormatter = new DecimalFormat("0.00");
                    maxSuffix = " Crores";
                } else if (maxValueInt < 100) {
                    maxPrice = (5 * (double) 200) / 100 + (10 * (double) (maxValueInt - 90));
                    maxFormatter = new DecimalFormat("0");
                    maxSuffix = " Crores";
                } else {
                    maxPrice = (double) 100;
                    maxSuffix = "+ Crores";
                }

                minPriceTV.setText(String.valueOf(minFormatter.format(minPrice)) + minSuffix);
                maxPriceTV.setText(String.valueOf(maxFormatter.format(maxPrice)) + maxSuffix);
            }
        });

        // Setting Spinner
        searchPropertyTypeSpinner = findViewById(R.id.search_property_type_spinner);

        ArrayAdapter<CharSequence> propertyTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.search_property_type_array, android.R.layout.simple_spinner_item);
        propertyTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchPropertyTypeSpinner.setAdapter(propertyTypeAdapter);

        // Search Button
        searchButton = findViewById(R.id.search_button);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.clear();
                bundle.putString("location", searchLocationET.getText().toString());

                double startPrice = 0.0;
                double endPrice = 0.0;

                if (minSuffix.equals("")) {
                    startPrice = minPrice;
                } else if (minSuffix.equals(" Lacs")) {
                    startPrice = minPrice * 100000;
                } else if (minSuffix.equals(" Crores")){
                    startPrice = minPrice * 10000000;
                } else {
                    startPrice = -1;
                }

                if (maxSuffix.equals("")) {
                    endPrice = maxPrice;
                } else if (maxSuffix.equals(" Lacs")) {
                    endPrice = maxPrice * 100000;
                } else if (maxSuffix.equals(" Crores")){
                    endPrice = maxPrice * 10000000;
                } else {
                    endPrice = -1;
                }
                bundle.putDouble("minPrice", startPrice);
                bundle.putDouble("maxPrice", endPrice);
                bundle.putString("propertyType", searchPropertyTypeSpinner.getSelectedItem().toString());

                SearchResultFragment searchResultFragment = new SearchResultFragment();
                searchResultFragment.setArguments(bundle);
                ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction().add(R.id.search_frame_layout,
                        searchResultFragment).addToBackStack(null).commit();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        getSupportActionBar().setTitle("Search Property");
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