package com.viktorfursov.toh;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TOHView extends SurfaceView implements Runnable{
/*
    private SoundPool soundPool;
    int start = -1;
    int takeDisc = -1;
    int dropDisc = -1;
    MediaPlayer mp;
*/
    volatile boolean playing;
    private boolean gameEnded;
    Thread gameThread = null;
    private long timeStarted;
    private long currentTime;
    private int moves;

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

    // GAME OBJECTS
    Tower tower1;
    Tower tower2;
    Tower tower3;
    int t1KernelX;
    int t2KernelX;
    int t3KernelX;
    int kernelY;

    // area for detecting gestures
    RectF tower1Area;
    RectF tower2Area;
    RectF tower3Area;

    // space dust array
    public ArrayList<SpaceDust> dustList = new  ArrayList<SpaceDust>();

    // CONSTRUCTOR
    public TOHView(Context context, int x, int y) {

        super(context);
        this.context = context;
/*
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,0);

        try {
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;
            descriptor = assetManager.openFd("disc_up.ogg");
            takeDisc = soundPool.load(descriptor, 0);
            descriptor = assetManager.openFd("disc_drop.ogg");
            dropDisc = soundPool.load(descriptor, 0);

        } catch (IOException e) {

        }
*/
        gameEnded = false;
        screenX = x;
        screenY = y;

        ourHolder = getHolder();
        paint = new Paint();
        gradientPaint = new Paint();

        // Initialize the towers
        t1KernelX = screenX/16*3;
        t2KernelX = screenX/2;
        t3KernelX = screenX/16*13;
        kernelY = screenY/12*9;

        tower1 = new Tower(context, 7, x, y);
        tower1.fill();
        tower2 = new Tower(context, -1, x, y);
        tower3 = new Tower(context, -1, x, y);

        tower1Area = new RectF(screenX/16,screenY/3-screenY/12,screenX/16*5,screenY/12*11-screenY/24);
        tower2Area = new RectF(screenX/16*6,screenY/3-screenY/12,screenX/16*10,screenY/12*11-screenY/24);
        tower3Area = new RectF(screenX/16*11,screenY/3-screenY/12,screenX/16*15,screenY/12*11-screenY/24);

        // Initialize space dust
        int numSpecs = 150;
        for (int i = 0; i < numSpecs; i++) {
            // Where will the dust spawn?
            SpaceDust spec = new SpaceDust(x, y);
            dustList.add(spec);
        }
        timeStarted = System.currentTimeMillis();
        moves = 0;
    }

    // MAIN LOOP
    @Override
    public void run() {
        while (playing) {
            update();
            draw();
            control();
        }
    }

    // UPDATE OBJECTS
    private void update() {
        //tower1.update();
        //tower2.update();
        //tower3.update();

        currentTime = System.currentTimeMillis();

        for (SpaceDust sd : dustList) {
            sd.update();
        }
    }

    // DRAW OBJECTS
    private void draw() {
        if (ourHolder.getSurface().isValid()) {

            // First we lock the area of memory we will drawing to
            canvas = ourHolder.lockCanvas();

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

            // DRAW TARGET ELLIPSES-----------------------------------------------------------------
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2);
            canvas.drawOval(screenX/16, screenY/12*9+screenY/24, screenX/16*5, screenY/12*11-screenY/24, paint);
            canvas.drawOval(screenX/16*6, screenY/12*9+screenY/24, screenX/16*10, screenY/12*11-screenY/24, paint);
            canvas.drawOval(screenX/16*11, screenY/12*9+screenY/24, screenX/16*15, screenY/12*11-screenY/24, paint);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.argb(25, 223,173,127));
            canvas.drawOval(screenX/16, screenY/12*9+screenY/24, screenX/16*5, screenY/12*11-screenY/24, paint);
            canvas.drawOval(screenX/16*6, screenY/12*9+screenY/24, screenX/16*10, screenY/12*11-screenY/24, paint);
            canvas.drawOval(screenX/16*11, screenY/12*9+screenY/24, screenX/16*15, screenY/12*11-screenY/24, paint);

            // DRAW THE TOWERS----------------------------------------------------------------------
            paint.setAlpha(255);
            // Tower 1
            for(int i=0, dY=kernelY; i < tower1.discs.length; i++){
                if (tower1.towerSchematic[i] != -1) {
                    canvas.drawBitmap(tower1.discs[i], t1KernelX-tower1.discs[i].getWidth()/2, dY, paint);
                    dY -= screenY/14;
                }
            }
            // Tower 2
            for(int i=0, dY=kernelY; i < tower2.discs.length; i++){
                if (tower2.towerSchematic[i] != -1) {
                    canvas.drawBitmap(tower2.discs[i], t2KernelX-tower2.discs[i].getWidth()/2, dY, paint);
                    dY -= screenY/14;
                }
            }
            // Tower 3
            for(int i=0, dY=kernelY; i < tower3.discs.length; i++){
                if (tower3.towerSchematic[i] != -1) {
                    canvas.drawBitmap(tower3.discs[i], t3KernelX-tower3.discs[i].getWidth()/2, dY, paint);
                    dY -= screenY/14;
                }
            }
            if (Tower.SINGLE_DISC != null ) {
                canvas.drawBitmap(Tower.SINGLE_DISC, Tower.SINGLE_DISC_X, Tower.SINGLE_DISC_Y, paint);
            }

            // GAME END DETECTION-------------------------------------------------------------------
            if (tower3.towerSchematic[0] == 0 & tower3.towerSchematic[1] == 1 & tower3.towerSchematic[2] ==2 &
                    tower3.towerSchematic[3] == 3 & tower3.towerSchematic[4] == 4 & tower3.towerSchematic[5] == 5 &
                    tower3.towerSchematic[6] == 6 & tower3.towerSchematic[7] == 7 ) {
                gameEnded = true; // END OF THE GAME
            } else {
                gameEnded = false;
            }


            //DRAW HUD------------------------------------------------------------------------------
            if (gameEnded) {
                int[] clrs = {Color.argb(255, 175, 105, 58),
                        Color.argb(255, 252, 215, 166),
                        Color.argb(255, 164, 88, 47)};

                gradientPaint.setTextSize(screenX / 28);
                gradientPaint.setTextAlign(Paint.Align.CENTER);
                gradient = new LinearGradient(screenX / 2 - screenX / 7, screenY / 12 * 5, screenX / 2 + screenX / 7, screenY / 12 * 5,
                        clrs, null, Shader.TileMode.CLAMP);
                gradientPaint.setShader(gradient);
                canvas.drawText("You solve it!", screenX / 2, screenY / 12 * 5, gradientPaint);

                paint.setColor(Color.argb(255, 255, 255, 255));
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setTextSize(screenX/40);
                canvas.drawText("tap to continue", screenX / 2, screenY / 12 * 11, paint);
            } else {
                gradientPaint.setTextSize(screenX/40);
                gradientPaint.setTypeface(context.getResources().getFont(R.font.orbitron_regular));
                gradient = new LinearGradient(screenX / 16, screenY / 12, screenX / 16 * 4, screenY / 12,
                        Color.argb(255, 175, 105, 58),
                        Color.argb(255, 252, 215, 166), Shader.TileMode.CLAMP);
                gradientPaint.setShader(gradient);
                gradientPaint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("A", t1KernelX, screenY / 12 * 11, gradientPaint);
                canvas.drawText("B", t2KernelX, screenY / 12 * 11, gradientPaint);
                canvas.drawText("C", t3KernelX, screenY / 12 * 11, gradientPaint);
                gradientPaint.setTextAlign(Paint.Align.LEFT);

                SimpleDateFormat convertedTime = new SimpleDateFormat("mm:ss");
                Date resultdate = new Date(currentTime - timeStarted);
                canvas.drawText("time: " + convertedTime.format(resultdate), screenX / 16, screenY / 12, gradientPaint);
                canvas.drawText("moves: " + moves, screenX / 4, screenY / 12, gradientPaint);
            }
            /* DEBUG==========================================================
            paint.setTextSize(25);
            canvas.drawText("tower 1 updPos: "+ tower1.updPOS, 700, 50, paint);
            canvas.drawText("tower 1 discs: "+ tower1.towerSchematic[0] + " "+ tower1.towerSchematic[1] +
                    tower1.towerSchematic[2] + tower1.towerSchematic[3] + tower1.towerSchematic[4] +
                    tower1.towerSchematic[5] + tower1.towerSchematic[6] + tower1.towerSchematic[7], 700, 100, paint);
            canvas.drawText("SINGLE_DISC_ORDER: "+ Tower.SINGLE_DISC_ORDER, 700, 150, paint);
            // END OF DEBUG====================================================
            */

            //Rub the last frame
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    // SurfaceView allows us to handle the onTouchEvent
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        // There are many different events in MotionEvent
        // We care about just 2 - for now.
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            // Has the player lifted there finger up?
            case MotionEvent.ACTION_UP:
                motionX = motionEvent.getX();
                motionY = motionEvent.getY();
                //soundPool.play(dropDisc,0.3f,0.3f,0,0,1);
                if (tower1Area.contains(motionX, motionY) & Tower.SINGLE_DISC != null) {
                    if (tower1.updPOS == -1 || (tower1.towerSchematic[tower1.updPOS] < Tower.SINGLE_DISC_ORDER)) {
                        tower1.updPOS++;
                        tower1.discs[tower1.updPOS] = Tower.IMAGE_DATA[Tower.SINGLE_DISC_ORDER][tower1.updPOS];
                        tower1.towerSchematic[tower1.updPOS] = Tower.SINGLE_DISC_ORDER;
                        Tower.SINGLE_DISC = null;
                        Tower.SINGLE_DISC_ORDER = -1;
                        moves++;
                    } else {
                        Tower.LAST_TOWER.updPOS++;
                        Tower.LAST_TOWER.discs[Tower.LAST_TOWER.updPOS] = Tower.SINGLE_DISC;
                        Tower.LAST_TOWER.towerSchematic[Tower.LAST_TOWER.updPOS] = Tower.SINGLE_DISC_ORDER;
                        Tower.SINGLE_DISC = null;
                        Tower.SINGLE_DISC_ORDER = -1;
                        moves++;
                    }

                } else if (tower2Area.contains(motionX, motionY) & Tower.SINGLE_DISC != null) {
                    if (tower2.updPOS == -1 || (tower2.towerSchematic[tower2.updPOS] < Tower.SINGLE_DISC_ORDER)) {
                        tower2.updPOS++;
                        tower2.discs[tower2.updPOS] = Tower.IMAGE_DATA[Tower.SINGLE_DISC_ORDER][tower2.updPOS];
                        tower2.towerSchematic[tower2.updPOS] = Tower.SINGLE_DISC_ORDER;
                        Tower.SINGLE_DISC = null;
                        Tower.SINGLE_DISC_ORDER = -1;
                        moves++;
                    } else {
                        Tower.LAST_TOWER.updPOS++;
                        Tower.LAST_TOWER.discs[Tower.LAST_TOWER.updPOS] = Tower.SINGLE_DISC;
                        Tower.LAST_TOWER.towerSchematic[Tower.LAST_TOWER.updPOS] = Tower.SINGLE_DISC_ORDER;
                        Tower.SINGLE_DISC = null;
                        Tower.SINGLE_DISC_ORDER = -1;
                        moves++;
                    }
                } else if (tower3Area.contains(motionX, motionY) & Tower.SINGLE_DISC != null) {
                    if (tower3.updPOS == -1 || (tower3.towerSchematic[tower3.updPOS] < Tower.SINGLE_DISC_ORDER)) {
                        tower3.updPOS++;
                        tower3.discs[tower3.updPOS] = Tower.IMAGE_DATA[Tower.SINGLE_DISC_ORDER][tower3.updPOS];
                        tower3.towerSchematic[tower3.updPOS] = Tower.SINGLE_DISC_ORDER;
                        Tower.SINGLE_DISC = null;
                        Tower.SINGLE_DISC_ORDER = -1;
                        moves++;
                    } else {
                        Tower.LAST_TOWER.updPOS++;
                        Tower.LAST_TOWER.discs[Tower.LAST_TOWER.updPOS] = Tower.SINGLE_DISC;
                        Tower.LAST_TOWER.towerSchematic[Tower.LAST_TOWER.updPOS] = Tower.SINGLE_DISC_ORDER;
                        Tower.SINGLE_DISC = null;
                        Tower.SINGLE_DISC_ORDER = -1;
                        moves++;
                    }
                } else {
                    if (Tower.SINGLE_DISC != null) {
                        Tower.LAST_TOWER.updPOS++;
                        Tower.LAST_TOWER.discs[Tower.LAST_TOWER.updPOS] = Tower.SINGLE_DISC;
                        Tower.LAST_TOWER.towerSchematic[Tower.LAST_TOWER.updPOS] = Tower.SINGLE_DISC_ORDER;
                        Tower.SINGLE_DISC = null;
                        Tower.SINGLE_DISC_ORDER = -1;
                        moves++;
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:
                motionX = motionEvent.getX();
                motionY = motionEvent.getY();
                if (Tower.SINGLE_DISC != null) {
                    Tower.SINGLE_DISC_X = motionX - Tower.SINGLE_DISC.getWidth() / 2;
                    Tower.SINGLE_DISC_Y = motionY - Tower.SINGLE_DISC.getHeight() / 2;
                }
                break;
            // Has the player touched the screen?
            case MotionEvent.ACTION_DOWN:
                if (gameEnded) {
                    Intent myIntent = new Intent(context, MainActivity.class);
                    context.startActivity(myIntent);
                } else {
                    motionX = motionEvent.getX();
                    motionY = motionEvent.getY();
                    if (tower1Area.contains(motionX, motionY)) {
                        //soundPool.play(takeDisc,0.3f,0.3f,0,0,1);
                        if (tower1.updPOS != -1) {
                            Tower.LAST_TOWER = tower1;
                            Tower.SINGLE_DISC = tower1.discs[tower1.updPOS];
                            Tower.SINGLE_DISC_ORDER = tower1.towerSchematic[tower1.updPOS];
                            if (Tower.SINGLE_DISC != null) {
                                Tower.SINGLE_DISC_X = motionX - Tower.SINGLE_DISC.getWidth() / 2;
                                Tower.SINGLE_DISC_Y = motionY - Tower.SINGLE_DISC.getHeight() / 2;
                            }
                            tower1.discs[tower1.updPOS] = null;
                            tower1.towerSchematic[tower1.updPOS] = -1;
                            tower1.updPOS--;
                        }
                    } else if (tower2Area.contains(motionX, motionY)) {
                        //soundPool.play(takeDisc,0.3f,0.3f,0,0,1);
                        if (tower2.updPOS != -1) {
                            Tower.LAST_TOWER = tower2;
                            Tower.SINGLE_DISC = tower2.discs[tower2.updPOS];
                            Tower.SINGLE_DISC_ORDER = tower2.towerSchematic[tower2.updPOS];
                            if (Tower.SINGLE_DISC != null) {
                                Tower.SINGLE_DISC_X = motionX - Tower.SINGLE_DISC.getWidth() / 2;
                                Tower.SINGLE_DISC_Y = motionY - Tower.SINGLE_DISC.getHeight() / 2;
                            }
                            tower2.discs[tower2.updPOS] = null;
                            tower2.towerSchematic[tower2.updPOS] = -1;
                            tower2.updPOS--;
                        }
                    } else if (tower3Area.contains(motionX, motionY)) {
                        //soundPool.play(takeDisc,0.3f,0.3f,0,0,1);
                        if (tower3.updPOS != -1) {
                            Tower.LAST_TOWER = tower3;
                            Tower.SINGLE_DISC = tower3.discs[tower3.updPOS];
                            Tower.SINGLE_DISC_ORDER = tower3.towerSchematic[tower3.updPOS];
                            if (Tower.SINGLE_DISC != null) {
                                Tower.SINGLE_DISC_X = motionX - Tower.SINGLE_DISC.getWidth() / 2;
                                Tower.SINGLE_DISC_Y = motionY - Tower.SINGLE_DISC.getHeight() / 2;
                            }
                            tower3.discs[tower3.updPOS] = null;
                            tower3.towerSchematic[tower3.updPOS] = -1;
                            tower3.updPOS--;
                        }
                    } else {

                    }
                    break;
                }
        }
        return true;
    }

    // CONTROL
    private void control() {
        try {    gameThread.sleep(17);
        } catch (InterruptedException e) {
        }
    }


    // Clean up our thread if the game is interrupted or
    // the player quits
    public void pause() {
        playing = false;
        try {
            //soundPool.release();
            //soundPool = null;
            gameThread.join();
        } catch( InterruptedException e) {

        }
    }

    // Make a new thread and start it
    // Execution moves to our R
    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

}
