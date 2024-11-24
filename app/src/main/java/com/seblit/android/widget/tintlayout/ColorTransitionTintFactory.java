package com.seblit.android.widget.tintlayout;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.graphics.Paint;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.Arrays;

/**
 * An {@link AnimatedTintFactory} that will smoothly transition between its colors
 *
 * @see ArgbEvaluator#evaluate(float, Object, Object)
 */
public class ColorTransitionTintFactory extends AnimatedTintFactory {

    private final int[] colors;
    private final float progressPerColor;
    private final ArgbEvaluator colorEvaluator = new ArgbEvaluator();
    private final Interpolator colorInterpolator;

    /**
     * Creates a new instance with the values of the provided configurations
     *
     * @param animationConfig The {@link AnimationConfig AnimationConfig} for this factory
     * @param config          The {@link Config} for this factory
     */
    public ColorTransitionTintFactory(@NonNull AnimationConfig animationConfig, @NonNull Config config) {
        super(animationConfig);
        this.colors = Arrays.copyOf(config.colors, config.colors.length);
        colorInterpolator = config.colorInterpolator;
        progressPerColor = 1f / (colors.length - 1);
    }

    /**
     * Creates a {@link Paint} with a color that represents the corresponding value between the current and next color according to the animations progress and the animations color interpolator.<br>
     * {@inheritDoc}
     */
    @NonNull
    @Override
    protected Paint createTint(int width, int height, float progress, int completedLaps) {
        int currentIndex = (int) (progress * (colors.length - 1));
        int nextIndex = getNextColorIndex(currentIndex);
        float colorProgress = progress % progressPerColor / progressPerColor;
        float interpolatedProgress = colorInterpolator.getInterpolation(colorProgress);
        int currentColor = (int) colorEvaluator.evaluate(interpolatedProgress, colors[currentIndex], colors[nextIndex]);
        Paint paint = new Paint();
        paint.setColor(currentColor);
        return paint;
    }

    private int getNextColorIndex(int currentIndex) {
        int nextIndex = currentIndex + 1;
        if (nextIndex == colors.length) {
            return 0;
        }
        return nextIndex;
    }

    /**
     * A configuration for {@link ColorTransitionTintFactory}
     */
    public static class Config {

        private final int[] colors;
        private Interpolator colorInterpolator = new LinearInterpolator();

        /**
         * Creates a new instance with the provided gradient colors and duration. The colors will be loaded by {@link ContextCompat#getColor(Context, int)}
         *
         * @param colorResources The colors used by the gradient
         * @param context        The {@link Context} used to load the colors
         */
        public Config(@NonNull Context context, @ColorRes int... colorResources) {
            this(Arrays.stream(colorResources).map(resource -> ContextCompat.getColor(context, resource)).toArray());
        }

        /**
         * Creates a new instance with the provided gradient colors and duration
         *
         * @param colors The colors used by the gradient
         */
        public Config(@ColorInt int... colors) {
            this.colors = colors;
        }

        /**
         * Sets the {@link Interpolator} that is used to interpolate the color according to the animations progress. By default {@link LinearInterpolator}
         *
         * @param colorInterpolator The desired {@link Interpolator}
         * @return the config for method chaining
         */
        @NonNull
        public Config setColorInterpolator(@NonNull Interpolator colorInterpolator) {
            this.colorInterpolator = colorInterpolator;
            return this;
        }
    }

}
