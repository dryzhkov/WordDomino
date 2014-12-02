package com.atobia.worddomino;

import android.graphics.Canvas;
import android.util.Log;

/**
 * Created by dima on 11/30/2014.
 */
public class GameLoop extends Thread{
    private enum GameState {Start, Mid, End, Done}; //this might need to be exposed later
    private boolean shouldRun = false;
    private final int FPS = 60;
    private GameState curGameState;
    private long startTime;
    private GameView view;
    private Canvas c;
    public GameLoop(GameView view){
        this.shouldRun = true;
        this.view = view;
        this.curGameState = GameState.Start;
    }

    public void setShouldRun(boolean run){
        //attempt to interrupt the main loop
        this.shouldRun = run;
    }

    @Override
    public void run(){
        long sleepMilliSec = 1000 / FPS;
        this.startTime = System.currentTimeMillis();
        while(shouldRun){
            //switch from one state to another
            switch (this.curGameState){
                case Start:
                    TransitionToMid();
                    break;
                case Mid:
                    TransitionToEnd();
                    break;
                case End:
                    EndGame();
                    break;
                default:
                    break;
            }
            //do some extra work, example draw stuff
            try {
                c = view.getHolder().lockCanvas();
                synchronized (view.getHolder()) {
                    view.draw(c);
                }
            } finally {
                if (c != null) {
                    view.getHolder().unlockCanvasAndPost(c);
                }
            }

            try{
                sleep(sleepMilliSec, 0);
            } catch (InterruptedException e) {
                Log.e(e.toString(), "GameLoop_Run");
            }
        }
    }

    private void TransitionToMid(){
        if(System.currentTimeMillis() - startTime > 5000) {
            view.ShowToast(1);
            curGameState = GameState.Mid;
        }
    }

    private void TransitionToEnd(){
        if(System.currentTimeMillis() - startTime > 10000) {
            view.ShowToast(2);
            curGameState = GameState.End;
        }
    }

    private void EndGame(){
        if(System.currentTimeMillis() - startTime > 15000) {
            view.ShowToast(3);
            curGameState = GameState.Done;
        }
    }
}


