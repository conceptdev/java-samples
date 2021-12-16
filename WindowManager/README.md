# Jetpack Window Manager for Java

Surface Duo sample that implements [Jetpack Window Manager rc01](https://developer.android.com/jetpack/androidx/releases/window#window-1.0.0-rc01) using Java and the **androidx.window:window-java** adapter package.

![Surface Duo running sample that shows window manager data using Java](Screenshots/winmgr-java-framed.png)

For Kotlin, refer to the [Surface Duo Window Manager samples repo](https://github.com/microsoft/surface-duo-window-manager-samples) and the [Android user-interface sample for Window Manager](https://github.com/android/user-interface-samples/tree/master/WindowManager).

## Using Java

The [rc01 release](https://developer.android.com/jetpack/androidx/releases/window#window-1.0.0-rc01) uses the API introduced in beta01 - using classes optimized for Kotlin, and a separate package **window-java** that includes a `WindowInfoTrackerCallbackAdapter` that makes it easy to use from Java.

```gradle
dependencies {
    implementation "androidx.window:window:1.0.0-rc01"
    implementation "androidx.window:window-java:1.0.0-rc01"
```

The implementation using `WindowInfoTrackerCallbackAdapter` can look like this:

```java
WindowInfoTrackerCallbackAdapter wit;
//...
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //...
    wit = new WindowInfoTrackerCallbackAdapter(
            WindowInfoTracker.Companion.getOrCreate(
                    this
            )
    );
}
//...
@Override
protected void onStart() {
    super.onStart();
    wit.addWindowLayoutInfoListener(
            this,
            runOnUiThreadExecutor(), 
            layoutStateChangeCallback);
}

@Override
protected void onStop() {
    super.onStop();
    wit.removeWindowLayoutInfoListener(layoutStateChangeCallback);
}
//...
void updateLayout(WindowLayoutInfo windowLayoutInfo)
{
    List<DisplayFeature> displayFeatures = windowLayoutInfo.getDisplayFeatures();

    displayFeatures.forEach(displayFeature -> {
        FoldingFeature foldingFeature = (FoldingFeature)displayFeature;
        if (foldingFeature != null)
        {   // only set if it's a fold, not other feature type. only works for single-fold devices.
            // do stuff with the hinge/fold
        }
    });
}
class LayoutStateChangeCallback implements Consumer<WindowLayoutInfo> {
    @Override
    public void accept(WindowLayoutInfo windowLayoutInfo) {
        updateLayout(windowLayoutInfo);
    }
}
Executor runOnUiThreadExecutor()
{
    return new MyExecutor();
}
class MyExecutor implements Executor
{
    Handler handler = new Handler(Looper.getMainLooper());
    @Override
    public void execute(Runnable command) {
        handler.post(command);
    }
}
```

Refer to the [Surface Duo developer documentation](https://docs.microsoft.com/dual-screen/android/jetpack/window-manager/) and the [Android developer documentation](https://developer.android.com/jetpack/androidx/releases/window) for more details about building apps using Jetpack Window Manager.
