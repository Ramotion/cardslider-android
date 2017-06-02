package com.ramotion.cardslider.example.simple;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * LruCache for caching background bitmaps for {@link DecodeBitmapTask}.
 */
class BackgroundBitmapCache {
    private LruCache<Integer, Bitmap> mBackgroundsCache;

    private static BackgroundBitmapCache instance;

    public static BackgroundBitmapCache getInstance() {
        if (instance == null) {
            instance = new BackgroundBitmapCache();
            instance.init();
        }
        return instance;
    }

    private void init() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 5;

        mBackgroundsCache = new LruCache<Integer, Bitmap>(cacheSize) {
            @Override
            protected void entryRemoved(boolean evicted, Integer key, Bitmap oldValue, Bitmap newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
            }

            @Override
            protected int sizeOf(Integer key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public void addBitmapToBgMemoryCache(Integer key, Bitmap bitmap) {
        if (getBitmapFromBgMemCache(key) == null) {
            mBackgroundsCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromBgMemCache(Integer key) {
        return mBackgroundsCache.get(key);
    }

}
