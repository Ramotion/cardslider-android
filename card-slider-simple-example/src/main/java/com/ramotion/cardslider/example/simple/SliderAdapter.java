package com.ramotion.cardslider.example.simple;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SliderAdapter extends RecyclerView.Adapter<SliderCard> {

    @Override
    public SliderCard onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(com.ramotion.cardslider.R.layout.layout_slider_card, parent, false);

        return new SliderCard(view);
    }

    @Override
    public void onBindViewHolder(SliderCard holder, int position) {
        holder.setContent(position);
    }

    @Override
    public int getItemCount() {
        return 100;
    }

}
