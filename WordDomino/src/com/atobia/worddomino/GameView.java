package com.atobia.worddomino;

/**
 * Created by dima on 12/1/2014.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.MotionEvent;
import android.widget.Toast;


public class GameView extends SurfaceView{
    private Bitmap bmp;
    private SurfaceHolder holder;
    private GameLoop gameLoopThread;
    private int x = 0;
    private Handler handler;
    private Context context;

    public GameView(Context c) {
        super(c);
        this.context = c;
        handler = new Handler();
        gameLoopThread = new GameLoop(this);
        holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                gameLoopThread.setShouldRun(false);
                while (true) {
                    try {
                        gameLoopThread.join();
                        break;
                    } catch (InterruptedException e) {}
                }
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                gameLoopThread.setShouldRun(true);
                gameLoopThread.start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
            }
        });
        setFocusable(true);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
        if (x < getWidth() - bmp.getWidth()) {
            x++;
        }
        canvas.drawBitmap(bmp, x, 10, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public void ShowToast(int flag){
        switch(flag) {
            case 1:
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Current: Start - Going to Mid!", Toast.LENGTH_LONG).show();
                    }
                });
                break;
            case 2:
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Current: Mid - Going to End!", Toast.LENGTH_LONG).show();
                    }
                });
                break;
            case 3:
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Current: End - Stopping Game!", Toast.LENGTH_LONG).show();
                    }
                });
                break;
            default:
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Unexpected! Shouldn't be here", Toast.LENGTH_LONG).show();
                    }
                });
                break;
        }
    }
}
