package com.viktorfursov.toh;

import java.util.Random;

public class SpaceDust {
    private int x, y;
    private int speed;
    // Detect dust leaving the screen
    private int maxX;
    private int maxY;
    private int minX;
    private int minY;

    // Constructor
    public SpaceDust(int screenX, int screenY) {
        maxX = screenX;
        maxY = screenY;
        minX = 0;
        minY = 0;
        //Set speed
        Random generator = new Random();
        speed = generator.nextInt(10);

        //  Set the starting coordinates
        x = generator.nextInt(maxX);
        y = generator.nextInt((maxY/3)*2);
    }

    public void update() {
        // Speed up when the player does
        x -= 1;
        //respawn space dust
        if (x < 0) {
            x = maxX;
            Random generator = new Random();
            y = generator.nextInt((maxY/3)*2);
            speed = generator.nextInt(15);
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}