package com.ramotion.cardslider.example.simple;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

class SliderAdapter extends RecyclerView.Adapter<SliderCard> {

    private final ArrayList<Integer> content = new ArrayList<>();
    private final View.OnClickListener listener;

    SliderAdapter(int[] content, View.OnClickListener listener) {
        this.listener = listener;
        for (int aContent : content) {
            this.content.add(aContent);
        }
    }

    @Override
    public SliderCard onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.layout_slider_card, parent, false);

        if (listener != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClick(view);
                }
            });
        }

        return new SliderCard(view);
    }

    @Override
    public void onBindViewHolder(SliderCard holder, int position) {
        holder.setContent(content.get(position));
    }

    @Override
    public void onViewRecycled(SliderCard holder) {
        holder.clearContent();
    }

    @Override
    public int getItemCount() {
        return content.size();
    }

}
