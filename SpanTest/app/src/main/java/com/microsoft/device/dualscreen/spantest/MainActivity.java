package com.microsoft.device.dualscreen.spantest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.TextView;

import com.microsoft.device.display.DisplayMask;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    String TAG = "SpanTestTag";

    @Override
    protected void onPostResume() {
        super.onPostResume();
        TextView output = this.findViewById(R.id.output);
        String tt = (String) output.getText();

        tt += "===============================" ;
        tt += "\n";

        tt += "fromResourcesRectApproximation: " ;
        tt += "\n";
        DisplayMask dm = DisplayMask.fromResourcesRectApproximation(this);
        tt += "getBoundingRects: " + dm.getBoundingRects() ;
        tt += "\n";
        tt += "getBoundingRectsForRotation: ";
        tt += "\n" + dm.getBoundingRectsForRotation(Surface.ROTATION_0);
        tt += "\n" + dm.getBoundingRectsForRotation(Surface.ROTATION_90);
        tt += "\n" + dm.getBoundingRectsForRotation(Surface.ROTATION_180);
        tt += "\n" + dm.getBoundingRectsForRotation(Surface.ROTATION_270);
        tt += "\n";
        tt += "getBounds: " + dm.getBounds() ;
        tt += "\n";
        tt += "window rect : " + getWindowRect(this);
        tt += "\n";
        if(dm.getBoundingRects().size() >=1) {
            tt += "intersect : " + getWindowRect(this).intersect(dm.getBoundingRects().get(0));
        }
        else{
            tt += "no intersect : not spanned" ;
        }

        tt += "\n";
        tt += "\n";
        tt += "isAppSpanned : " + isAppSpanned();

        output.setText(tt);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView output = this.findViewById(R.id.output);

        String tt ="";
        tt += "fromResourcesRectApproximation: " ;
        tt += "\n";
        DisplayMask dm = DisplayMask.fromResourcesRectApproximation(this);
        tt += "getBoundingRects: " + dm.getBoundingRects() ;
        tt += "\n";
        tt += "getBoundingRectsForRotation: ";
        tt += "\n" + dm.getBoundingRectsForRotation(Surface.ROTATION_0);
        tt += "\n" + dm.getBoundingRectsForRotation(Surface.ROTATION_90);
        tt += "\n" + dm.getBoundingRectsForRotation(Surface.ROTATION_180);
        tt += "\n" + dm.getBoundingRectsForRotation(Surface.ROTATION_270);
        tt += "\n";
        tt += "getBounds: " + dm.getBounds() ;
        tt += "\n";
        tt += "window rect : " + getWindowRect(this);
        tt += "\n";
        if(dm.getBoundingRects().size() >=1) {
            tt += "intersect : " + getWindowRect(this).intersect(dm.getBoundingRects().get(0));
        }
        else{
            tt += "no intersect : not spanned" ;
        }

        tt += "\n";
        tt += "\n";
        tt += "isAppSpanned : " + isAppSpanned();

        output.setText(tt);

        // force portrait when wide
//        if (getWindowRect(this).width() == 2784){
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        } else  if (getWindowRect(this).height() == 2784){
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }
//        else {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
//        }


        // force landscape when wide
        if (getWindowRect(this).width() == 2784){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else  if (getWindowRect(this).height() == 2784){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    Rect getWindowRect(Activity activity) {
        Rect windowRect = new Rect();
        activity.getWindowManager().getDefaultDisplay().getRectSize(windowRect);
        return windowRect;
    }


    private boolean isAppSpanned() {
        DisplayMask displayMask = DisplayMask.fromResourcesRectApproximation(this);
        Region region = displayMask.getBounds();
        List<Rect> boundings = displayMask.getBoundingRects();
        if (boundings.size() < 1)
        {
            Log.d(TAG, "Single screen - not intersect (no boundings) ");
            return false;
        }
        Rect first = boundings.get(0);
        View rootView = this.getWindow().getDecorView().getRootView();
        Rect drawingRect = new Rect();
        rootView.getDrawingRect(drawingRect);
        if (first.intersect(drawingRect)) {
            Log.d(TAG, "Dual screen - intersect: " + drawingRect);
            return true;
        } else {
            Log.d(TAG, "Single screen - not intersect: " + drawingRect);
            return false;
        }
    }

}