package net.conceptdev.windowmanagerjava;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.window.java.layout.WindowInfoRepositoryCallbackAdapter;
import androidx.window.layout.DisplayFeature;
import androidx.window.layout.FoldingFeature;
import androidx.window.layout.WindowInfoRepository;
import androidx.window.layout.WindowLayoutInfo;
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
* https://developer.android.com/jetpack/androidx/releases/window#window-1.0.0-alpha10
* */

public class MainActivity extends AppCompatActivity {
    String TAG = "JWM";
    WindowInfoRepositoryCallbackAdapter wir;
    ConstraintLayout root;
    TextView outputText;

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
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart add listener");
        wir.addWindowLayoutInfoListener(runOnUiThreadExecutor(), (windowLayoutInfo -> {
            List<DisplayFeature> displayFeatures = windowLayoutInfo.getDisplayFeatures();

            if (displayFeatures.isEmpty()) {
                outputText.setText("no display features");
                return;
            }
            else {
                // update screen
                Log.d(TAG, "window layout contains display feature/s");
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
                        outputText.setText(out);

                        return; // only works for one hinge/fold!
                    }
                });
            }
        }));
    }

    @Override
    protected void onStop() {
        super.onStop();
        //wir.removeWindowLayoutInfoListener(); // TODO?
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