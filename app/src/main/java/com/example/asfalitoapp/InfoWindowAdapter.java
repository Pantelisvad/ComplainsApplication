package com.example.asfalitoapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

public class InfoWindowAdapter implements GoogleMap.InfoWindowAdapter {


    private final View mWindow;
    private Context mContext;

    public InfoWindowAdapter(Context context) {
        this.mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.info_window,null);
    }

    private void renderWindowText(Marker marker , View view){
        TextView tvTitle = (TextView) view.findViewById(R.id.info_text);
        ImageView imageView = (ImageView) view.findViewById(R.id.info_image);
        Picasso.get().load(marker.getSnippet()).fit().centerInside().into(imageView);
        tvTitle.setText(marker.getTitle());

    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowText(marker ,mWindow);

        return mWindow;
    }


    @Override
    public View getInfoContents(Marker marker) {
        renderWindowText(marker ,mWindow);

        return mWindow;
    }
}
