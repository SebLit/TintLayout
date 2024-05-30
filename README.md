# TintLayout
A FrameLayout that can apply a tint to all of its children. Tints can be individually provided through factories. To tint something, simply wrap it in a TintLayout
~~~
 <com.litts.android.widget.tintlayout.TintLayout
        android:id="@+id/tintLayout"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Lorem ipsum dolor sit amet"/>

    </com.litts.android.widget.tintlayout.TintLayout>
~~~

Then apply the desired factory to your TintLayout
~~~
layout.setTintFactory(new ColorTintFactory(this, R.color.blue, PorterDuff.Mode.SRC_ATOP));
~~~

See below for visual examples.

## TintFactory
Factories supply a TintLayout with a Paint that it uses to tint its children. Implement the TintFactory interface to create your own factories, or use the premade ones. Note that TintLayout will use [Canvas#drawPaint(Paint)](https://developer.android.com/reference/android/graphics/Canvas#drawPaint(android.graphics.Paint)) to apply the tint.
So a Xfermode should be set to the Paint to influence which parts of the layout's content should be tinted.
~~~
public class MyTintFactory implements TintFactory {

    @NonNull
    @Override
    public Paint createTint(int width, int height) {
        Paint p = new Paint();
        p.setColor(someColorValue);
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        return p;
    }
    
}
~~~
By extending from AnimatedTintFactory instead, it is also possible to create tints that change over time. See the next section for more deail.

### AnimatedTintFactory
An abstract class implementing TintLayout. It provides an abstract interface with animation values that allow to create Paints that change over time. Extend this class to create your own animated factories, or use the premade ones.
~~~
public class MyTintFactory extends AnimatedTintFactory {
    
    public MyTintFactory(@NonNull AnimationConfig config) {
        super(config);
    }

    @NonNull
    @Override
    protected Paint createTint(int width, int height, float progress, int completedLaps) {
        // create paint based on progress and laps
        return new Paint();
    }
}
~~~

Through a Config the following animation values can be configured
* Duration in milliseconds
* Behaviour when the animation has reached its end (reset to first frame, stick with last frame, repeat) - default stick
* An Interpolator for the animations progress - default [LinearInterpolator](https://developer.android.com/reference/android/view/animation/LinearInterpolator)
* A default Xfermode for the Paint, if the subclass doesn't set one by itself - default [SRC_ATOP](https://developer.android.com/reference/android/graphics/PorterDuff.Mode#SRC_ATOP)

#### LinearGradientBounds
LinearGradientBounds provides boundary calculation for LinearGradients supporting full 360° angles. May be useful for creating your own AnimatedTintFactory when utilizing linear gradients. 
It will always put the start and end point of the gradient to the corners closer to the gradients axis so the entire target area will be covered by the gradient.

### Premade factories
Each of the premade factories can be configured through different parameters and configurations to apply its effect differently.
|ColorTintFactory|GradientTintFactory|ColorSwipeTintFactory|ColorTransitionTintFactory|ShimmerTintFactory|
|---|---|---|---|---|
|<a href="https://lh3.googleusercontent.com/drive-viewer/AKGpihYlylA5Rx_L3ureNfyMTsJDQneW9sRqvQrQuRnFFpOqBIgNqvAg8GJ6cbIaiKljJf6YsPegGKi0pF1KUCeNilYPfJ2GfT6P0w=s1600-rw-v1?source=screenshot.guru"> <img src="https://lh3.googleusercontent.com/drive-viewer/AKGpihYlylA5Rx_L3ureNfyMTsJDQneW9sRqvQrQuRnFFpOqBIgNqvAg8GJ6cbIaiKljJf6YsPegGKi0pF1KUCeNilYPfJ2GfT6P0w=s1600-rw-v1" /> </a>|<a href="https://lh3.googleusercontent.com/drive-viewer/AKGpihaWEIHlFSl2PBt020LH0EOLdNinm8Sx1AqiESnTLlOanVnk6Ax0417vMWZaaPgIlOFQks5ZdADVD87WfE-6dXcR7ToaaWJKV6M=s1600-rw-v1?source=screenshot.guru"> <img src="https://lh3.googleusercontent.com/drive-viewer/AKGpihaWEIHlFSl2PBt020LH0EOLdNinm8Sx1AqiESnTLlOanVnk6Ax0417vMWZaaPgIlOFQks5ZdADVD87WfE-6dXcR7ToaaWJKV6M=s1600-rw-v1" /> </a>|<a href="https://lh3.googleusercontent.com/drive-viewer/AKGpihbOQN_iqi1uZZZ8WZPBr-60pzEczCB9NpPau0kh_IIsXTRIhl83xDNLdFgTvmTfNxSf0QKxuNrEBGF9eHJrtLcG9YqEbVJT_NA=s1600-rw-v1?source=screenshot.guru"> <img src="https://lh3.googleusercontent.com/drive-viewer/AKGpihbOQN_iqi1uZZZ8WZPBr-60pzEczCB9NpPau0kh_IIsXTRIhl83xDNLdFgTvmTfNxSf0QKxuNrEBGF9eHJrtLcG9YqEbVJT_NA=s1600-rw-v1" /> </a>|<a href="https://lh3.googleusercontent.com/drive-viewer/AKGpihbVGakT894RUePINYjEaygE9ssIEB09IgOTiEiHLE2Xp3qFtREHfK0vXg93drsgFrC_0VIWHm6ReH9_888-h0L-lbeCKr8qwo8=s1600-rw-v1?source=screenshot.guru"> <img src="https://lh3.googleusercontent.com/drive-viewer/AKGpihbVGakT894RUePINYjEaygE9ssIEB09IgOTiEiHLE2Xp3qFtREHfK0vXg93drsgFrC_0VIWHm6ReH9_888-h0L-lbeCKr8qwo8=s1600-rw-v1" /> </a>|<a href="https://lh3.googleusercontent.com/drive-viewer/AKGpihb1uT9WqoK4addXZWKnk6VnxFfyrzPySAd5HZwOZb25P9mkCAxbAA9iFzGOIXOcsWf0ytP0JZ2JN2rIL95vBdiZ8MJYx1FHKA=s1600-rw-v1?source=screenshot.guru"> <img src="https://lh3.googleusercontent.com/drive-viewer/AKGpihb1uT9WqoK4addXZWKnk6VnxFfyrzPySAd5HZwOZb25P9mkCAxbAA9iFzGOIXOcsWf0ytP0JZ2JN2rIL95vBdiZ8MJYx1FHKA=s1600-rw-v1" /> </a>|
|Provides a fix color|Provides a fix gradient. Has support for different gradient types|Swiping animation between multiple colors|Transition animation between multiple colors|Runs a shimmer across the tinted area|
