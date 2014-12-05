package com.atobia.worddomino.util;

public enum EnumGameState {
    /*
        Standard lifecycle of the game
        1. AskForWord() -- Ask the user for a word starting with a letter
        2. ListenForWord() -- Start the listener to get the word from the user
        3. onActivityResult() -- This is the result from the listener. Gets called automatically from ListenForWord()
        4. ProcessAnswer() -- Checks if the user's answer is correct
        5. Retort() -- The AI's answer to the user's word
    */
    ASKFORWORD,
    LISTENFORWORD,
    PROCESSANSWER,
    RETORT
}
