package com.ramotion.cardslider.example.simple;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class DetailsActivity extends AppCompatActivity {

    static String BUNDLE_IMAGE_ID = "BUNDLE_IMAGE_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        final int imgId = getIntent().getIntExtra(BUNDLE_IMAGE_ID, -1);
        if (imgId == -1) {
            finish();
            return;
        }

        ((ImageView)findViewById(R.id.image_view)).setImageResource(imgId);
    }
}
