package com.seblit.android.widget.tintlayout;

import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.Xfermode;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.Arrays;

/**
 * A {@link TintFactory} supporting {@link LinearGradient}, {@link RadialGradient} and {@link SweepGradient}
 */
public class GradientTintFactory implements TintFactory {

    private final Type type;
    private final int[] colors;
    private final Shader.TileMode mode;
    private final float[] distribution;
    private final float angle;
    private final Xfermode xfermode;

    /**
     * Creates a new instance with the animation data of the provided {@link Config}
     */
    public GradientTintFactory(@NonNull Config config) {
        this.type = config.type;
        this.colors = Arrays.copyOf(config.colors, config.colors.length);
        this.mode = config.mode;
        this.distribution = config.distribution != null ? Arrays.copyOf(config.distribution, config.distribution.length) : null;
        angle = config.angle;
        if (config.xferMode != null) {
            xfermode = new PorterDuffXfermode(config.xferMode);
        } else {
            xfermode = null;
        }
    }

    /**
     * Creates a {@link Paint} with the configured gradient as a shader.<br>
     * {@inheritDoc}
     */
    @NonNull
    @Override
    @MainThread
    public Paint createTint(int width, int height) {
        Shader gradientShader;
        float halfWidth = width / 2;
        float halfHeight = height / 2;
        switch (type) {
            case RADIAL:
                float radius = Math.max(halfWidth, halfHeight);
                gradientShader = new RadialGradient(halfWidth, halfHeight, radius, colors, distribution, mode);
                break;
            case LINEAR:
                LinearGradientBounds bounds = new LinearGradientBounds(new RectF(0, 0, width, height), angle);
                gradientShader = new LinearGradient(bounds.startX, bounds.startY, bounds.endX, bounds.endY, colors, distribution, mode);
                break;
            case SWEEP:
            default:
                gradientShader = new SweepGradient(halfWidth, halfHeight, colors, distribution);
                break;
        }
        Paint paint = new Paint();
        paint.setShader(gradientShader);
        paint.setXfermode(xfermode);
        return paint;
    }

    /**
     * A configuration for {@link GradientTintFactory}
     */
    public static class Config {
        private Type type = Type.LINEAR;
        private final int[] colors;
        private Shader.TileMode mode = Shader.TileMode.CLAMP;
        private float[] distribution;
        private float angle;
        private PorterDuff.Mode xferMode = PorterDuff.Mode.SRC_ATOP;

        /**
         * Creates a new instance with the provided gradient colors. The colors will be loaded by {@link ContextCompat#getColor(Context, int)}
         *
         * @param colorResources The colors used by the gradient
         * @param context        The {@link Context} used to load the colors
         */
        public Config(@NonNull Context context, @ColorRes int... colorResources) {
            this(Arrays.stream(colorResources)
                    .map(color -> ContextCompat.getColor(context, color))
                    .toArray());
        }

        /**
         * Creates a new instance with the provided gradient colors
         *
         * @param colors The colors used by the gradient
         */
        public Config(@ColorInt int... colors) {
            this.colors = colors;
        }

        /**
         * Sets the gradients {@link Type}. Default is {@link Type#LINEAR LINEAR}
         *
         * @param type The desired {@link Type}
         * @return the {@link Config} for method chaining
         */
        @NonNull
        public Config setType(@NonNull Type type) {
            this.type = type;
            return this;
        }

        /**
         * Sets the {@link Shader.TileMode TileMode} used by {@link Type#LINEAR} and {@link Type#RADIAL}. Default is {@link Shader.TileMode#CLAMP CLAMP}
         *
         * @param mode The desired {@link Shader.TileMode TileMode}
         * @return the {@link Config} for method chaining
         */
        @NonNull
        public Config setTileMode(@NonNull Shader.TileMode mode) {
            this.mode = mode;
            return this;
        }

        /**
         * Sets the distribution of the gradients colors. null to evenly distribute the colors. This takes effect as
         * <li>{@code positions} for {@link LinearGradient#LinearGradient(float, float, float, float, int[], float[], Shader.TileMode) LinearGradient}
         * and {@link SweepGradient#SweepGradient(float, float, int[], float[]) SweepGradient}</li>
         * <li>{@code stops} for {@link RadialGradient#RadialGradient(float, float, float, int[], float[], Shader.TileMode) RadialGradient}</li>
         *
         * @param distribution The distribution of the colors within the gradient
         * @return the {@link Config} for method chaining
         */
        @NonNull
        public Config setDistribution(@Nullable float... distribution) {
            this.distribution = distribution;
            return this;
        }

        /**
         * Sets the rotation angle in degrees. Only used by {@link Type#LINEAR}
         *
         * @param angle The desired angle in degrees
         * @return the {@link Config} for method chaining
         */
        @NonNull
        public Config setAngle(float angle) {
            this.angle = angle;
            return this;
        }

        /**
         * Sets the xfer mode for the factories {@link Paint}. {@link PorterDuff.Mode#SRC_ATOP SRC_ATOP} by default.
         *
         * @param xferMode The desired xfer mode
         * @return the {@link Config} for method chaining
         */
        public Config setXferMode(@NonNull PorterDuff.Mode xferMode) {
            this.xferMode = xferMode;
            return this;
        }
    }

    /**
     * Gradient types supported by {@link GradientTintFactory}
     */
    public enum Type {
        /**
         * @see RadialGradient
         */
        RADIAL,
        /**
         * @see LinearGradient
         */
        LINEAR,
        /**
         * @see SweepGradient
         */
        SWEEP
    }

}