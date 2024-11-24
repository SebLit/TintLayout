package com.seblit.android.widget.tintlayout;

import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.Arrays;

/**
 * An {@link AnimatedTintFactory} that transitions between its colors in a swiping motion
 */
public class ColorSwipeTintFactory extends AnimatedTintFactory {

    private final int[] colors;
    private final Shader.TileMode mode;
    private final float colorBlendRange;
    private final float animationDistance;
    private final float progressPerColor;
    private final Interpolator swipeInterpolator;
    private final float angle;

    /**
     * Creates a new instance with the provided configurations
     *
     * @param animationConfig The {@link AnimationConfig AnimationConfig} for this factory
     * @param config The {@link Config} for this factory
     */
    public ColorSwipeTintFactory(@NonNull AnimationConfig animationConfig, @NonNull Config config) {
        super(animationConfig);
        this.colors = Arrays.copyOf(config.colors, config.colors.length);
        mode = config.mode;
        angle = config.angle;
        swipeInterpolator = config.swipeInterpolator;
        colorBlendRange = config.colorBlendRange;
        animationDistance = 1 + colorBlendRange;
        progressPerColor = 1f / (colors.length - 1);
    }

    /**
     * Creates a {@link Paint} with a {@link LinearGradient} shader that transitions its colors according to the animations progress and swipe interpolator<br>
     * {@inheritDoc}
     */
    @NonNull
    @Override
    protected Paint createTint(int width, int height, float progress, int completedLaps) {
        int currentIndex = (int) (progress * (colors.length - 1));
        int nextIndex = getNextColorIndex(currentIndex);
        int[] gradientColors = {colors[nextIndex], colors[nextIndex], colors[currentIndex], colors[currentIndex]};
        LinearGradientBounds bounds = new LinearGradientBounds(new RectF(0, 0, width, height), angle);
        float colorProgress = progress % progressPerColor / progressPerColor;
        float currentPosition = swipeInterpolator.getInterpolation(colorProgress) * animationDistance;
        float[] colorDistribution = {-colorBlendRange, currentPosition - colorBlendRange, currentPosition, animationDistance};
        LinearGradient gradient = new LinearGradient(bounds.startX, bounds.startY, bounds.endX, bounds.endY, gradientColors, colorDistribution, mode);
        Paint paint = new Paint();
        paint.setShader(gradient);
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
     * A configuration for {@link ColorSwipeTintFactory}
     */
    public static class Config {

        private final int[] colors;
        private float angle;
        private float colorBlendRange;
        private Shader.TileMode mode = Shader.TileMode.CLAMP;
        private Interpolator swipeInterpolator = new LinearInterpolator();

        /**
         * Creates a new instance with the provided colors. The colors will be loaded by {@link ContextCompat#getColor(Context, int)}
         *
         * @param colorResources The colors used
         * @param context        The {@link Context} used to load the colors
         */
        public Config(@NonNull Context context, @ColorRes int... colorResources) {
            this(Arrays.stream(colorResources).map(resource -> ContextCompat.getColor(context, resource)).toArray());
        }

        /**
         * Creates a new instance with the provided colors
         *
         * @param colors The colors used
         */
        public Config(@ColorInt int... colors) {
            this.colors = colors;
        }

        /**
         * Sets the angle in degrees for the swipe direction
         *
         * @param angle The desired angle in degrees
         * @return the config for method chaining
         */
        @NonNull
        public Config setAngle(float angle) {
            this.angle = angle;
            return this;
        }

        /**
         * Sets the {@link Shader.TileMode TileMode} that will be used for creating the {@link LinearGradient} shader. Default is {@link Shader.TileMode#CLAMP CLAMP}
         *
         * @return the config for method chaining
         */
        @NonNull
        public Config setMode(@NonNull Shader.TileMode mode) {
            this.mode = mode;
            return this;
        }

        /**
         * Sets the size of the blending distance between two colors within the {@link LinearGradient}.<br>
         * This value must be between 0 and 1. 0 results in a clean line in between the colors, 1 in a smooth transition covering the full size.
         *
         * @param colorBlendRange The blending range
         * @return the config for method chaining
         */
        @NonNull
        public Config setColorBlendRange(@FloatRange(from = 0, to = 1) float colorBlendRange) {
            this.colorBlendRange = colorBlendRange;
            return this;
        }

        /**
         * Sets the {@link Interpolator} that is used to interpolate the transition between two colors according to the animations progress. By default {@link LinearInterpolator}
         *
         * @param swipeInterpolator The desired {@link Interpolator}
         * @return the config for method chaining
         */
        @NonNull
        public Config setSwipeInterpolator(@NonNull Interpolator swipeInterpolator) {
            this.swipeInterpolator = swipeInterpolator;
            return this;
        }
    }

}
