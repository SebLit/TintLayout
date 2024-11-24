package com.seblit.android.widget.tintlayout;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

/**
 * A {@link TintFactory} that will produce a {@link Paint} with a fix color
 */
public class ColorTintFactory implements TintFactory {

    private final Paint paint;

    /**
     * Creates a new instance with the provided color. The color is loaded using {@link ContextCompat#getColor(Context, int)}
     *
     * @param context       The {@link Context} that is used to load the color
     * @param colorResource The color resource
     * @param xferMode      The xfermode that should be used
     */
    public ColorTintFactory(@NonNull Context context, @ColorRes int colorResource, PorterDuff.Mode xferMode) {
        this(ContextCompat.getColor(context, colorResource), xferMode);
    }

    /**
     * Creates a new instance with the provided color.
     *
     * @param color    The color used by this factory
     * @param xferMode The xfermode that should be used
     */
    public ColorTintFactory(@ColorInt int color, PorterDuff.Mode xferMode) {
        this.paint = new Paint();
        paint.setColor(color);
        paint.setXfermode(new PorterDuffXfermode(xferMode));
    }

    @NonNull
    @Override
    public Paint createTint(int width, int height) {
        return paint;
    }
}