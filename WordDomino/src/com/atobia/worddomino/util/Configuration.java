package com.atobia.worddomino.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;

import com.google.android.gms.common.api.GoogleApiClient;

public class Configuration {
    // Constants
    private static final String PREFERENCES_KEY = "WORDS_APP_PREF_FILE";
    private static final String GAME_DIFF_KEY = "GAME_DIFFICULTY";
    private static final String FIRST_PLAYER_KEY = "FIRST_PLAYER";
    private static final String SHOW_SAFETY_SCREEN_KEY = "SHOW_SAFETY_SCREEN";
    private static final String SAVED_GAME_EXISTS_KEY = "SAVED_GAME_EXISTS";
    public static final double DEFAULT_TIME_TO_WAIT = 5000;
    public static final int DEFAULT_NUM_OF_STRIKES = 3;
    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
    public static final int GAME_MIN_VOLUME = 3; //min 0, max 15
    public static final int GAME_AUDIO_STREAM = TextToSpeech.Engine.DEFAULT_STREAM;

    public static int GameDifficulty = 0;
    public static int FirstPlayer = 0;
    public static boolean ShowSafetyScreen = true;
    public static boolean SavedGameExists = false;
    public static Game LoadedGame = null;
    public static GoogleApiClient GMSClient = null;

    public static int[] LetterRanking = new int[]
    {        DifficultyLevel.EASY, /* a */
             DifficultyLevel.EASY, /* b */
             DifficultyLevel.EASY, /* c */
             DifficultyLevel.MEDIUM, /* d */
             DifficultyLevel.MEDIUM, /* e */
             DifficultyLevel.MEDIUM, /* f */
             DifficultyLevel.EASY, /* g */
             DifficultyLevel.EASY, /* h */
             DifficultyLevel.HARD, /* i */
             DifficultyLevel.HARD, /* j */
             DifficultyLevel.HARD, /* k */
             DifficultyLevel.EASY, /* l */
             DifficultyLevel.EASY, /* m */
             DifficultyLevel.MEDIUM, /* n */
             DifficultyLevel.HARD, /* o */
             DifficultyLevel.EASY, /* p */
             DifficultyLevel.HARD, /* q */
             DifficultyLevel.EASY, /* r */
             DifficultyLevel.EASY, /* s */
             DifficultyLevel.MEDIUM, /* t */
             DifficultyLevel.HARD, /* u */
             DifficultyLevel.HARD, /* v */
             DifficultyLevel.EASY, /* w */
             DifficultyLevel.HARD, /* x */
             DifficultyLevel.HARD, /* y */
             DifficultyLevel.HARD, /* z */};

    public class DifficultyLevel{
        public static final int EASY = 1;
        public static final int MEDIUM = 3;
        public static final int HARD = 7;
    }

    public class PlayerType{
        public static final int AI = 0;
        public static final int Player = 1;
    }

    public static void LoadSettings(Context context){
        SharedPreferences settings = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
        GameDifficulty = settings.getInt(GAME_DIFF_KEY, DifficultyLevel.EASY);
        FirstPlayer = settings.getInt(FIRST_PLAYER_KEY, PlayerType.AI);
        ShowSafetyScreen = settings.getBoolean(SHOW_SAFETY_SCREEN_KEY, true); //show safety screen by default
        SavedGameExists = settings.getBoolean(SAVED_GAME_EXISTS_KEY, false);
    }

    public static void SaveSettings(Context context){
        SharedPreferences settings = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(GAME_DIFF_KEY, GameDifficulty);
        editor.putInt(FIRST_PLAYER_KEY, FirstPlayer);
        editor.putBoolean(SHOW_SAFETY_SCREEN_KEY, ShowSafetyScreen);
        editor.putBoolean(SAVED_GAME_EXISTS_KEY, SavedGameExists);
        editor.commit();
    }

    public static boolean IsPlayerSignedIn(){
        return GMSClient != null && GMSClient.isConnected();
    }
}
