package com.ramotion.cardslider.example.simple;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.DrawableRes;
import android.util.Log;


class DecodeBitmapTask extends AsyncTask<Void, Void, Bitmap> {

    private final BackgroundBitmapCache cache;
    private final Resources resources;
    private final int bitmapResId;
    private final int reqWidth;
    private final int reqHeight;

    DecodeBitmapTask(Resources resources, @DrawableRes int bitmapResId, int reqWidth, int reqHeight) {
        this.cache = BackgroundBitmapCache.getInstance();
        this.resources = resources;
        this.bitmapResId = bitmapResId;
        this.reqWidth = reqWidth;
        this.reqHeight = reqHeight;
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        Bitmap cachedBitmap = cache.getBitmapFromBgMemCache(bitmapResId);
        if (cachedBitmap != null) {
            Log.d("D", "bitmap from cache: " + bitmapResId);
           return cachedBitmap;
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, bitmapResId, options);

        final int width = options.outWidth;
        final int height = options.outHeight;

        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            int halfWidth = width / 2;
            int halfHeight = height / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth
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

        final Bitmap decodedBitmap = BitmapFactory.decodeResource(resources, bitmapResId, options);
        cache.addBitmapToBgMemoryCache(bitmapResId, decodedBitmap);

        return decodedBitmap;
    }

}