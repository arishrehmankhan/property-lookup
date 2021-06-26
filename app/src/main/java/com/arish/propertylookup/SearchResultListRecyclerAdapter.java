package com.arish.propertylookup;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.arish.propertylookup.R;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class SearchResultListRecyclerAdapter extends RecyclerView.Adapter<SearchResultListRecyclerAdapter.MyViewHolder> {

    private ArrayList<PropertyPostModelClass> postList;

    private Context context;

    @NonNull
    @Override
    public SearchResultListRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.property_list_card, parent, false);

        context = view.getContext();

        SearchResultListRecyclerAdapter.MyViewHolder myViewHolder = new SearchResultListRecyclerAdapter.MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultListRecyclerAdapter.MyViewHolder holder, final int position) {

        final String propertyType = postList.get(position).getPropertyType();
        final String buyOrSell = postList.get(position).getBuyOrSell();
        final String rent = postList.get(position).getRent();
        String mainImage = postList.get(position).getImages().get(0);
        final ArrayList<String> images = postList.get(position).getImages();
        final String hnoStreetName = postList.get(position).getHnoStreetName();
        final String locality = postList.get(position).getLocality();
        final String city = postList.get(position).getCity();
        final Double price = postList.get(position).getPrice();
        final Double age = postList.get(position).getAge();
        final String otherDetails = postList.get(position).getOtherDetails();
        final String userName = postList.get(position).getUserName();
        final String userEmail = postList.get(position).getUserEmail();
        final String userMobile = postList.get(position).getUserMobile();
        final String userAddress = postList.get(position).getUserAddress();

        // if post is about selling the property, glide loads the main image from server otherwise glide loads the default image buy_property.jpg from resource folder
        if(buyOrSell.equals("sell")) {
            Glide.with(context).load(mainImage).into(holder.propertyImageView);
            holder.buyOrSellTextView.setText("For Sale");
        } else {
            holder.propertyImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.buy_property));
            holder.buyOrSellTextView.setText("Wanted");
        }

        if(rent.equals("true")) {

            holder.rentTextView.setVisibility(View.VISIBLE);

        } else {

            holder.rentTextView.setVisibility(View.GONE);

        }

        holder.propertyTypeTextView.setText(propertyType);
        holder.propertyAddress.setText(locality + ", "  + city);

        DecimalFormat IndianCurrencyFormat = new DecimalFormat("##,##,###.00");

        holder.priceTextView.setText(IndianCurrencyFormat.format(price));

        holder.userNameTextView.setText("Posted by " + userName);

        holder.callImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + userMobile));
                context.startActivity(intent);
            }
        });

        holder.whatsappImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String number = userMobile.replace(" ", "").replace("+", "");

                    Intent sendIntent = new Intent("android.intent.action.MAIN");
                    sendIntent.setComponent(new ComponentName("com.whatsapp","com.whatsapp.Conversation"));
                    sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(number)+"@s.whatsapp.net");
                    context.startActivity(sendIntent);

                } catch(Exception e) {
                    Log.e("Error: ", "ERROR_OPEN_MESSANGER"+e.toString());
                }
            }
        });

        holder.postCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // convert image urls list to string using Gson() so that it can be passed to ViewFullPostFragment using bundle
                String propertyImages = new Gson().toJson(images);

                Bundle bundle = new Bundle();
                bundle.putString("propertyType", propertyType);
                bundle.putString("buyOrSell", buyOrSell);
                bundle.putString("rent", rent);
                bundle.putString("hnoStreetName", hnoStreetName);
                bundle.putString("locality", locality);
                bundle.putString("city", city);
                bundle.putDouble("price", price);
                bundle.putDouble("age", age);
                bundle.putString("otherDetails", otherDetails);
                bundle.putString("userName", userName);
                bundle.putString("userEmail", userEmail);
                bundle.putString("userMobile", userMobile);
                bundle.putString("userAddress", userAddress);
                bundle.putString("fromFragment", "searchResultFragment");
                bundle.putString("images", propertyImages);

                ViewFullPostFragment ViewFullPostFragment = new ViewFullPostFragment();
                ViewFullPostFragment.setArguments(bundle);
                ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.search_frame_layout,
                        ViewFullPostFragment).addToBackStack(null).commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public SearchResultListRecyclerAdapter(ArrayList<PropertyPostModelClass> postList) {
        this.postList = postList;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView propertyImageView;

        TextView propertyTypeTextView;
        TextView propertyAddress;
        TextView priceTextView;
        TextView userNameTextView;
        TextView buyOrSellTextView;
        TextView rentTextView;

        ImageView whatsappImageView;
        ImageView callImageView;

        CardView postCard;

        public MyViewHolder(View itemView) {
            super(itemView);

            propertyImageView = itemView.findViewById(R.id.property_image_view);

            propertyTypeTextView = itemView.findViewById(R.id.property_type_text_view);
            propertyAddress = itemView.findViewById(R.id.property_address);
            priceTextView = itemView.findViewById(R.id.price_text_view);
            userNameTextView = itemView.findViewById(R.id.user_name_text_view);
            buyOrSellTextView = itemView.findViewById(R.id.buy_or_sell_text_view);
            rentTextView = itemView.findViewById(R.id.rent_text_view);

            whatsappImageView = itemView.findViewById(R.id.whatsapp_image_view);
            callImageView = itemView.findViewById(R.id.call_image_view);

            postCard = itemView.findViewById(R.id.post_card);
        }
    }
}
