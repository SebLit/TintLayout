package com.litts.android.widget.tintlayout;

import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link TintFactory} that supports changes in its tint over time, allowing for animated tints
 */
public abstract class AnimatedTintFactory implements TintFactory {

    protected final long duration;
    protected final EndBehaviour endBehaviour;
    protected final Interpolator animationInterpolator;
    private final List<WeakReference<ViewInvalidationListener>> invalidationListenerPool = new ArrayList<>();
    private final List<AnimationListener> animationListenerPool = new ArrayList<>();
    private final Xfermode defaultXferMode;

    private long startTime;
    private long pauseDuration;
    private int completedLaps;
    private boolean isFinished = true;

    /**
     * Creates a new instance with the values from the provided config
     *
     * @param config The {@link AnimationConfig} containing the animations values
     */
    public AnimatedTintFactory(@NonNull AnimationConfig config) {
        this.duration = config.duration;
        this.endBehaviour = config.endBehaviour;
        this.animationInterpolator = config.animationInterpolator;
        if (config.defaultXferMode != null) {
            defaultXferMode = new PorterDuffXfermode(config.defaultXferMode);
        } else {
            defaultXferMode = null;
        }
    }

    /**
     * Calculates the current progress and passed laps, then calls {@link #createTint(int, int, float, int)} to obtain the tint for the current animation state.<br>
     * If a lap has finished all currently registered {@link AnimationListener}s will be notified about it.<br>
     * If the tint doesn't have an xfer mode specified and a default xfer mode is specified by the config, the default mode will be used.<br>
     * {@inheritDoc}
     */
    @NonNull
    @MainThread
    @Override
    public final Paint createTint(int width, int height) {
        long elapsedTime = getElapsedTime();
        int completedLaps = (int) (elapsedTime / duration);
        float progress = calculateElapsedDuration(elapsedTime, completedLaps) / duration;
        float animationProgress = animationInterpolator.getInterpolation(progress);
        Paint tint = createTint(width, height, animationProgress, completedLaps);
        if (completedLaps > this.completedLaps) {
            this.completedLaps = completedLaps;
            isFinished = endBehaviour != EndBehaviour.LOOP;
            notifyAnimationListeners();
        }
        if (tint.getXfermode() == null && defaultXferMode != null) {
            tint.setXfermode(defaultXferMode);
        }
        return tint;
    }

    /**
     * Called by this factories {@link #createTint(int, int)} on the main Thread.
     *
     * @param width         The width of the tinted area
     * @param height        The height of the tinted area
     * @param progress      The progress of the animation
     * @param completedLaps The number of completed laps (starting at 0). Only relevant for looping animations.
     * @return the tint in form of a {@link Paint}
     */
    @MainThread
    @NonNull
    protected abstract Paint createTint(int width, int height, float progress, int completedLaps);

    /**
     * Launches the animation if not yet started or resumes it if currently paused.<br>
     * Previously started animations must be {@link #reset()} before they can be started again.<br>
     * Must be called on the main Thread
     */
    @MainThread
    public final void start() {
        isFinished = false;
        if (isPaused()) {
            startTime = System.currentTimeMillis() - pauseDuration;
            pauseDuration = 0;
            invalidateViews();
        } else if (!isRunning()) {
            startTime = System.currentTimeMillis();
            invalidateViews();
        }
    }

    /**
     * Cancels any ongoing animations and resets it to the start position.<br>
     * Must be called on main Thread
     */
    @MainThread
    public final void reset() {
        isFinished = true;
        startTime = 0;
        pauseDuration = 0;
        completedLaps = 0;
        invalidateViews();
    }

    /**
     * Pauses the animation if currently running. Can be resumed by calling {@link #start()}.<br>
     * Must be called on main Thread
     */
    @MainThread
    public final void pause() {
        if (isRunning()) {
            pauseDuration = getElapsedTime();
        }
    }

    /**
     * @return true if this animation has been started and is currently either running, paused or finished
     */
    public final boolean wasStarted() {
        return startTime != 0;
    }

    /**
     * @return true if this animation has been started and is currently paused
     */
    public final boolean isPaused() {
        return pauseDuration != 0;
    }

    /**
     * @return true if this animation is currently running
     */
    public final boolean isRunning() {
        return wasStarted() && !isPaused() && !isFinished;
    }

    /**
     * @return the {@link EndBehaviour} of this animation
     */
    @NonNull
    public EndBehaviour getEndBehaviour() {
        return endBehaviour;
    }

    /**
     * @return the duration of this animation in milliseconds
     */
    public final long getDuration() {
        return duration;
    }

    /**
     * Note that paused animations do not elapse any more time until resumed
     *
     * @return the elapsed time since this animation has been started or 0 if it hasn't been started yet
     */
    public final long getElapsedTime() {
        if (wasStarted()) {
            return isPaused() ? pauseDuration : System.currentTimeMillis() - startTime;
        }
        return 0;
    }

    /**
     * @return the count of completed animation laps
     */
    public final int getCompletedLaps() {
        return completedLaps;
    }

    /**
     * Adds an {@link AnimationListener} to this factory. It will be notified once its animation reached its end.<br>
     * Note that calling {@link #reset()} does not count as reaching the end and won't notify the listener<br>
     * Must be called on the main Thread
     *
     * @param listener The {@link AnimationListener} that should be added.
     */
    @MainThread
    public final void addAnimationListener(@NonNull AnimationListener listener) {
        animationListenerPool.add(listener);
    }

