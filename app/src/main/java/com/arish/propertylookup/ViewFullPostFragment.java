package com.arish.propertylookup;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.arish.propertylookup.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class ViewFullPostFragment extends Fragment {

    private String postID;
    private FirebaseFirestore firebaseFirestore;

    public ViewFullPostFragment() {
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
        View view =  inflater.inflate(R.layout.fragment_view_full_post, container, false);

        Bundle bundle = this.getArguments();

        // hide the bottom nav if user is not coming from searchResultFragment
        if(!bundle.getString("fromFragment").equals("searchResultFragment")) {
            if(getActivity() != null) {
                ((MainActivity)getActivity()).getNav().setVisibility(View.GONE);
            }
        }

        firebaseFirestore = FirebaseFirestore.getInstance();

        TextView buyOrSellTextView = view.findViewById(R.id.buy_or_sell_text_view);
        TextView price = view.findViewById(R.id.price_text_view);
        TextView propertyType = view.findViewById(R.id.property_type_text_view);
        TextView propertyAddress = view.findViewById(R.id.property_address);
        TextView age = view.findViewById(R.id.age_text_view);
        TextView otherDetails = view.findViewById(R.id.other_details_text_view);
        TextView userName = view.findViewById(R.id.user_name_text_view);
        TextView userEmail = view.findViewById(R.id.user_email_text_view);
        final TextView userMobile = view.findViewById(R.id.user_mobile_text_view);
        TextView userAddress = view.findViewById(R.id.user_address_text_view);

        TextView contactDetailsTV = view.findViewById(R.id.contact_details_text_view);
        LinearLayout contactDetailsLayout = view.findViewById(R.id.contact_details_layout);
        LinearLayout contactButtonLayout = view.findViewById(R.id.contact_buttons_layout);

        CircularProgressButton whatsappButton = view.findViewById(R.id.whatsapp_button);
        CircularProgressButton callButton = view.findViewById(R.id.call_button);

        LinearLayout deletePostButtonLayout = view.findViewById(R.id.delete_post_button_layout);
        CircularProgressButton deletePostButton = view.findViewById(R.id.delete_post_button);

        Toolbar toolbar = view.findViewById(R.id.view_full_post_toolbar);

        // on toolbar's back button press, remove this fragment
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((FragmentActivity) view.getContext()).getSupportFragmentManager().popBackStack();
            }
        });

        // setting images to imageSlider
        String images = bundle.getString("images");

        ArrayList<String> propertyImages = new Gson().fromJson(images, ArrayList.class);

        ArrayList<SlideModel> imageList = new ArrayList<>();

        // if buyOrSell value is 'sell' the imageslider will display the images posted by the user, else it will show the default image buy_property.jpg present in drawable folder
        if(bundle.getString("buyOrSell").equals("sell")) {

            for (int i = 0; i < propertyImages.size(); ++i) {

                imageList.add(new SlideModel(propertyImages.get(i), null, ScaleTypes.CENTER_CROP));

            }
            buyOrSellTextView.setText("For Sale");

        } else {

            imageList.add(new SlideModel(R.drawable.buy_property, null, ScaleTypes.CENTER_CROP));
            buyOrSellTextView.setText("Wanted");

        }

        ImageSlider imageSlider = view.findViewById(R.id.image_slider);
        imageSlider.setImageList(imageList, ScaleTypes.CENTER_CROP);

        // stop image slider from sliding
        imageSlider.stopSliding();

        DecimalFormat IndianCurrencyFormat = new DecimalFormat("##,##,###.00");

        // display price in indian currency format
        price.setText("₹ " + IndianCurrencyFormat.format(bundle.getDouble( "price")));

        if(bundle.getString("rent").equals("true")) {

            buyOrSellTextView.setText(buyOrSellTextView.getText() + " At Rent");
            price.setText(price.getText() + " / Month");

        }

        propertyType.setText(bundle.getString("propertyType"));


        if (bundle.getString("hnoStreetName").isEmpty()) {
            propertyAddress.setText(bundle.getString("locality") + ", " + bundle.getString("city"));
        } else {
            propertyAddress.setText(bundle.getString("hnoStreetName") + ", " + bundle.getString("locality") + ", " + bundle.getString("city"));
        }

        age.setText(bundle.getDouble("age") + " year(s)");

        otherDetails.setText(bundle.getString("otherDetails"));

        // if user is coming from userPostFragment, don't show the details of user who posted the property
        if (bundle.getString("fromFragment").equals("userPostFragment")) {

            postID = bundle.getString("postID");

            contactDetailsTV.setVisibility(View.GONE);
            contactDetailsLayout.setVisibility(View.GONE);
            contactButtonLayout.setVisibility(View.GONE);

            deletePostButtonLayout.setVisibility(View.VISIBLE);

            // show yes/no dialog if user press delete post button
            deletePostButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                }
            });

        } else {

            contactDetailsTV.setVisibility(View.VISIBLE);
            contactDetailsLayout.setVisibility(View.VISIBLE);
            contactButtonLayout.setVisibility(View.VISIBLE);

            deletePostButtonLayout.setVisibility(View.GONE);

            userName.setText(bundle.getString("userName"));
            userEmail.setText(bundle.getString("userEmail"));
            userAddress.setText(bundle.getString("userAddress"));

            final String userMobileNumber = bundle.getString("userMobile");

            userMobile.setText(userMobileNumber);

            whatsappButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String number = userMobileNumber.replace(" ", "").replace("+", "");

                        Intent sendIntent = new Intent("android.intent.action.MAIN");
                        sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
                        sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(number) + "@s.whatsapp.net");
                        v.getContext().startActivity(sendIntent);

                    } catch (Exception e) {
                        Log.e("Error: ", "ERROR_OPEN_MESSANGER" + e.toString());
                    }
                }
            });

            callButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + userMobileNumber));
                    v.getContext().startActivity(intent);
                }
            });
        }


        return view;
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:

                    // if user chooses yes, delete the post and remove this fragment
                    final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.MyAlertDialogStyle);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Deleting post...");
                    progressDialog.show();

                    firebaseFirestore.collection("Posts").document(postID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getContext(), "Post deleted", Toast.LENGTH_SHORT).show();
                            ((FragmentActivity) getContext()).getSupportFragmentManager().popBackStack();
                            progressDialog.dismiss();
                        }
                    });

                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };
}