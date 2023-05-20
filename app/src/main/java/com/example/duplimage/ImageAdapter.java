package com.example.duplimage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private List<String> imageList;
    private List<Boolean> selectionList;
    private List<String> selectedImages;
    private MainActivity activity;


    public ImageAdapter(List<String> imageList, View.OnClickListener onClickListener) {
        this.imageList = imageList;
        selectionList = new ArrayList<>();
        selectedImages = new ArrayList<>();
        this.activity = activity;

        for (int i = 0; i < imageList.size(); i++) {
            selectionList.add(false);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imagePath = imageList.get(position);
        boolean isSelected = selectionList.get(position);

        // Load the image into ImageView using an image loading library like Glide or Picasso
        Glide.with(holder.itemView.getContext())
                .load(new File(imagePath))
                .into(holder.imageView);

        // Set the selection indicator visibility
        holder.selectionIndicator.setVisibility(isSelected ? View.VISIBLE : View.GONE);
    }


    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public List<String> getSelectedImages() {
        return selectedImages;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imageView;
        private ImageView selectionIndicator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            imageView = itemView.findViewById(R.id.imageView);
            selectionIndicator = itemView.findViewById(R.id.selectionIndicator);

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            boolean isSelected = !selectionList.get(position);
            selectionList.set(position, isSelected);
            selectionIndicator.setVisibility(isSelected ? View.VISIBLE : View.GONE);

            String imagePath = imageList.get(position);
            if (isSelected) {
                selectedImages.add(imagePath);
            } else {
                selectedImages.remove(imagePath);
            }
        }



    }
}
