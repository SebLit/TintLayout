package com.seblit.android.widget.tintlayout;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

/**
 * An {@link AnimatedTintFactory} that runs a shimmer animation over its target
 */
public class ShimmerTintFactory extends AnimatedTintFactory {

    private final float angle;
    private final float shimmerSize;
    private final float animationDistance;
    private final float fadeDistance;
    private final int color;
    private final Shader.TileMode mode;

    /**
     * Creates a new instance with the values of the provided configurations
     *
     * @param animationConfig The {@link AnimationConfig AnimationConfig} for this factory
     * @param config          The {@link Config }for this factory
     */
    public ShimmerTintFactory(@NonNull AnimationConfig animationConfig, @NonNull Config config) {
        super(animationConfig);
        angle = config.angle;
        shimmerSize = config.size;
        animationDistance = 1 + shimmerSize;
        fadeDistance = config.fadeRange * (shimmerSize / 2);
        color = config.color;
        mode = config.mode;
    }

    /**
     * Creates a {@link Paint} with a {@link LinearGradient} shader that positions the configured shimmer according to the animations progress<br>
     * {@inheritDoc}
     */
    @NonNull
    @Override
    protected Paint createTint(int width, int height, float progress, int completedLaps) {
        LinearGradientBounds bounds = new LinearGradientBounds(new RectF(0, 0, width, height), angle);
        float shimmerEnd = progress * animationDistance;
        float shimmerStart = shimmerEnd - shimmerSize;
        float solidStart = shimmerStart + fadeDistance;
        float solidEnd = shimmerEnd - fadeDistance;
        int[] gradientColors = {Color.TRANSPARENT, Color.TRANSPARENT, color, color, Color.TRANSPARENT, Color.TRANSPARENT};
        float[] colorDistribution = {-shimmerSize, shimmerStart, solidStart, solidEnd, shimmerEnd, shimmerSize};
        LinearGradient gradient = new LinearGradient(bounds.startX, bounds.startY, bounds.endX, bounds.endY, gradientColors, colorDistribution, mode);
        Paint paint = new Paint();
        paint.setShader(gradient);
        return paint;
    }

    /**
     * A configuration for {@link ShimmerTintFactory}
     */
    public static class Config {

        private final float size;
        private final int color;
        private float fadeRange = 1;
        private float angle;
        private Shader.TileMode mode = Shader.TileMode.CLAMP;

        /**
         * Creates a new instance with the provided size and colors. The colors will be loaded by {@link ContextCompat#getColor(Context, int)}.
         *
         * @param size          The size of the shimmer relative to its target. Value must be between 0 and 1 and is represented in percentages
         * @param colorResource The color used
         * @param context       The {@link Context} used to load the color
         */
        public Config(@FloatRange(from = 0, to = 1) float size, @NonNull Context context, @ColorRes int colorResource) {
            this(size, ContextCompat.getColor(context, colorResource));
        }

        /**
         * Creates a new instance with the provided size and colors. The colors will be loaded by {@link ContextCompat#getColor(Context, int)}.
         *
         * @param size  The size of the shimmer relative to its target. Value must be between 0 and 1 and is represented in percentages
         * @param color The color used
         */
        public Config(@FloatRange(from = 0, to = 1) float size, @ColorInt int color) {
            this.size = size;
            this.color = color;
        }

        /**
         * Sets the range of the shimmers fade (alpha start to full color) relative to its size.<br>
         * Setting this to 0 results in a fully solid shimmer. Settings this to 1 results in a shimmer that is only solid at its center and fades to both sides from its center.
         *
         * @param fadeRange The fade range of the shimmer, relative to its size. Value must be between 0 and 1 and is represented in percentages.
         * @return the {@link Config} for method chaining
         */
        @NonNull
        public Config setFadeRange(@FloatRange(from = 0, to = 1) float fadeRange) {
            this.fadeRange = fadeRange;
            return this;
        }

        /**
         * Sets the shimmer angle in degrees.
         *
         * @param angle The desired angle in degrees
         * @return the {@link Config} for method chaining
         */
        @NonNull
        public Config setAngle(float angle) {
            this.angle = angle;
            return this;
        }
    }

}
