package com.litts.android.widget.tintlayout;

import android.graphics.PointF;
import android.graphics.RectF;

import androidx.annotation.NonNull;

/**
 * Bounds for a {@link android.graphics.LinearGradient LinearGradient} with support for rotation angles. Bounds are always fitted to make the gradient start and end at the closest corners to the gradients axis
 */
public class LinearGradientBounds {

    private static final float FULL_ANGLE_DEGREES = 360;
    private static final float RIGHT_ANGLE_DEGREES = 90;
    private static final float RIGHT_ANGLE_RADIANS = (float) Math.toRadians(RIGHT_ANGLE_DEGREES);

    private static final int DIRECTION_DOWN = 0;
    private static final int DIRECTION_LEFT = 1;
    private static final int DIRECTION_UP = 2;
    private static final int DIRECTION_RIGHT = 3;

    /**
     * The horizontal start position of the gradient
     */
    public final float startX;
    /**
     * The vertical start position of the gradient
     */
    public final float startY;
    /**
     * The horizontal end position of the gradient
     */
    public final float endX;
    /**
     * The vertical end position of the gradient
     */
    public final float endY;

    /**
     * Creates gradient bounds for the provided drawing bounds of the gradient and its angle
     *
     * @param drawBounds     The bounds within the gradient will be visible
     * @param angleInDegrees The angle of the gradient in degrees. May be negative or exceed 360°
     */
    public LinearGradientBounds(@NonNull RectF drawBounds, float angleInDegrees) {
        float normalizedDegrees = angleInDegrees % FULL_ANGLE_DEGREES;
        float[] positions;
        if (normalizedDegrees % RIGHT_ANGLE_DEGREES == 0) {
            positions = findPositionsForRightAngle(drawBounds, normalizedDegrees);
        } else {
            positions = calculatePositions(drawBounds, normalizedDegrees);
        }
        startX = positions[0];
        startY = positions[1];
        endX = positions[2];
        endY = positions[3];
    }

    private float[] findPositionsForRightAngle(RectF drawBounds, float angleInDegrees) {
        int direction = (int) (angleInDegrees / RIGHT_ANGLE_DEGREES);
        float startX;
        float startY;
        float endX;
        float endY;
        if (direction == DIRECTION_UP || direction == DIRECTION_DOWN) {
            startX = drawBounds.centerX();
            endX = startX;
            boolean isUp = direction == DIRECTION_UP;
            startY = isUp ? drawBounds.bottom : drawBounds.top;
            endY = isUp ? drawBounds.top : drawBounds.bottom;
        } else {
            startY = drawBounds.centerY();
            endY = startY;
            boolean isRight = direction == DIRECTION_RIGHT;
            startX = isRight ? drawBounds.left : drawBounds.right;
            endX = isRight ? drawBounds.right : drawBounds.left;
        }
        return new float[]{startX, startY, endX, endY};
    }

    private float[] calculatePositions(RectF drawBounds, float angleInDegrees) {
        PointF startCorner;
        PointF endCorner;
        int direction = (int) (angleInDegrees / RIGHT_ANGLE_DEGREES);
        switch (direction) {
            case DIRECTION_DOWN:
                startCorner = new PointF(drawBounds.right, drawBounds.top);
                endCorner = new PointF(drawBounds.left, drawBounds.bottom);
                break;
            case DIRECTION_LEFT:
                startCorner = new PointF(drawBounds.right, drawBounds.bottom);
                endCorner = new PointF(drawBounds.left, drawBounds.top);
                break;
            case DIRECTION_UP:
                startCorner = new PointF(drawBounds.left, drawBounds.bottom);
                endCorner = new PointF(drawBounds.right, drawBounds.top);
                break;
            case DIRECTION_RIGHT:
            default:
                startCorner = new PointF(drawBounds.left, drawBounds.top);
                endCorner = new PointF(drawBounds.right, drawBounds.bottom);
                break;
        }

        double gradientAngle = RIGHT_ANGLE_RADIANS + Math.toRadians(angleInDegrees); // add 90° to make down direction the origin
        double gradientSlope = Math.tan(gradientAngle);
        double gradientYIntersection = drawBounds.centerY() - gradientSlope * drawBounds.centerX();

        double startLineAngle = RIGHT_ANGLE_RADIANS + gradientAngle;
        double startLineSlope = Math.tan(startLineAngle);
        double startLineYIntersection = startCorner.y - startLineSlope * startCorner.x;

        float startX = (float) ((startLineYIntersection - gradientYIntersection) / (gradientSlope - startLineSlope));
        float startY = (float) (gradientSlope * startX + gradientYIntersection);
        float endX = endCorner.x - (startX - startCorner.x);
        float endY = endCorner.y - (startY - startCorner.y);
        return new float[]{startX, startY, endX, endY};
    }

    @Override
    public String toString() {
        return "LinearGrdientBounds{" +
                "startX=" + startX +
                ", startY=" + startY +
                ", endX=" + endX +
                ", endY=" + endY +
                '}';
    }
}
