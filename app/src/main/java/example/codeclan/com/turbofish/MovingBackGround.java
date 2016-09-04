package example.codeclan.com.turbofish;

/**
 * Created by user on 11/08/2016.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MovingBackGround extends SurfaceView implements
        SurfaceHolder.Callback {
    private Bitmap backGround;

    public MovingBackGround(Context context) {
        super(context);
        backGround = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.undersea);
        setWillNotDraw(false);
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        doDrawRunning(canvas);
        invalidate();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    /**
     * Draws current state of the game Canvas.
     */

    private int mBGFarMoveX = 0;
    private int mBGNearMoveX = 0;

    private void doDrawRunning(Canvas canvas) {

        // decrement the far background
        mBGFarMoveX = mBGFarMoveX - 1;

        // decrement the near background
        mBGNearMoveX = mBGNearMoveX - 4;

        // calculate the wrap factor for matching image draw
        int newFarX = backGround.getWidth() - (-mBGFarMoveX);

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
}
