package com.ramotion.cardslider.example.simple;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

class SliderCard extends RecyclerView.ViewHolder {

    private final ImageView imageView;

    private static int viewWidth = 0;
    private static int viewHeight = 0;

    SliderCard(View itemView) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.image);
    }

    void setContent(@IdRes final int resId) {
        if (viewWidth == 0) {
            itemView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    viewWidth = itemView.getWidth();
                    viewHeight = itemView.getHeight();
                    loadBitmap(resId);
                }
            });
        } else {
            loadBitmap(resId);
        }
    }

    private void loadBitmap(@IdRes int resId) {
        Bitmap bitmap = BitmapFactory.decodeResource(itemView.getResources(), resId);
        Matrix matrix = new Matrix();
        RectF br = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF ir = new RectF(0, 0, viewWidth, viewHeight);
        matrix.setRectToRect(br, ir, Matrix.ScaleToFit.FILL);

        imageView.setImageBitmap(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true));
    }

}
