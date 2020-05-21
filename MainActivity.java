package com.viktorfursov.toh;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity{

    StartScreenViewer startScreenView;
    // This is the entry point to our game
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get a Display object to access screen details
        Display display = getWindowManager().getDefaultDisplay();
        // Load the resolution into a Point object
        Point size = new Point();
        display.getSize(size);

        // Create an instance of our TOHView
        startScreenView = new StartScreenViewer(this, size.x, size.y);
        setContentView(startScreenView);
        //finish();

    }


    // If the Activity is paused make sure
    // to pause our thread
    @Override
    protected void onPause() {
        super.onPause();
        startScreenView.pause();
    }

    // If Activity is resumed make sure
    // to resume our thread
    @Override
    protected void onResume() {
        super.onResume();
        startScreenView.resume();
    }
}
