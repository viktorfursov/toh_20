package com.viktorfursov.toh;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class Tower {

    /* Class's instance holds arrays of discs
    and manipulate them.
     */
    // STATIC BLOCK---------------------------------------------------------------------------------
    // IMAGE_DATA holds all available 8 perspective positions of
    // each of all 8 discs
    public static final Bitmap[][] IMAGE_DATA = new Bitmap[8][];
    private static boolean init_data;
    public static Bitmap SINGLE_DISC;
    public static float SINGLE_DISC_X;
    public static float SINGLE_DISC_Y;
    public static int SINGLE_DISC_ORDER;
    public  static Tower LAST_TOWER; // tower from single disc was taken

    static {
        init_data =false;
        SINGLE_DISC = null;
        SINGLE_DISC_ORDER = -1;
        LAST_TOWER = null;
        /*
        Load all discs into the base IMAGE_DATA
         */
        for (int i=0; i<IMAGE_DATA.length; i++) {
            IMAGE_DATA[i] = new Bitmap[i+1];
        }
    }
    // END OF STATIC BLOCK--------------------------------------------------------------------------

    public Bitmap [] discs;
    public int [] towerSchematic;
    public int updPOS;

    private static int x;
    private static int y;

    // Method of conversion graphics for different screens
    public static Bitmap getResizedBitmap(Bitmap bm) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth;
        float scaleHeight;

        scaleWidth = ((float) x)/4 / 2880;
        scaleHeight = ((float) y)/6 / 1440;

        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);
        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    // CONSTRUCTOR
    public Tower(Context context, int updPOS, int screenX, int screenY) {
        // Set screen resolution
        x = screenX;
        y = screenY;

        //allocate storage for discs and initialize a tower schematic
        discs = new Bitmap[8];
        towerSchematic = new int [8];
        for (int i=0; i < discs.length; i++) {
            towerSchematic[i] = -1;
        }
        // set initial update position of last disc, this is 7 for tower1 and 0 for tower2 and tower3
        this.updPOS = updPOS;

        // Initialize static data
        if (!Tower.init_data) {

            IMAGE_DATA[0][0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tower340_0);

            IMAGE_DATA[1][0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tower300_0);
            IMAGE_DATA[1][1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tower300_1);

            IMAGE_DATA[2][0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tower260_0);
            IMAGE_DATA[2][1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tower260_1);
            IMAGE_DATA[2][2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tower260_2);

            IMAGE_DATA[3][0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tower220_0);
            IMAGE_DATA[3][1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tower220_1);
            IMAGE_DATA[3][2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tower220_2);
            IMAGE_DATA[3][3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tower220_3);

            IMAGE_DATA[4][0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tower180_0);
            IMAGE_DATA[4][1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tower180_1);
            IMAGE_DATA[4][2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tower180_2);
            IMAGE_DATA[4][3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tower180_3);
            IMAGE_DATA[4][4] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tower180_4);

            IMAGE_DATA[5][0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tower140_0);
            IMAGE_DATA[5][1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tower140_1);
            IMAGE_DATA[5][2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tower140_2);
            IMAGE_DATA[5][3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tower140_3);
            IMAGE_DATA[5][4] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tower140_4);
            IMAGE_DATA[5][5] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tower140_5);

            IMAGE_DATA[6][0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tower100_0);
            IMAGE_DATA[6][1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tower100_1);
            IMAGE_DATA[6][2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tower100_2);
            IMAGE_DATA[6][3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tower100_3);
            IMAGE_DATA[6][4] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tower100_4);
            IMAGE_DATA[6][5] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tower100_5);
            IMAGE_DATA[6][6] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tower100_6);

            IMAGE_DATA[7][0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tower60_0);
            IMAGE_DATA[7][1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tower60_1);
            IMAGE_DATA[7][2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tower60_2);
            IMAGE_DATA[7][3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tower60_3);
            IMAGE_DATA[7][4] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tower60_4);
            IMAGE_DATA[7][5] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tower60_5);
            IMAGE_DATA[7][6] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tower60_6);
            IMAGE_DATA[7][7] = BitmapFactory.decodeResource(context.getResources(), R.drawable.tower60_7);

            // Scaling images for different screens
            for (int i=0; i < IMAGE_DATA.length; i++) {
                for (int z=0; z < IMAGE_DATA[i].length; z++) {
                    IMAGE_DATA[i][z] = Tower.getResizedBitmap(IMAGE_DATA[i][z]);
                }
            }
            init_data = true;
        }
    }

    public void update(String FLAG, int sourceTower) {
        /*/ modify disc's array
        if (FLAG.equals("DOWN")) {
            if (updPOS != -1) {
                LAST_TOWER = sourceTower;
                SINGLE_DISC = discs[updPOS];
                SINGLE_DISC_ORDER = towerSchematic[updPOS];
                discs[updPOS] = null;
                towerSchematic[updPOS] = -1;
                updPOS--;
            }
        } else if (FLAG.equals("UP")) {
            if (updPOS == -1 || (towerSchematic[updPOS] > SINGLE_DISC_ORDER)) {
                ++updPOS;
                discs[updPOS] = IMAGE_DATA[SINGLE_DISC_ORDER][updPOS];
            } else {

            }
        }*/
    }

    private void scaleImages() {
    }

    public void fill() {
        /*
        Initially fills one of the tower with all 8 discs
         */
        for (int i=0, z=0; i<IMAGE_DATA.length;) {
            discs[i] = IMAGE_DATA[i][z];
            i++;
            z++;
        }
        for (int i=0; i<discs.length; i++) {
            towerSchematic[i] = i;
        }
    }

}
