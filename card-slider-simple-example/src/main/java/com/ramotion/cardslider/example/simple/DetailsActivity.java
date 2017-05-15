package com.ramotion.cardslider.example.simple;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.transition.Transition;
import android.util.DisplayMetrics;
import android.widget.ImageView;

public class DetailsActivity extends AppCompatActivity {

    static String BUNDLE_IMAGE_ID = "BUNDLE_IMAGE_ID";

    private ImageView imageView;
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

        imageView = (ImageView)findViewById(R.id.image);
        imageView.setImageResource(smallResId);

        if (Build.VERSION.SDK_INT < 21) {
            loadFullSizeBitmap(smallResId);
        } else {
            getWindow().getSharedElementEnterTransition().addListener(new Transition.TransitionListener() {
                @Override public void onTransitionStart(Transition transition) {
                    boolean isClosing = decodeBitmapTask != null;
                    if (isClosing) {
                        ((CardView)findViewById(R.id.card)).setRadius(15f);
                    }
                }
                @Override public void onTransitionPause(Transition transition) {}
                @Override public void onTransitionResume(Transition transition) {}
                @Override public void onTransitionEnd(Transition transition) { loadFullSizeBitmap(smallResId);}
                @Override public void onTransitionCancel(Transition transition) { loadFullSizeBitmap(smallResId);}
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (isFinishing() && decodeBitmapTask != null) {
            decodeBitmapTask.cancel(true);
        }
    }

    private void loadFullSizeBitmap(int smallResId) {
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
                imageView.setImageBitmap(bitmap);
            }
        };
        decodeBitmapTask.execute();
    }

}
