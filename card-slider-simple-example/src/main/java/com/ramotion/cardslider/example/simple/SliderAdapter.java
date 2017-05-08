package com.ramotion.cardslider.example.simple;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class SliderAdapter extends RecyclerView.Adapter<SliderCard> {

    private final ArrayList<Integer> content = new ArrayList<>();

    public SliderAdapter() {
        content.add(R.drawable.p1);
        content.add(R.drawable.p2);
        content.add(R.drawable.p3);
        content.add(R.drawable.p4);
        content.add(R.drawable.p5);
        content.add(R.drawable.p6);
    }

    @Override
    public SliderCard onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.layout_slider_card, parent, false);

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

}
