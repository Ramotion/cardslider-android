package com.ramotion.cardslider.examples.simple.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.DrawableRes;

import com.ramotion.cardslider.examples.simple.R;


public class DecodeBitmapTask extends AsyncTask<Void, Void, Bitmap> {

    private final BackgroundBitmapCache cache;
    private final Resources resources;
    private final int bitmapResId;
    private final int reqWidth;
    private final int reqHeight;

    public DecodeBitmapTask(Resources resources, @DrawableRes int bitmapResId, int reqWidth, int reqHeight) {
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
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        final Bitmap decodedBitmap = BitmapFactory.decodeResource(resources, bitmapResId, options);

        final Bitmap result;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            result = getRoundedCornerBitmap(decodedBitmap,
                    resources.getDimension(R.dimen.card_corner_radius), reqWidth, reqHeight);
            decodedBitmap.recycle();
        } else {
            result = decodedBitmap;
        }

        cache.addBitmapToBgMemoryCache(bitmapResId, result);
        return result;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float pixels, int width, int height) {
        final Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int sourceWidth = bitmap.getWidth();
        final int sourceHeight = bitmap.getHeight();

        float xScale = (float) width / bitmap.getWidth();
        float yScale = (float) height / bitmap.getHeight();
        float scale = Math.max(xScale, yScale);

        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        float left = (width - scaledWidth) / 2;
        float top = (height - scaledHeight) / 2;

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);

        final RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, pixels, pixels, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, null, targetRect, paint);

        return output;
    }

}