package com.example.duplimage;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {

    ImageView imageView;
    TextView nameView, dateView, matchView;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.itemimageview);
        nameView = itemView.findViewById(R.id.name);
        dateView= itemView.findViewById(R.id.date);
        matchView = itemView.findViewById(R.id.matchResult);
    }
}
