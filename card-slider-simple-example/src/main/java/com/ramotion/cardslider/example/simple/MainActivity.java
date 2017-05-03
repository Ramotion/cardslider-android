package com.ramotion.cardslider.example.simple;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ramotion.cardslider.CardSliderLayoutManager;
import com.ramotion.cardslider.CardSnapHelper;

public class MainActivity extends AppCompatActivity {

    private final SliderAdapter sliderAdapter = new SliderAdapter();

    private RecyclerView recyclerView;
    private EditText etScroll;
    private EditText etDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initSlider();

        etScroll = (EditText) findViewById(R.id.et_scroll);
        etDelete = (EditText) findViewById(R.id.et_delete);

        ((Button)findViewById(R.id.button_scroll)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.smoothScrollToPosition(sliderAdapter.getItemCount() / 2);
            }
        });

        ((Button)findViewById(R.id.button_delete)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sliderAdapter.removeItem(sliderAdapter.getItemCount() / 2);
            }
        });

    }

    private void initSlider() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setAdapter(sliderAdapter);
        recyclerView.setLayoutManager(new CardSliderLayoutManager());
        recyclerView.scrollToPosition(10);
        new CardSnapHelper().attachToRecyclerView(recyclerView);
    }

}
