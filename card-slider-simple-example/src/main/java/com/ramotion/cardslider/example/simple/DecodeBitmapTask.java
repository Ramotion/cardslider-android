package com.ramotion.cardslider.example.simple;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.DrawableRes;


class DecodeBitmapTask extends AsyncTask<Void, Void, Bitmap> {

    private final Resources resources;
    private final int bitmapResId;
    private final int reqWidth;
    private final int reqHeight;

    DecodeBitmapTask(Resources resources, @DrawableRes int bitmapResId, int reqWidth, int reqHeight) {
        this.resources = resources;
        this.bitmapResId = bitmapResId;
        this.reqWidth = reqWidth;
        this.reqHeight = reqHeight;
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, bitmapResId, options);

        final int width = options.outWidth;
        final int height = options.outHeight;

        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            int halfWidth = width / 2;
            int halfHeight = height / 2;

            while ( ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth )
                    && !isCancelled() )
            {
                inSampleSize *= 2;
            }
        }

        if (isCancelled()) {
            return null;
        }

        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeResource(resources, bitmapResId, options);
    }

}