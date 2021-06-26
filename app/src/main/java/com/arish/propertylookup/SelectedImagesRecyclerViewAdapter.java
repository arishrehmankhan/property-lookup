package com.arish.propertylookup;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.arish.propertylookup.R;

import java.util.ArrayList;

public class SelectedImagesRecyclerViewAdapter extends RecyclerView.Adapter<SelectedImagesRecyclerViewAdapter.MyViewHolder> {

    private ArrayList<Uri> uri;

    public SelectedImagesRecyclerViewAdapter(ArrayList<Uri> uri) {
        this.uri = uri;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.selected_images_list_card, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedImagesRecyclerViewAdapter.MyViewHolder holder, final int position) {

        holder.selectedImagesCardImageView.setImageURI(uri.get(position));

        holder.removeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uri.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,getItemCount());
            }
        });

    }

    @Override
    public int getItemCount() {
        return uri.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView selectedImagesCardImageView;
        ImageView removeImage;

        public MyViewHolder(View itemView) {
            super(itemView);

            selectedImagesCardImageView = itemView.findViewById(R.id.selectedImagesCardImageView);
            removeImage = itemView.findViewById(R.id.removeImage);
        }
    }

}
