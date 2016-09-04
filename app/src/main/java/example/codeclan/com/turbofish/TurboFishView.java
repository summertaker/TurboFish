package example.codeclan.com.turbofish;

/**
 * Created by user on 07/08/2016.
 */
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.IOException;
import java.util.Random;


// Implement runnable so we have
// A thread and can override the run method.
class TurboFishView extends SurfaceView implements Runnable {

    private Context context;

        // This is our thread
    private Thread gameThread = null;

    //Lock surface for use
    private SurfaceHolder ourHolder;

//    Instantiate the playerFish
    private PlayerFish playerFish;

    private MovingBackGround bg_image;

    private Bitmap backGround;

    // A boolean which we will set and unset
    // when the game is running- or not.
    volatile boolean playing;

    // For sound FX
    private SoundPool soundPool;
    private int playerExplodeID = -1;
    private int uhID = -1;
    private int ohID = -1;
    private int crowd_cheerID = -1;
    private int ouchID = -1;
    private int wilhemlmID = -1;

    private int mBGFarMoveX = 0;
    private int mBGNearMoveX = 0;

    // The score
    private int score = 0;

    // Lives
    private int lives = 3;

    // How menacing should the sound be?
    private long menaceInterval = 1000;

    // Which menace sound should play next
    private boolean uhOrOh;

    // Last played menace sound
    private long lastMenaceTime = System.currentTimeMillis();

    // The size of the screen in pixels
    private int screenX;
    private int screenY;

    // A Canvas and a Paint object
    Canvas canvas;
    Paint paint;

    // This variable tracks the game frame rate
    long fps;

    // help calculate the fps
    private long timeThisFrame;

    // Game is paused at the start
    private boolean paused = true;

    // Up to 500 enemies
    private Enemy[] enemies = new Enemy[150];
    private int numEnemies = 0;

    // Up to 5 extra lives
    private ExtraLife[] extraLives = new ExtraLife[5];
    private int numExtraLives = 0;

    // Up to 500 octopi
    private Octopus[] octopi = new Octopus[100];
    private int numOctopi = 0;

    // Up to 500 snakes
    private Snake[] snakes = new Snake[100];
    private int numSnakes = 0;


    // TurboFishView Constructor method
    public TurboFishView(Context context, int x, int y) {

        //SurfaceView Super class gets context.
        super(context);

        // Make context globally available
        this.context = context;

        // Initialize ourHolder and paint objects
        ourHolder = getHolder();
        paint = new Paint();

        backGround = BitmapFactory.decodeResource(context.getResources(), R.drawable.undersea);

        bg_image = new MovingBackGround(context);


        screenX = x;
        screenY = y;

        // Set our boolean to true - game on!
        playing = true;

        // This SoundPool is deprecated but don't worry
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,0);

        try{
            // Create objects of the 2 required classes
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            // Load our fx in memory ready for use
            descriptor = assetManager.openFd("playerexplode.ogg");
            playerExplodeID = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("uh.ogg");
            uhID = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("oh.ogg");
            ohID = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("crowd_cheer.mp3");
            crowd_cheerID = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("ouch.mp3");
            ouchID = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("wilhemlm.ogg");
            wilhemlmID = soundPool.load(descriptor, 0);



        }catch(IOException e){
            // Print an error message to the console
            Log.e("error", "failed to load sound files");
        }


