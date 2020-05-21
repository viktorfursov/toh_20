package com.viktorfursov.toh;

import android.app.Activity;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

public class GameActivity extends Activity {

    // Our object to handle the View
    private TOHView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Get a Display object to access screen details
        Display display = getWindowManager().getDefaultDisplay();
        // Load the resolution into a Point object
        Point size = new Point();
        display.getSize(size);

        // Create an instance of our TOHView
        gameView = new TOHView(this, size.x, size.y);
        setContentView(gameView);
    }

    // If the Activity is paused make sure
    // to pause our thread
    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    // If Activity is resumed make sure
    // to resume our thread
    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }
}
