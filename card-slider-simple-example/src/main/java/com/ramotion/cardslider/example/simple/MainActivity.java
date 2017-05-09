package com.ramotion.cardslider.example.simple;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.StyleRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.ramotion.cardslider.CardSliderLayoutManager;
import com.ramotion.cardslider.CardSnapHelper;

public class MainActivity extends AppCompatActivity {

    private final String[] countries = {"GREECE", "FRANCE", "CHINA", "CANADA", "RUSSIA", "JAPAN"};
    private final String[] temperatures = {"8~21°C", "0~35°C", "-5~25°C", "-8~8°C", "-58~21°C", "9~22°C"};

    private final SliderAdapter sliderAdapter = new SliderAdapter();

    private RecyclerView recyclerView;
    private TextSwitcher countrySwitcher;
    private TextSwitcher temperatureSwitcher;

    private int currentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final int left = getResources().getDimensionPixelSize(R.dimen.active_card_left);
        final int width = getResources().getDimensionPixelSize(R.dimen.active_card_width);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setAdapter(sliderAdapter);
        recyclerView.setLayoutManager(new CardSliderLayoutManager(left, width));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                MainActivity.this.onScrollStateChanged(recyclerView, newState);
            }
        });

        new CardSnapHelper().attachToRecyclerView(recyclerView);

        countrySwitcher = (TextSwitcher) findViewById(R.id.ts_country);
        countrySwitcher.setFactory(new SwitcherFactory(R.style.CountryTextView));
        countrySwitcher.setCurrentText(countries[0]);

        temperatureSwitcher = (TextSwitcher) findViewById(R.id.ts_temperature);
        temperatureSwitcher.setFactory(new SwitcherFactory(R.style.TemperatureTextView));
        temperatureSwitcher.setCurrentText(temperatures[0]);
    }

    private void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            final CardSliderLayoutManager lm = ((CardSliderLayoutManager) recyclerView.getLayoutManager());
            final int pos = lm.getActiveCardPosition();
            Log.d("D", "current position is: " + pos);
            if (pos != currentPosition) {
                int anim[] = new int[] {R.anim.slide_in_right, R.anim.slide_out_left};
                if (pos < currentPosition) {
                    anim[0] = android.R.anim.slide_in_left;
                    anim[1] = android.R.anim.slide_out_right;
                }

                countrySwitcher.setInAnimation(this, anim[0]);
                countrySwitcher.setOutAnimation(this, anim[1]);
                countrySwitcher.setText(countries[pos]);

                temperatureSwitcher.setInAnimation(this, anim[0]);
                temperatureSwitcher.setOutAnimation(this, anim[1]);
                temperatureSwitcher.setText(temperatures[pos]);

                currentPosition = pos;
            }
        }

    }

    private class SwitcherFactory implements  ViewSwitcher.ViewFactory {

        final int styleId;

        SwitcherFactory(@StyleRes int styleId) {
            this.styleId = styleId;
        }

        @Override
        public View makeView() {
            final TextView textView = new TextView(MainActivity.this);
            textView.setTextAppearance(MainActivity.this, styleId);
            return textView;
        }

    }

}
