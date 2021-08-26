package net.conceptdev.windowmanagerjava;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.util.Consumer;
import androidx.window.java.layout.WindowInfoRepositoryCallbackAdapter;
import androidx.window.layout.DisplayFeature;
import androidx.window.layout.FoldingFeature;
import androidx.window.layout.WindowInfoRepository;
import androidx.window.layout.WindowLayoutInfo;
import androidx.window.layout.WindowMetrics;
import androidx.window.layout.WindowMetricsCalculator;

import android.graphics.Rect;
import android.os.Bundle;

import java.util.List;
import java.util.concurrent.Executor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import com.google.android.material.internal.ContextUtils;

/*
* https://developer.android.com/jetpack/androidx/releases/window#window-1.0.0-beta01
* */

public class MainActivity extends AppCompatActivity {
    String TAG = "JWM";
    LayoutStateChangeCallback layoutStateChangeCallback = new LayoutStateChangeCallback();
    WindowInfoRepositoryCallbackAdapter wir;
    ConstraintLayout root;
    TextView outputText;
    WindowMetricsCalculator wmc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        root = findViewById(R.id.root);
        outputText = findViewById(R.id.outputText);

        Log.d(TAG, "onCreate callback adapter");
        wir = new WindowInfoRepositoryCallbackAdapter(
                WindowInfoRepository.Companion.getOrCreate(
                        this
                )
        );
        wmc = WindowMetricsCalculator.Companion.getOrCreate();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // WindowInfoRepository listener
        Log.d(TAG, "onStart add listener");
        wir.addWindowLayoutInfoListener(runOnUiThreadExecutor(), layoutStateChangeCallback);
    }

    @Override
    protected void onStop() {
        super.onStop();
        wir.removeWindowLayoutInfoListener(layoutStateChangeCallback);
    }

    void updateLayout(WindowLayoutInfo windowLayoutInfo)
    {
        // WindowMetrics synchronous call
        WindowMetrics wm = wmc.computeCurrentWindowMetrics(this);
        wm = wmc.computeCurrentWindowMetrics(this);
        String currentMetrics = wm.getBounds().toString();
        wm = wmc.computeMaximumWindowMetrics(this);
        String maxMetrics = wm.getBounds().toString();
        String outMetrics = "\n\n";
        outMetrics += "Current Window Metrics " + currentMetrics;
        outMetrics += "\n";
        outMetrics += "Maximum Window Metrics " + maxMetrics;

        // WindowInfoRepository DisplayFeatures
        List<DisplayFeature> displayFeatures = windowLayoutInfo.getDisplayFeatures();
        if (displayFeatures.isEmpty()) {
            String out = getString(R.string.no_features);
            out += outMetrics;
            outputText.setText(out);
            return;
        }
        else {
            // update screen
            Log.d(TAG, "window layout contains display feature/s");
            String finalOutMetrics = outMetrics;
            displayFeatures.forEach(displayFeature -> {
                FoldingFeature foldingFeature = (FoldingFeature)displayFeature;
                if (foldingFeature != null)
                {   // only set if it's a fold, not other feature type. only works for single-fold devices.
                    String out = "";
                    if (foldingFeature.getOrientation() == FoldingFeature.Orientation.HORIZONTAL) {
                        out += getString(R.string.hinge_is_horizontal);
                    } else {
                        out += getString(R.string.hinge_is_vertical);
                    }
                    out += "\n";
                    out += "State is " + foldingFeature.getState().toString();
                    out += "\n";
                    out += "OcclusionType is " + foldingFeature.getOcclusionType().toString();
                    out += "\n";
                    out += "isSeparating is " + foldingFeature.isSeparating();
                    out += "\n";
                    out += "Bounds are " + foldingFeature.getBounds().toString();
                    out += finalOutMetrics;
                    outputText.setText(out);

                    return; // only works for one hinge/fold!
                }
            });
        }
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
}