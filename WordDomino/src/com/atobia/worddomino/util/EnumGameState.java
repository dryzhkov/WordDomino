package com.atobia.worddomino.util;

public enum EnumGameState {
    /*
        Standard lifecycle of the game
            NEW_GAME
            ASK_FOR_WORD -- Ask the user for a word starting with a letter
            LISTEN_FOR_WORD -- Start the listener to get the word from the user
            WAIT_FOR_TYPING -- The alternative to LISTEN_FOR_WORD when you don't play over bluetooth
            PROCESS_ANSWER -- Checks if the user's answer is correct
            RETORT -- The AI's answer to the user's word
            NEXT_ROUND -- Goes back to ASK_FOR_WORD
            GAME_OVER

        Between each state we go into a WORKING_SHORT_SLEEP state while waiting for another state
    */

    NEW_GAME,
    ASK_FOR_WORD,
    LISTEN_FOR_WORD,
    WAIT_FOR_TYPING,
    PROCESS_ANSWER,
    RETORT,
    GAME_OVER,
    NEXT_ROUND,
    WORKING_SHORT_SLEEP
}
