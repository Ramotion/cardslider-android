package com.ramotion.cardslider.example.simple;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ramotion.cardslider.CardSliderLayoutManager;
import com.ramotion.cardslider.SliderAdapter;

public class MainActivity extends AppCompatActivity {

    private final SliderAdapter sliderAdapter = new SliderAdapter();

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initSlider();
    }

    private void initSlider() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setAdapter(sliderAdapter);
        recyclerView.setLayoutManager(new CardSliderLayoutManager());
//        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

}
