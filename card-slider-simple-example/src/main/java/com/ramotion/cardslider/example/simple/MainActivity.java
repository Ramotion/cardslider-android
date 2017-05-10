package com.ramotion.cardslider.example.simple;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.StyleRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.ramotion.cardslider.CardSliderLayoutManager;
import com.ramotion.cardslider.CardSnapHelper;

public class MainActivity extends AppCompatActivity {

    private final int[] pics = {R.drawable.p1, R.drawable.p2, R.drawable.p3, R.drawable.p4, R.drawable.p5};
    private final int[] maps = {R.drawable.map_1, R.drawable.map_2, R.drawable.map_3};
    private final String[] countries = {"GREECE", "FRANCE", "CHINA", "CANADA", "JAPAN"};
    private final String[] temperatures = {"8~21°C", "6~19°C", "5~17°C"};
    private final String[] places = {"Aegeana Sea", "Somewhere on Earth", "Another place"};
    private final String[] times = {"4.11~11.15    7:00~18:00", "3.15~9.15    8:00~16:00", "8.1~12.15    7:00~18:00"};

    private final SliderAdapter sliderAdapter = new SliderAdapter(pics, new OnCardClickListener());

    private RecyclerView recyclerView;
    private ImageSwitcher mapSwitcher;
    private TextSwitcher countrySwitcher;
    private TextSwitcher temperatureSwitcher;
    private TextSwitcher placeSwitcher;
    private TextSwitcher clockSwitcher;

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
        recyclerView.addOnScrollListener(new ScrollListener());

        new CardSnapHelper().attachToRecyclerView(recyclerView);

        countrySwitcher = (TextSwitcher) findViewById(R.id.ts_country);
        countrySwitcher.setFactory(new TextViewFactory(R.style.CountryTextView));
        countrySwitcher.setCurrentText(countries[0]);

        temperatureSwitcher = (TextSwitcher) findViewById(R.id.ts_temperature);
        temperatureSwitcher.setFactory(new TextViewFactory(R.style.TemperatureTextView));
        temperatureSwitcher.setCurrentText(temperatures[0]);

        placeSwitcher = (TextSwitcher) findViewById(R.id.ts_place);
        placeSwitcher.setFactory(new TextViewFactory(R.style.PlaceTextView));
        placeSwitcher.setCurrentText(places[0]);

        clockSwitcher = (TextSwitcher) findViewById(R.id.ts_clock);
        clockSwitcher.setFactory(new TextViewFactory(R.style.ClockTextView));
        clockSwitcher.setCurrentText(times[0]);

        mapSwitcher = (ImageSwitcher) findViewById(R.id.ts_map);
        mapSwitcher.setInAnimation(this, android.R.anim.fade_in);
        mapSwitcher.setOutAnimation(this, android.R.anim.fade_out);
        mapSwitcher.setFactory(new ImageViewFactory());
        mapSwitcher.setImageResource(maps[0]);
    }

    private class TextViewFactory implements  ViewSwitcher.ViewFactory {

        @StyleRes final int styleId;

        TextViewFactory(@StyleRes int styleId) {
            this.styleId = styleId;
        }

        @SuppressWarnings("deprecation")
        @Override
        public View makeView() {
            final TextView textView = new TextView(MainActivity.this);
            if (Build.VERSION.SDK_INT < 23) {
                textView.setTextAppearance(MainActivity.this, styleId);
            } else {
                textView.setTextAppearance(styleId);
            }

            return textView;
        }

    }

    private class ImageViewFactory implements ViewSwitcher.ViewFactory {
        @Override
        public View makeView() {
            final ImageView imageView = new ImageView(MainActivity.this);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            return imageView;
        }
    }

    private class ScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState != RecyclerView.SCROLL_STATE_IDLE) {
                return;
            }

            final CardSliderLayoutManager lm = ((CardSliderLayoutManager) recyclerView.getLayoutManager());
            final int pos = lm.getActiveCardPosition();
            if (pos != currentPosition) {
                int animH[] = new int[] {R.anim.slide_in_right, R.anim.slide_out_left};
                int animV[] = new int[] {R.anim.slide_in_top, R.anim.slide_out_bottom};

                if (pos < currentPosition) {
                    animH[0] = android.R.anim.slide_in_left;
                    animH[1] = android.R.anim.slide_out_right;

                    animV[0] = R.anim.slide_in_bottom;
                    animV[1] = R.anim.slide_out_top;
                }

                countrySwitcher.setInAnimation(MainActivity.this, animH[0]);
                countrySwitcher.setOutAnimation(MainActivity.this, animH[1]);
                countrySwitcher.setText(countries[pos]);

                temperatureSwitcher.setInAnimation(MainActivity.this, animH[0]);
                temperatureSwitcher.setOutAnimation(MainActivity.this, animH[1]);
                temperatureSwitcher.setText(temperatures[pos % temperatures.length]);

                placeSwitcher.setInAnimation(MainActivity.this, animV[0]);
                placeSwitcher.setOutAnimation(MainActivity.this, animV[1]);
                placeSwitcher.setText(places[pos % places.length]);

                clockSwitcher.setInAnimation(MainActivity.this, animV[0]);
                clockSwitcher.setOutAnimation(MainActivity.this, animV[1]);
                clockSwitcher.setText(times[pos % times.length]);

                mapSwitcher.setImageResource(maps[pos % maps.length]);

                currentPosition = pos;
            }
        }
    }

    private class OnCardClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            final CardSliderLayoutManager lm =  (CardSliderLayoutManager) recyclerView.getLayoutManager();

            if (lm.isSmoothScrolling()) {
                return;
            }

            final int activeCardPosition = lm.getActiveCardPosition();
            if (activeCardPosition == RecyclerView.NO_POSITION) {
                return;
            }

            final int clickedPosition = recyclerView.getChildAdapterPosition(view);
            if (clickedPosition != activeCardPosition) {
                return;
            }

            int imgId;
            switch (pics[activeCardPosition]) {
                case R.drawable.p1: imgId = R.drawable.p1_big; break;
                case R.drawable.p2: imgId = R.drawable.p2_big; break;
                case R.drawable.p3: imgId = R.drawable.p3_big; break;
                case R.drawable.p4: imgId = R.drawable.p4_big; break;
                case R.drawable.p5: imgId = R.drawable.p5_big; break;
                default: imgId = R.drawable.p1_big;
            }

            final Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
            intent.putExtra(DetailsActivity.BUNDLE_IMAGE_ID, imgId);

            if (Build.VERSION.SDK_INT < 21) {
                startActivity(intent);
            } else {
                final ActivityOptions options = ActivityOptions
                        .makeSceneTransitionAnimation(MainActivity.this, view, "shared");
                startActivity(intent, options.toBundle());
            }
        }
    }

}
