package com.ramotion.cardslider.examples.simple.cards;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ramotion.cardslider.examples.simple.R;

import androidx.recyclerview.widget.RecyclerView;

public class SliderAdapter extends RecyclerView.Adapter<SliderCard> {

    private final int count;
    private final int[] content;
    private final View.OnClickListener listener;

    public SliderAdapter(int[] content, int count, View.OnClickListener listener) {
        this.content = content;
        this.count = count;
        this.listener = listener;
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
        holder.setContent(content[position % content.length]);
    }

    @Override
    public void onViewRecycled(SliderCard holder) {
        holder.clearContent();
    }

    @Override
    public int getItemCount() {
        return count;
    }

}
