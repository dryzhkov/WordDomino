package com.example.words.util;

import android.content.Context;
import android.content.SharedPreferences;

public class Configuration {
    public static final String PREFERENCES_KEY = "WORDS_APP_PREF_FILE";
    public static final String GAME_DIFF_KEY = "GAME_DIFFICULTY";

    public static int GameDifficulty;
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
    public static void LoadGameDifficulty(Context context){
        SharedPreferences settings = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
        Configuration.GameDifficulty = settings.getInt(GAME_DIFF_KEY, Configuration.DifficultyLevel.EASY);
    }
    public static void SaveGameDifficulty(Context context, int diffLevel){
        SharedPreferences settings = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(GAME_DIFF_KEY, diffLevel);
        editor.commit();
    }
}
