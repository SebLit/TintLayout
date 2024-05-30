package com.litts.android.widget.tintlayout;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;

/**
 * A {@link FrameLayout} that will draw a {@link Paint} over its children to tint them.<br>
 * Tints are provided by a {@link TintFactory}. Animated tints are supported through {@link AnimatedTintFactory}.<br>
 * This View sets its layer type to {@link #LAYER_TYPE_HARDWARE} and is rendered through androids hardware pipeline.
 */
public class TintLayout extends FrameLayout implements AnimatedTintFactory.ViewInvalidationListener {

    private TintFactory tintFactory;
    private boolean isCurrentFactoryAnimated;

    public TintLayout(@NonNull Context context) {
        super(context);
    }

    public TintLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TintLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public TintLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setWillNotDraw(false);
        setLayerType(LAYER_TYPE_HARDWARE, new Paint());
    }

    /**
     * Sets the {@link TintFactory} that will be used for tinting children.<br>
     * Set this to null to disable tinting.<br>
     * Must be called on the main thread
     *
     * @param tintFactory The desired {@link TintFactory} or null if no tinting is desired
     */
    @MainThread
    public void setTintFactory(@Nullable TintFactory tintFactory) {
        if (isCurrentFactoryAnimated) {
            ((AnimatedTintFactory) this.tintFactory).removeInvalidationListener(this);
        }
        this.tintFactory = tintFactory;
        isCurrentFactoryAnimated = this.tintFactory instanceof AnimatedTintFactory;
        if (isCurrentFactoryAnimated) {
            ((AnimatedTintFactory) this.tintFactory).addInvalidationListener(this);
        }
        invalidate();
    }

    /**
     * Draws the tint after drawing its children.
     * {@inheritDoc}
     *
     * @see #tint(Canvas)
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        tint(canvas);
    }

    /**
     * Called in {@link #dispatchDraw(Canvas)} to apply the tint.<br>
     * If no {@link TintFactory} is set, this method has no effect. If the {@link TintFactory} is a {@link AnimatedTintFactory#isRunning() running} {@link AnimatedTintFactory} this method will
     * invalidate the View after applying the tint to schedule a redraw of the animated tint.
     *
     * @param canvas The {@link Canvas} to apply the tint to.
     */
    protected void tint(@NonNull Canvas canvas) {
        if (tintFactory != null) {
            Paint paint = tintFactory.createTint(getWidth(), getHeight());
            canvas.drawPaint(paint);
            if (isCurrentFactoryAnimated && ((AnimatedTintFactory) tintFactory).isRunning()) {
                invalidate();
            }
        }
    }
}
