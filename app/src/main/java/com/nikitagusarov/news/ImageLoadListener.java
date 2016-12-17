package com.nikitagusarov.news;

import android.graphics.Bitmap;

/**
 * Created by mac on 17/12/2016.
 */
public interface ImageLoadListener {
    void onImageLoaded(Bitmap bitmap);
    void onError();
}
