package com.ramotion.cardslider.example.simple;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class SliderAdapter extends RecyclerView.Adapter<SliderCard> {

    private final int COUNT = 40;
    private final ArrayList<Integer> content = new ArrayList<>(COUNT);

    public SliderAdapter() {
        for (int i = 0; i < COUNT; i++) {
            content.add(i);
        }
    }

    @Override
    public SliderCard onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(com.ramotion.cardslider.R.layout.layout_slider_card, parent, false);

        return new SliderCard(view);
    }

    @Override
    public void onBindViewHolder(SliderCard holder, int position) {
        holder.setContent(content.get(position));
    }

    @Override
    public int getItemCount() {
        return content.size();
    }

    public void removeItem(int position) {
        if (position < 0 || position >= getItemCount()) {
            return;
        }

        content.remove(position);
        notifyItemRemoved(position);
    }

}
