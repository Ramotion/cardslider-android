package com.ramotion.cardslider.example.simple;

import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

class SliderCard extends RecyclerView.ViewHolder {

    private final ImageView imageView;

    SliderCard(View itemView) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.image);
    }

    void setContent(@DrawableRes final int resId) {
        imageView.setImageResource(resId);
    }

}
