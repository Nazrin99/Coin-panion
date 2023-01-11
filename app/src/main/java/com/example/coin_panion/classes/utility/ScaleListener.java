package com.example.coin_panion.classes.utility;

import android.view.ScaleGestureDetector;
import android.widget.ImageView;

public class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

    ImageView imageView;

    public ScaleListener(ImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    public boolean onScale(ScaleGestureDetector scaleGestureDetector){
        float scaleFactor = scaleGestureDetector.getScaleFactor();

        // Don't let the object get too small or too large.
        scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f));

        // Scale the view
        imageView.setScaleX(scaleFactor);
        imageView.setScaleY(scaleFactor);
        return true;
    }
}

