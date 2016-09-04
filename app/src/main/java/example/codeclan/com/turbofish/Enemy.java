package example.codeclan.com.turbofish;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import java.util.Random;

/**
 * Created by user on 07/08/2016.
 */

public class Enemy {

    RectF rect;

    // Enemy bitmap
    private Bitmap bitmap1;
    private Bitmap bitmap2;


    //Size of enemy sprite
    private float length;
    private float height;

    // X is the far left of the rectangle which forms our fish sprite
    private float x;

    // Y is the top coordinate
    private float y;

    // This will hold the pixels per second speed that the fish sprite will move
    private float enemySpeed;

    // Which ways can the enemy Fish move
    public final int STOPPED = 0;

    // Is the enemy moving and in which direction
    private int enemyMoving = STOPPED;

    boolean isVisible;

    private boolean isActive;



    //Enemy constructor
    public Enemy(Context context, int screenX, int screenY){

        // Initialize a blank RectF
        rect = new RectF();

        length = 100;
        height = 70;

        isVisible = true;

        //randomize enemy spawn location
        x = getRandomNumberInRange(500, screenX*10);
        y = getRandomNumberInRange(50, screenY-100);

        // Initialize the bitmaps
        bitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.shark);
        bitmap2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.shark_open);


        // stretch the first bitmap to a size appropriate for the screen resolution
        bitmap1 = Bitmap.createScaledBitmap(bitmap1,
                (int) (length),
                (int) (height),
                false);

        // stretch the first bitmap to a size appropriate for the screen resolution
        bitmap2 = Bitmap.createScaledBitmap(bitmap2,
                (int) (length),
                (int) (height),
                false);


        // How fast is the invader in pixels per second
        enemySpeed = 100;
    }

    public void setInvisible(){
        isVisible = false;
    }

    public boolean getVisibility(){
        return isVisible;
    }

    public RectF getRect(){
        return rect;
    }

    //Getters
    public Bitmap getBitmap1(){
        return bitmap1;
    }

    public Bitmap getBitmap2(){
        return bitmap2;
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }

    public boolean getStatus(){
        return isActive;
    }
    public void setInactive(){
        isActive = false;
    }

    private static int getRandomNumberInRange(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }



    //Updates
    public void update(long fps){

        x = x - enemySpeed / fps;

        rect.top = y;
        rect.bottom = y + height;
        rect.left = x;
        rect.right = x + length;
    }
}


