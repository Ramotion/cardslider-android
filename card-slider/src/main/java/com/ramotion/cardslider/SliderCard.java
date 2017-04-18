package com.ramotion.cardslider;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

class SliderCard extends RecyclerView.ViewHolder {

    private final TextView labelNumber;

    SliderCard(View itemView) {
        super(itemView);

        labelNumber = (TextView) itemView.findViewById(R.id.label_number);
    }

    void setContent(int content) {
        labelNumber.setText(Integer.toString(content));
    }

}
