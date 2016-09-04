package example.codeclan.com.turbofish;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;


/**
 * Created by user on 07/08/2016.
 */

public class PlayerFish {

    RectF rect;

    // The playerFish will be represented by a Bitmap
    private Bitmap bitmap1;
    private Bitmap bitmap2;

    // Screen Size
    private float screenSizeY;
    private float screenSizeX;

    // Length and height of sprite
    private float length;
    private float height;

    // X is the far left of the rectangle which forms the fish sprite
    private float x;

    // Y is the top coordinate
    private float y;

    // This will hold the pixels per second speed that the fish sprite will move
    private float playerSpeed;

    // Fish sprite status

    private boolean isActive;

    // Which ways can the playerFish move
    public final int STOPPED = 0;
    public final int UP = 1;


    // Is the playerFish moving and in which direction
    private int playerMoving = STOPPED;



    // PlayerFish constructor method
    public PlayerFish(Context context, int screenX, int screenY){

        // Initialize a blank RectF
        rect = new RectF();

        // Define size of sprite
        length = 70;
        height = 50;

        // Screen size passed in and converted
        screenSizeX = screenX;
        screenSizeY = screenY;

        // Start playerFish in roughly the screen centre
        x = screenX / 4;
        y = screenY / 2;

        // Initialize the bitmaps
        bitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.fish_bitmap1);
        bitmap2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.fish_bitmap2);

        //stretch the bitmap to a size appropriate for the screen resolution
        bitmap1 = Bitmap.createScaledBitmap(bitmap1,
                (int) (length),
                (int) (height),
                false);

        //stretch the bitmap to a size appropriate for the screen resolution
        bitmap2 = Bitmap.createScaledBitmap(bitmap2,
                (int) (length),
                (int) (height),
                false);

        // How fast is the playerShip in pixels per second
        playerSpeed = 200;
    }

    public RectF getRect(){
        return rect;
    }

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




    //  Check if player is moving and in which direction
    public void setMovementState(int state){
        playerMoving = state;
    }



    // All the updates
    public void update(long fps){
        if (playerMoving == UP && y > 40) {
                y = y - playerSpeed / fps;
            }

        if(playerMoving == STOPPED && y < screenSizeY -20) {
                y = y + playerSpeed / fps;
            }

        //Update rect which is used to detect hits
        rect.top = y;
        rect.bottom = y;
        rect.left = x;
        rect.right = x + length;
    }
}

