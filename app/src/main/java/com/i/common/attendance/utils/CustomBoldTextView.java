package com.i.common.attendance.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * Created by macbookair on 11/03/17.
 */

public class CustomBoldTextView extends AppCompatTextView {

    public CustomBoldTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CustomBoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomBoldTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            try {
                Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "LatoBold.ttf");
                setTypeface(tf);
            } catch (Exception e) {
                // Font file missing or wrong path — falls back to system default font
                e.printStackTrace();
            }
        }
    }

}