package com.ramotion.cardslider.example.simple;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;

public class DetailsActivity extends AppCompatActivity {

    static String BUNDLE_IMAGE_ID = "BUNDLE_IMAGE_ID";

    private DecodeBitmapTask decodeBitmapTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        final int smallResId = getIntent().getIntExtra(BUNDLE_IMAGE_ID, -1);
        if (smallResId == -1) {
            finish();
            return;
        }

        ((ImageView)findViewById(R.id.image_view)).setImageResource(smallResId);

        int bigResId;
        switch (smallResId) {
            case R.drawable.p1: bigResId = R.drawable.p1_big; break;
            case R.drawable.p2: bigResId = R.drawable.p2_big; break;
            case R.drawable.p3: bigResId = R.drawable.p3_big; break;
            case R.drawable.p4: bigResId = R.drawable.p4_big; break;
            case R.drawable.p5: bigResId = R.drawable.p5_big; break;
            default: bigResId = R.drawable.p1_big;
        }

        final DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(metrics);

        decodeBitmapTask = new DecodeBitmapTask(getResources(), bigResId, metrics.widthPixels, metrics.heightPixels) {
            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                ((ImageView)findViewById(R.id.image_view)).setImageBitmap(bitmap);
            }
        };
        decodeBitmapTask.execute();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (isFinishing() && decodeBitmapTask != null) {
            decodeBitmapTask.cancel(true);
        }
    }
}
