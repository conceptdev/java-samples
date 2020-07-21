package com.microsoft.device.dualscreen.configchangestestj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import com.microsoft.device.display.DisplayMask;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    String TAG = "DUALSCREEN_CONFIGCHANGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Log.d(TAG, "onConfigurationChanged isAppSpanned " + isAppSpanned(this));
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume isAppSpanned " + isAppSpanned(this));
    }

    private boolean isDeviceSurfaceDuo(){
        String feature = "com.microsoft.device.display.displaymask";
        PackageManager pm = this.getPackageManager();

        if (pm.hasSystemFeature(feature)) {
            Log.i(TAG, "System has feature: " + feature);
            return true;
        } else {
            Log.i(TAG, "System missing feature: " + feature);
            return false;
        }
    }




    public final int getCurrentRotation(@NonNull Activity activity) {
        int rotation;
        try {
            WindowManager wm = (WindowManager)activity.getSystemService(Context.WINDOW_SERVICE);
            Display var5 = wm.getDefaultDisplay();
            rotation = var5.getRotation();
        } catch (IllegalStateException ise) {
            rotation = Surface.ROTATION_0;
        }
        return rotation;
    }

    public final Rect getHinge(@NonNull Activity activity) {
        Rect hinge;
        if (isDeviceSurfaceDuo()) {
            DisplayMask displayMask = DisplayMask.fromResourcesRectApproximation((Context)activity);
            if (displayMask != null) {
                List screensBounding = displayMask.getBoundingRectsForRotation(this.getCurrentRotation(activity));
                hinge = screensBounding.size() == 0 ? new Rect(0, 0, 0, 0) : (Rect)screensBounding.get(0);
            } else {
                hinge = null;
            }
        } else {
            hinge = null;
        }
        return hinge;
    }

    public final Rect getWindowRect(@NonNull Activity activity) {
        Rect windowRect = new Rect();
        WindowManager wm = activity.getWindowManager();
        wm.getDefaultDisplay().getRectSize(windowRect);
        return windowRect;
    }

    public final boolean isAppSpanned(@NonNull Activity activity) {
        Rect hinge = this.getHinge(activity);
        Rect windowRect = this.getWindowRect(activity);
        return hinge != null && windowRect.width() > 0 && windowRect.height() > 0 ? hinge.intersect(windowRect) : false;
    }
}