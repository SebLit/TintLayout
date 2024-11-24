package com.seblit.android.widget.tintlayout;

import android.graphics.Paint;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

/**
 * Creates tints for {@link TintLayout}
 */
public interface TintFactory {

    /**
     * Called on the main Thread when a {@link TintLayout} requests a tint from this factory
     *
     * @param width  The width of the tinted area
     * @param height The height of the tinted area
     * @return the tint in form of a {@link Paint}
     */
    @MainThread
    @NonNull
    Paint createTint(int width, int height);

}
