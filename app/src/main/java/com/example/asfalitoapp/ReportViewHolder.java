package com.example.asfalitoapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ReportViewHolder extends RecyclerView.ViewHolder {

    TextView textView;
    ImageView imageView;

    public ReportViewHolder(View itemview) {
        super(itemview);

       textView = (TextView)itemview.findViewById(R.id.desccard);
       imageView = (ImageView)itemview.findViewById(R.id.image_view_upload);


    }
}
