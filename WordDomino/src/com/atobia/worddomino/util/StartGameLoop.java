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

        // Keep on going until the game is over
        while (this.shouldRun) {
            switch (this.view.game.CurrentState) {
                case NEW_GAME:
                    Log.d("state change", "NEW GAME");
                    this.view.game.CurrentState = EnumGameState.WORKING_SHORT_SLEEP;
                    this.view.CountDown();
                    break;
                case ASK_FOR_WORD:
                    Log.d("state change", "ASK FOR WORD");
                    this.view.game.CurrentState = EnumGameState.WORKING_SHORT_SLEEP;
                    this.view.AskForWord();
                    break;
                case LISTEN_FOR_WORD:
                    Log.d("state change", "LISTEN FOR WORD");
                    this.view.game.CurrentState = EnumGameState.WORKING_SHORT_SLEEP;
                    this.view.ListenForWord();
                    break;
                case WAIT_FOR_TYPING:
                    Log.d("state change", "LISTEN FOR WORD");
                    this.view.game.CurrentState = EnumGameState.WORKING_SHORT_SLEEP;
                    this.view.WaitForTyping();
                    break;
                case RETORT:
                    Log.d("state change", "RETORT");
                    this.view.game.CurrentState = EnumGameState.WORKING_SHORT_SLEEP;
                    this.view.Retort();
                    break;
                case GAME_OVER:
                    Log.d("state change", "GAME OVER");
                    this.shouldRun = false;
                    this.view.GameOver(false);
                    break;
                case GAME_WON:
                    Log.d("state change", "GAME WON");
                    this.shouldRun = false;
                    this.view.GameOver(true);
                    break;
                case NEXT_ROUND:
                    Log.d("state change", "NEXT ROUND");
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
