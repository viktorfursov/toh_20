package com.viktorfursov.toh;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class StartScreenViewer extends SurfaceView implements Runnable{

    /*
    Class draws start screen
     */

    // time
    int counter;

    private boolean screenLOGO;
    private boolean screenMenu;
    private boolean screenCreator;
    private boolean screenHowToPlay;
    private boolean seeFlag;

    Thread startScreenThread = null;

    private int screenX;
    private int screenY;
    private Context context;
    private float motionX;
    private float motionY;

    // for drawing
    private Paint paint;
    private Paint gradientPaint;
    private Canvas canvas;
    private SurfaceHolder ourHolder;

    RectF newGameArea;
    RectF howToPlayArea;
    RectF creatorArea;

    public ArrayList<SpaceDust> dustList = new  ArrayList<SpaceDust>();

    // CONSTRUCTOR
    public StartScreenViewer(Context context, int x, int y) {
        super(context);
        this.context = context;
        screenX = x;
        screenY = y;

        ourHolder = getHolder();
        paint = new Paint();
        gradientPaint = new Paint();

        counter = 0;

        screenLOGO = true;
        screenMenu = false;
        screenCreator = false;
        screenHowToPlay = false;

        newGameArea = new RectF(screenX/4, screenY/3-screenY/12, screenX/2+screenX/4, screenY/3);
        howToPlayArea = new RectF(screenX/4, screenY/3+screenY/12, screenX/2+screenX/4, screenY/3 + screenY/6);
        creatorArea = new RectF(screenX/4, screenY/3*2-screenY/12, screenX/2+screenX/4, screenY/3*2);

        // Initialize space dust
        int numSpecs = 150;
        for (int i = 0; i < numSpecs; i++) {
            // Where will the dust spawn?
            SpaceDust spec = new SpaceDust(x, y);
            dustList.add(spec);
        }
    }

    // MAIN LOOP
    @Override
    public void run() {
        while (seeFlag) {
            update();
            draw();
            control();
        }
    }

    // UPDATE OBJECTS
    private void update() {
        if (counter < 41) {
            counter++;
        }
        for (SpaceDust sd : dustList) {
            sd.update();
        }
    }

    // DRAW OBJECTS
    private void draw() {
        if (ourHolder.getSurface().isValid()) {

            // First we lock the area of memory we will drawing to
            canvas = ourHolder.lockCanvas();

            int[] clrs = {Color.argb(255, 175, 105, 58),
                    Color.argb(255, 252, 215, 166),
                    Color.argb(255, 164, 88, 47)};

            // DRAW SPACE---------------------------------------------------------------------------
            Shader gradient = new LinearGradient(0, 0, 0, screenY*2,
                    Color.argb(255,16,10, 29), Color.argb(255,28,17,49), Shader.TileMode.CLAMP);
            gradientPaint.setShader(gradient);
            canvas.drawRect(new RectF(0,0,screenX,screenY), gradientPaint);

            gradient = new LinearGradient(screenX/2, (screenY/3)*2, screenX/2, screenY,
                    Color.argb(150,30,22, 55), Color.argb(85,30,22,55), Shader.TileMode.CLAMP);
            gradientPaint.setShader(gradient);
            canvas.drawRect(new RectF(0,(screenY/3)*2,screenX,screenY), gradientPaint);

            paint.setColor(Color.argb(255,255,255,255));
            // Draw space dust
            for (SpaceDust sd : dustList) {
                canvas.drawPoint(sd.getX(), sd.getY(), paint);
            }

            // Draw mountains
            Path path = new Path();
            // draw path
            path.moveTo(0, screenY/3);
            path.lineTo(0, screenY/12*6);

            path.lineTo(screenX/16, screenY/12*5);
            path.lineTo(screenX/16*2, screenY/12*6);
            path.lineTo(screenX/16*3, screenY/12*5);
            path.lineTo(screenX/16*4, screenY/3);
            path.lineTo(screenX/16*5, screenY/12*5);
            path.lineTo(screenX/16*6, screenY/12*3);
            path.lineTo(screenX/16*7, screenY/6);
            path.lineTo(screenX/2, screenY/12*3);
            path.lineTo(screenX/16*9, screenY/12*4);
            path.lineTo(screenX/16*10, screenY/2);
            path.lineTo(screenX/16*11, screenY/12*5);
            path.lineTo(screenX/16*12, screenY/12*6);
            path.lineTo(screenX/16*13, screenY/12*4);
            path.lineTo(screenX/16*14, screenY/12*3);
            path.lineTo(screenX/16*15, screenY/12*4);

            path.lineTo(screenX, screenY/12*3);
            path.lineTo(screenX, (screenY/3)*2);
            path.lineTo(0, (screenY/3)*2);
            path.lineTo(0, screenY/3);
            path.close();
            gradient = new LinearGradient(screenX/2, screenY/12, screenX/2, (screenY/3)*2,
                    Color.argb(255,16,10, 29), Color.argb(255,14,8,25), Shader.TileMode.CLAMP);
            gradientPaint.setShader(gradient);
            canvas.drawPath(path, gradientPaint);

            // DRAW GROUND LINES--------------------------------------------------------------------
            paint.setColor(Color.argb(40,255,255,255));
            canvas.drawLine(0, (screenY/3)*2, screenX, (screenY/3)*2, paint);
            canvas.drawLine(0,screenY,screenX/2, (screenY/3)*2, paint);
            canvas.drawLine(screenX/2,(screenY/3)*2, screenX, screenY, paint);
            canvas.drawLine(screenX/3, screenY, screenX/2, (screenY/3)*2, paint);
            canvas.drawLine(screenX/2, (screenY/3)*2, (screenX/3)*2, screenY, paint);

            gradient = new LinearGradient(screenX/2, (screenY/3)*2, screenX, ((screenY/3)*2 + (screenY/3)/2),
                    Color.argb(60,255,255, 255), Color.argb(0,255,255,255), Shader.TileMode.CLAMP);
            gradientPaint.setShader(gradient);
            canvas.drawLine(screenX/2, (screenY/3)*2, screenX, ((screenY/3)*2 + (screenY/3)/2), gradientPaint);

            gradient = new LinearGradient(screenX/2, (screenY/3)*2, 0, ((screenY/3)*2 + (screenY/3)/2),
                    Color.argb(60,255,255, 255), Color.argb(0,255,255,255), Shader.TileMode.CLAMP);
            gradientPaint.setShader(gradient);
            canvas.drawLine(0, ((screenY/3)*2 + (screenY/3)/2), screenX/2, (screenY/3)*2, gradientPaint);

            gradient = new LinearGradient(screenX/2, (screenY/3)*2, screenX/2, screenY,
                    Color.argb(60,255,255, 255), Color.argb(0,255,255,255), Shader.TileMode.CLAMP);
            gradientPaint.setShader(gradient);
            canvas.drawLine(screenX/2, (screenY/3)*2, screenX/2, screenY, gradientPaint);

            if (screenLOGO) {
                //DRAW GAME LOGO------------------------------------------------------------------------------

                gradientPaint.setTextAlign(Paint.Align.CENTER);
                gradientPaint.setTypeface(context.getResources().getFont(R.font.orbitron_regular));
                paint.setTypeface(context.getResources().getFont(R.font.orbitron_regular));

                // Draw the
                gradientPaint.setTextSize(screenX / 38);
                gradient = new LinearGradient(screenX / 2 - screenX / 7, screenY / 12 * 5, screenX / 2 + screenX / 7, screenY / 12 * 5,
                        clrs, null, Shader.TileMode.CLAMP);
                gradientPaint.setShader(gradient);
                canvas.drawText("the", screenX / 2, screenY / 12 * 4 - screenY / 34, gradientPaint);

                // Draw Tower
                gradientPaint.setTextSize(screenX / 12);
                gradient = new LinearGradient(screenX / 2 - screenX / 7, screenY / 12 * 5, screenX / 2 + screenX / 7, screenY / 12 * 5,
                        clrs, null, Shader.TileMode.CLAMP);
                gradientPaint.setShader(gradient);
                canvas.drawText("Tower", screenX / 2, screenY / 12 * 5, gradientPaint);

                // Draw of
                gradientPaint.setTextSize(screenX / 38);
                gradient = new LinearGradient(screenX / 2 - screenX / 7, screenY / 12 * 5, screenX / 2 + screenX / 7, screenY / 12 * 5,
                        clrs, null, Shader.TileMode.CLAMP);
                gradientPaint.setShader(gradient);
                canvas.drawText("of", screenX / 2, screenY / 12 * 5 + screenY / 20, gradientPaint);

                // Draw Hanoi
                gradientPaint.setTextSize(screenX / 12);
                gradient = new LinearGradient(screenX / 2 - screenX / 7, screenY / 12 * 5, screenX / 2 + screenX / 7, screenY / 12 * 5,
                        clrs, null, Shader.TileMode.CLAMP);
                gradientPaint.setShader(gradient);
                canvas.drawText("Hanoi", screenX / 2, screenY / 12 * 7, gradientPaint);

                // Draw tap to continue
                if ((counter) > 40) {
                    paint.setColor(Color.argb(255, 255, 255, 255));
                    paint.setTextAlign(Paint.Align.CENTER);
                    paint.setTextSize(screenX/40);
                    canvas.drawText("tap to continue", screenX / 2, screenY / 12 * 11, paint);
                }
            } else if (screenMenu) {
                gradient = new LinearGradient(screenX / 2 - screenX / 7, screenY / 12 * 5, screenX / 2 + screenX / 7, screenY / 12 * 5,
                        clrs, null, Shader.TileMode.CLAMP);
                gradientPaint.setShader(gradient);
                gradientPaint.setTextAlign(Paint.Align.CENTER);
                gradientPaint.setTextSize(screenX/28);
                canvas.drawText("New game", screenX / 2, screenY / 3, gradientPaint);
                canvas.drawText("How to play", screenX / 2, screenY / 3 *2-screenY/6 , gradientPaint);
                canvas.drawText("About", screenX / 2, screenY/3*2, gradientPaint);
            } else if (screenCreator) {
                gradient = new LinearGradient(screenX / 2 - screenX / 7, screenY / 12 * 5, screenX / 2 + screenX / 7, screenY / 12 * 5,
                        clrs, null, Shader.TileMode.CLAMP);
                gradientPaint.setShader(gradient);
                gradientPaint.setTextAlign(Paint.Align.CENTER);
                gradientPaint.setTextSize(screenX/44);
                canvas.drawText("Programmer and game designer", screenX / 2, screenY / 3, gradientPaint);
                canvas.drawText("Viktor Fursov", screenX / 2, screenY / 3+screenY/12, gradientPaint);
                canvas.drawText("2020", screenX / 2, screenY/3 + screenY / 12*2, gradientPaint);
            } else if (screenHowToPlay) {
                gradient = new LinearGradient(screenX / 2 - screenX / 7, screenY / 12 * 5, screenX / 2 + screenX / 7, screenY / 12 * 5,
                        clrs, null, Shader.TileMode.CLAMP);
                gradientPaint.setShader(gradient);
                gradientPaint.setTextAlign(Paint.Align.CENTER);
                gradientPaint.setTextSize(screenX/44);
                canvas.drawText("Your goal in this game is to relocate the tower from A to C using B area.", screenX / 2, screenY / 3, gradientPaint);
                canvas.drawText("You can take only one disc at the time. Have a put disc at any area.", screenX / 2, screenY / 3+screenY/12, gradientPaint);
                canvas.drawText("It's allowed to have a smaller disc on a bigger, but not vice versa.", screenX / 2, screenY/3 + screenY / 12*2, gradientPaint);
            }

            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    // SurfaceView allows us to handle the onTouchEvent
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        // There are many different events in MotionEvent
        // We care about just 2 - for now.
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                motionX = motionEvent.getX();
                motionY = motionEvent.getY();

                if (screenLOGO) {
                    screenLOGO = false;
                    screenMenu = true;
                } else if (screenMenu) {
                    if (newGameArea.contains(motionX, motionY)) {
                        Intent myIntent = new Intent(context, GameActivity.class);
                        context.startActivity(myIntent);
                        //try {
                        //    startScreenThread.join();
                        //} catch(InterruptedException e) {


                    } else if (howToPlayArea.contains(motionX, motionY)) {
                        screenMenu = false;
                        screenHowToPlay = true;
                    } else if (creatorArea.contains(motionX, motionY)) {
                        screenMenu = false;
                        screenCreator = true;
                    }

                } else if (screenCreator) {
                    screenCreator = false;
                    screenMenu = true;
                } else if (screenHowToPlay) {
                    screenHowToPlay = false;
                    screenMenu = true;
                }
                break;
        }
        return true;


    }

    // CONTROL
    private void control() {
        try {    startScreenThread.sleep(17);
        } catch (InterruptedException e) {
        }
    }


    // Clean up our thread if the game is interrupted or
    // the player quits
    public void pause() {
        seeFlag = false;
        try {
            startScreenThread.join();
        } catch( InterruptedException e) {

        }
    }

    // Make a new thread and start it
    // Execution moves to our R
    public void resume() {
        seeFlag = true;
        startScreenThread = new Thread(this);
        startScreenThread.start();
    }

}
