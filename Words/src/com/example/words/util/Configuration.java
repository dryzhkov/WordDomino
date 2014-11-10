package com.example.words.util;

public class Configuration {
	public static int GameDifficulty;
	
	public class DifficultyLevel{
		public static final int EASY = 1;
		public static final int MEDIUM = 3;
		public static final int HARD = 7;
	}
	
	public static int[] LetterRanking = new int[]
			{DifficultyLevel.EASY, /* a */
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
}