        prepareLevel();

    }



    private void prepareLevel() {

        // Initialize all the game objects

        // Make a new player fish
        playerFish = new PlayerFish(context, screenX, screenY);

        // Build an army of Enemies
        numEnemies = 0;
        for (int column = 0; column < 7; column++) {
            for (int row = 0; row < 5; row++) {
                enemies[numEnemies] = new
                        Enemy(context, screenX, screenY);
                numEnemies++;
            }
        }

        // Build an army of Octopi
        numOctopi = 0;
        for (int column = 0; column < 7; column++) {
            for (int row = 0; row < 5; row++) {
                octopi[numOctopi] = new
                        Octopus(context, screenX, screenY);
                numOctopi++;
            }
        }

        // Build an army of Snakes
        numSnakes = 0;
        for (int column = 0; column < 7; column++) {
            for (int row = 0; row < 5; row++) {
               snakes[numSnakes] = new
                        Snake(context, screenX, screenY);
                numSnakes++;
            }
        }

        // Build extra lives
        numExtraLives = 0;
        for (int column = 0; column < 5; column++) {
            for (int row = 0; row < 1; row++) {
                extraLives[numExtraLives] = new
                        ExtraLife(context, screenX, screenY);
                numExtraLives++;
            }
        }


    }

    @Override
    public void run() {
        while (playing) {

            // Capture the current time in milliseconds in startFrameTime
            long startFrameTime = System.currentTimeMillis();

            // Update the frame
            if(!paused){
                update();
            }

            // Draw the frame
            draw();

            //Calculate the fps to time animations
            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame > 0) {
                fps = 1000 / timeThisFrame;
            }

            // Play a sound based on the menace level
            if(!paused) {
                if ((startFrameTime - lastMenaceTime)> menaceInterval) {
                    if (uhOrOh) {
                        // Play Uh
                        soundPool.play(uhID, 1, 1, 0, 0, 1);

                    } else {
                        // Play Oh
                        soundPool.play(ohID, 1, 1, 0, 0, 1);
                    }

                    // Reset the last menace time
                    lastMenaceTime = System.currentTimeMillis();
                    // Alter value of uhOrOh
                    uhOrOh = !uhOrOh;
                }
            }

        }

    }

    //Everything that needs updated
    public void update() {


        playerFish.update(fps);

        // Update all the sharks if visible
        for(int i = 0; i < numEnemies; i++) {
            if (enemies[i].getVisibility()) {
                // Move the next shark
                enemies[i].update(fps);
            }
        }

        // Update all the octopi if visible
        for(int i = 0; i < numOctopi; i++) {
            if (octopi[i].getVisibility()) {
                // Move the next shark
                octopi[i].update(fps);
            }
        }

        // Update all the snakes if visible
        for(int i = 0; i < numSnakes; i++) {
            if (snakes[i].getVisibility()) {
                // Move the next shark
                snakes[i].update(fps);
            }
        }

        // Update all the extra lives if visible
        for(int i = 0; i < numExtraLives; i++) {
            if (extraLives[i].getVisibility()) {
                // Move the next shark
                extraLives[i].update(fps);
            }
        }

        // Has player hit a shark
            for(int i = 0; i < numEnemies; i++){
                if(enemies[i].getVisibility()){
                    if(RectF.intersects(playerFish.getRect(), enemies[i].getRect())){
                        // A collision has occurred
                        enemies[i].setInvisible();
                        lives = lives -1;
                        soundPool.play(wilhemlmID, 1, 1, 0, 0, 1);
                        // Is it game over?
                        if(lives == 0){
                            paused = true;
                            lives = 3;
                            score = 0;
                            prepareLevel();

                        }
                    }
                }
            }

        // Has player hit a snake
        for(int i = 0; i < numSnakes; i++){
            if(snakes[i].getVisibility()){
                if(RectF.intersects(playerFish.getRect(), snakes[i].getRect())){
                    // A collision has occurred
                    snakes[i].setInvisible();
                    lives = lives -1;
                    soundPool.play(wilhemlmID, 1, 1, 0, 0, 1);
                    // Is it game over?
                    if(lives == 0){
                        paused = true;
                        lives = 3;
                        score = 0;
                        prepareLevel();

                    }
                }
            }
        }


        // Has player hit an octopus
        for(int i = 0; i < numOctopi; i++){
            if(octopi[i].getVisibility()){
                if(RectF.intersects(playerFish.getRect(), octopi[i].getRect())){
                    // A collision has occurred
                    octopi[i].setInvisible();
                    lives = lives -1;
                    soundPool.play(wilhemlmID, 1, 1, 0, 0, 1);
                    // Is it game over?
                    if(lives == 0){
                        paused = true;
                        lives = 3;
                        score = 0;
                        prepareLevel();

                    }
                }
            }
        }

        // Has player hit an extra life
        for(int i = 0; i < numExtraLives; i++){
            if(extraLives[i].getVisibility()){
                if(RectF.intersects(playerFish.getRect(), extraLives[i].getRect())){
                    // A collision has occurred
                    extraLives[i].setInvisible();
                    lives = lives +1;
                    soundPool.play(crowd_cheerID, 1, 1, 0, 0, 1);
                }
            }
        }

//        Has player scored?
            for (int i =0; i < numEnemies; i++){
                if(enemies[i].getVisibility()){
                    if(enemies[i].getX() <= screenX/4){
                        score = score + 10;
                    }
                }
            }
    }


    private static int getRandomNumberInRange(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    private void doDrawRunning(Canvas canvas) {
        // decrement the far background
        mBGFarMoveX = mBGFarMoveX - 1;
        // decrement the near background
        mBGNearMoveX = mBGNearMoveX - 4;
        // calculate the wrap factor for matching image draw
        int newFarX = bg_image.getWidth() - (-mBGFarMoveX);
        // if we have scrolled all the way, reset to start
        if (newFarX <= 0) {
            mBGFarMoveX = 0;
            // only need one draw
            canvas.drawBitmap(backGround, mBGFarMoveX, 0, null);
        } else {
            // need to draw original and wrap
            canvas.drawBitmap(backGround, mBGFarMoveX, 0, null);
            canvas.drawBitmap(backGround, newFarX, 0, null);
        }
    }

    // Draw the newly updated scene
    public void draw() {

        // Make sure our drawing surface is valid
        if (ourHolder.getSurface().isValid()) {
            // Lock the canvas ready to draw
            canvas = ourHolder.lockCanvas();

            // Draw the background color
//            canvas.drawColor(Color.argb(255, 120, 160, 138));
            doDrawRunning(canvas);



            // Choose the brush color for drawing
            paint.setColor(Color.argb(255,  255, 255, 255));

            // Now draw the player fish

            for(int i = 0; i < numEnemies; i++) {
                if (enemies[i].getVisibility()) {
                    if (uhOrOh) {
                        canvas.drawBitmap(playerFish.getBitmap1(), playerFish.getX(), playerFish.getY() - 50, paint);
                    } else {
                        canvas.drawBitmap(playerFish.getBitmap2(), playerFish.getX(), playerFish.getY() - 50, paint);
                    }
                }
            }

            // Draw the enemies
            for(int i = 0; i < numEnemies; i++) {
                if (enemies[i].getVisibility()) {
                    if(uhOrOh) {
                    canvas.drawBitmap(enemies[i].getBitmap1(), enemies[i].getX(), enemies[i].getY(), paint);
                }else{
                    canvas.drawBitmap(enemies[i].getBitmap2(), enemies[i].getX(), enemies[i].getY(), paint);
                    }
                }
            }

            // Draw the octopi
            for(int i = 0; i < numOctopi; i++) {
                if (octopi[i].getVisibility()) {
                    if(uhOrOh) {
                        canvas.drawBitmap(octopi[i].getBitmap1(), octopi[i].getX(), octopi[i].getY(), paint);
                    }else{
                        canvas.drawBitmap(octopi[i].getBitmap2(), octopi[i].getX(), octopi[i].getY(), paint);
                    }
                }
            }

            // Draw the snakes
            for(int i = 0; i < numSnakes; i++) {
                if (snakes[i].getVisibility()) {
                    if(uhOrOh) {
                        canvas.drawBitmap(snakes[i].getBitmap1(), snakes[i].getX(), snakes[i].getY(), paint);
                    }else{
                        canvas.drawBitmap(snakes[i].getBitmap2(), snakes[i].getX(), snakes[i].getY(), paint);
                    }
                }
            }

            // Draw the extra lives
            for(int i = 0; i < numExtraLives; i++) {
                if (extraLives[i].getVisibility()) {
                    if(uhOrOh) {
                        canvas.drawBitmap(extraLives[i].getBitmap1(), extraLives[i].getX(), extraLives[i].getY(), paint);
                    }else{
                        canvas.drawBitmap(extraLives[i].getBitmap2(), extraLives[i].getX(), extraLives[i].getY(), paint);
                    }
                }
            }

            // Make the text a bit bigger
            paint.setTextSize(45);

//            // Display the current fps on the screen
//            canvas.drawText("FPS:" + fps, 20, 40, paint);
//
//            // Display the current player y position on the screen
//            canvas.drawText("Y:" + playerFish.getY(), 20, 100, paint);

            // Draw the score and remaining lives
            // Change the brush color
            paint.setColor(Color.argb(255,  0, 0, 0));
            paint.setTextSize(40);
            canvas.drawText("Score: " + score + "   Lives: " + lives, 10,50, paint);


            // Draw everything to the screen
            ourHolder.unlockCanvasAndPost(canvas);
        }

    }

    // If GameEngine Activity is paused/stopped
    // shutdown thread.
    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }

    }

    // If GameEngine Activity is started then
    // start our thread.
    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    // The SurfaceView class implements onTouchListener
    // So we can override this method and detect screen touches.
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

            // Player has touched the screen
            case MotionEvent.ACTION_DOWN:
                paused = false;
                playerFish.setMovementState(playerFish.UP);
                break;

            // Player has removed finger from screen
            case MotionEvent.ACTION_UP:
                playerFish.setMovementState(playerFish.STOPPED);

                break;
        }
        return true;
    }






}



