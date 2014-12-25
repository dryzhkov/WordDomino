package com.atobia.worddomino.util;

public enum EnumGameState {
    /*
        Standard lifecycle of the game
        1. AskForWord() -- Ask the user for a word starting with a letter
        2. ListenForWord() -- Start the listener to get the word from the user
        3. onActivityResult() -- This is the result from the listener. Gets called automatically from ListenForWord()
        4. ProcessAnswer() -- Checks if the user's answer is correct
        5. Retort() -- The AI's answer to the user's word
        6. GameOver

        Working States
        There is a WORKING_LONG_SLEEP state that is in the middle of each of the other states
        Once that is finished, we go into a WORKING_SHORT_SLEEP mode
    */
    ASK_FOR_WORD,
    LISTEN_FOR_WORD,
    PROCESS_ANSWER,
    RETORT,
    GAME_OVER,
    WORKING_LONG_SLEEP,
    WORKING_SHORT_SLEEP

}
