package com.atobia.worddomino.util;

import android.os.SystemClock;
import android.util.Log;

import com.atobia.worddomino.StartGame;

public class StartGameLoop extends Thread {
    private final int FPS = 10;
    private StartGame view;
    public boolean shouldRun = true;

    public StartGameLoop(StartGame view) {
        this.view = view;
    }

    @Override
    public void run() {
        long sleepMilliSec = 1000 / FPS;
        this.view.startTime = System.currentTimeMillis();

        // Keep on going until the game is over
        while (this.shouldRun) {
            switch (this.view.game.CurrentState) {
                case NEW_GAME:
                    this.view.game.CurrentState = EnumGameState.WORKING_SHORT_SLEEP;
                    this.view.CountDown();
                    break;
                case ASK_FOR_WORD:
                    this.view.game.CurrentState = EnumGameState.WORKING_SHORT_SLEEP;
                    this.view.AskForWord();
                    break;
                case LISTEN_FOR_WORD:
                    this.view.game.CurrentState = EnumGameState.WORKING_SHORT_SLEEP;
                    this.view.ListenForWord();
                    break;
                case PROCESS_ANSWER:
                    this.view.game.CurrentState = EnumGameState.WORKING_SHORT_SLEEP;
                    this.view.ProcessAnswer();
                    break;
                case RETORT:
                    this.view.game.CurrentState = EnumGameState.WORKING_SHORT_SLEEP;
                    this.view.Retort();
                    break;
                case GAME_OVER:
                    this.shouldRun = false;
                    this.view.game.GameOver();
                    break;
                case NEXT_ROUND:
                    this.view.game.CurrentState = EnumGameState.WORKING_SHORT_SLEEP;
                    this.view.NextRound();
                    break;
                case WORKING_SHORT_SLEEP:
                    SystemClock.sleep(sleepMilliSec);
                    break;
                default:
                    this.view.game.CurrentState = EnumGameState.WORKING_SHORT_SLEEP;
            }
        }
    }
}