    /**
     * Removes an {@link AnimationListener} from this factory. The listener will no longer be notified after this call.<br>
     * Must be called on the main Thread
     *
     * @param listener The {@link AnimationListener} that should be removed
     */
    @MainThread
    public final void removeAnimationListener(@Nullable AnimationListener listener) {
        animationListenerPool.remove(listener);
    }

    /**
     * Adds a {@link ViewInvalidationListener} to this animation. Should only be called by {@link TintLayout}. The listener will be notified if changes about the animation require a redraw of the tint.<br>
     * The listener will only be held as a {@link WeakReference} to avoid memory leaks since the View may be disposed of without notifying its factory.<br>
     * Must be called on the main Thread
     *
     * @param listener The {@link ViewInvalidationListener} that should be added
     */
    @MainThread
    final void addInvalidationListener(@NonNull ViewInvalidationListener listener) {
        invalidationListenerPool.add(new WeakReference<>(listener));
    }

    /**
     * Removes a {@link ViewInvalidationListener} from this animation. Should only be called by {@link TintLayout}. The listener will no longer be notified after this call
     *
     * @param listener The {@link ViewInvalidationListener} that should be removed
     */
    @MainThread
    final void removeInvalidationListener(@Nullable ViewInvalidationListener listener) {
        invalidationListenerPool.removeIf(reference -> reference.get() == null || reference.get() == listener);
    }

    private void invalidateViews() {
        invalidationListenerPool.forEach(ref -> {
            ViewInvalidationListener listener = ref.get();
            if (listener != null) {
                listener.invalidate();
            }
        });
    }

    private void notifyAnimationListeners() {
        for (AnimationListener listener : animationListenerPool) {
            listener.onAnimationFinished(this);
        }
    }

    private float calculateElapsedDuration(long elapsedTime, int completedLaps) {
        switch (endBehaviour) {
            case LOOP:
                return elapsedTime - duration * completedLaps;
            case STICK:
                return Math.min(duration, elapsedTime);
            case RESET:
            default:
                return elapsedTime > duration ? 0 : elapsedTime;
        }
    }

    /**
     * A configuration for an {@link AnimatedTintFactory} containing the animations data
     */
    public static class AnimationConfig {

        private final long duration;
        private EndBehaviour endBehaviour = EndBehaviour.STICK;
        private Interpolator animationInterpolator = new LinearInterpolator();
        private PorterDuff.Mode defaultXferMode = PorterDuff.Mode.SRC_ATOP;

        /**
         * Creates a new instance with the given duration
         *
         * @param duration The duration of the animation in milliseconds
         * @throws IllegalArgumentException if the duration is <= 0
         */
        public AnimationConfig(long duration) {
            if (duration <= 0) {
                throw new IllegalArgumentException("Duration must be > 0");
            }
            this.duration = duration;
        }

        /**
         * Sets the {@link EndBehaviour} of the animation. By default {@link EndBehaviour#STICK}
         *
         * @param endBehaviour The desired {@link EndBehaviour}
         * @return the config for method chaining
         */
        public AnimationConfig setEndBehaviour(@NonNull EndBehaviour endBehaviour) {
            this.endBehaviour = endBehaviour;
            return this;
        }

        /**
         * Sets the {@link Interpolator} that is used to interpolate the progress of the animation. By default {@link LinearInterpolator}
         *
         * @param animationInterpolator The desired {@link Interpolator}
         * @return the config for method chaining
         */
        public AnimationConfig setAnimationInterpolator(@NonNull Interpolator animationInterpolator) {
            this.animationInterpolator = animationInterpolator;
            return this;
        }

        /**
         * Sets the provided {@link PorterDuff.Mode} as the default xfer mode that will be used by the animations {@link Paint} if none was set by {@link #createTint(int, int, float, int)}. By default {@link PorterDuff.Mode#SRC_ATOP SRC_ATOP}<br>
         * Pass null to have no default mode
         *
         * @param xferMode The {@link PorterDuff.Mode} that should be used as default xfer mode
         * @return the config for method chainig
         */
        public AnimationConfig setDefaultXferMode(@Nullable PorterDuff.Mode xferMode) {
            this.defaultXferMode = xferMode;
            return this;
        }
    }

    /**
     * Behaviours that an animation can take upon finishing
     */
    public enum EndBehaviour {
        /**
         * Resets the animation to the start and replays it.<br>
         * This goes infinitely until {@link #reset()} is called
         */
        LOOP,
        /**
         * Keeps the last frame of the animation
         */
        STICK,
        /**
         * Resets to the first frame of the animation
         */
        RESET
    }

    /**
     * A listener that will be notified by an {@link AnimatedTintFactory} when the current lap of an animation is completed
     */
    public interface AnimationListener {

        /**
         * Called on the main thread when the current lap of the animation is completed
         *
         * @param factory The {@link AnimatedTintFactory} that has reached its end
         */
        @MainThread
        void onAnimationFinished(@NonNull AnimatedTintFactory factory);

    }

    /**
     * A listener that will be notified by an {@link AnimatedTintFactory} when the current tint requires a redraw.<br>
     * Should only be implemented by {@link TintLayout}
     */
    interface ViewInvalidationListener {

        /**
         * Called on the main Thread when a redraw of the current tint is required
         */
        @MainThread
        void invalidate();

    }

}