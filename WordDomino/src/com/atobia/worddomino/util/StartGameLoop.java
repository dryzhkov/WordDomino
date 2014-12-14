package com.atobia.worddomino.util;

import android.os.SystemClock;

import com.atobia.worddomino.StartGame;

public class StartGameLoop extends Thread {
    private final int FPS = 10;
    private StartGame view;

    public StartGameLoop(StartGame view) {
        this.view = view;
    }

    @Override
    public void run() {
        long sleepMilliSec = 1000 / FPS;
        this.view.startTime = System.currentTimeMillis();

        int timesSleptInARow = 0;

        // Keep on going until the game is over
        while (!this.view.game.isGameOver) {
            switch (this.view.game.CurrentState) {
                case ASK_FOR_WORD:
                    timesSleptInARow = 0;
                    this.view.game.CurrentState = EnumGameState.WORKING_SHORT_SLEEP;
                    this.view.AskForWord();
                    break;

                case LISTEN_FOR_WORD:
                    timesSleptInARow = 0;
                    this.view.game.CurrentState = EnumGameState.WORKING_SHORT_SLEEP;

                    // Starting the Listener requires being on the view thread
                    this.view.ListenForWord();
                    break;

                case PROCESS_ANSWER:
                    timesSleptInARow = 0;
                    this.view.game.CurrentState = EnumGameState.WORKING_SHORT_SLEEP;
                    this.view.ProcessAnswer();
                    break;

                case RETORT:
                    timesSleptInARow = 0;
                    this.view.game.CurrentState = EnumGameState.WORKING_SHORT_SLEEP;
                    this.view.Retort();
                    break;

                case WORKING_SHORT_SLEEP:

                    // 500 sleeps is enough to know that something is wrong
                    if (500 < timesSleptInARow) {
                        SystemClock.sleep(sleepMilliSec);
                        timesSleptInARow++;
                    } else {
                        // We should decide what to do here. For now, this will do.
                        this.view.game.GameOver();
                    }
                    break;

                default:
                    this.view.game.CurrentState = EnumGameState.WORKING_SHORT_SLEEP;
            }
        }
    }
}
