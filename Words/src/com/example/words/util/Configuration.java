package com.example.words.util;

public class Configuration {
	public static int GameDifficulty;
	
	public class DifficultyLevel{
		public static final int EASY = 1;
		public static final int MEDIUM = 3;
		public static final int HARD = 7;
	}
	
	public static int[] LetterRanking = new int[]
			{Configuration.DifficultyLevel.EASY, /* a */
			 Configuration.DifficultyLevel.EASY, /* b */
			 Configuration.DifficultyLevel.EASY, /* c */
			 Configuration.DifficultyLevel.MEDIUM, /* d */
			 Configuration.DifficultyLevel.MEDIUM, /* e */
			 Configuration.DifficultyLevel.MEDIUM, /* f */
			 Configuration.DifficultyLevel.EASY, /* g */
			 Configuration.DifficultyLevel.EASY, /* h */
			 Configuration.DifficultyLevel.HARD, /* i */
			 Configuration.DifficultyLevel.HARD, /* j */
			 Configuration.DifficultyLevel.HARD, /* k */
			 Configuration.DifficultyLevel.EASY, /* l */
			 Configuration.DifficultyLevel.EASY, /* m */
			 Configuration.DifficultyLevel.MEDIUM, /* n */
			 Configuration.DifficultyLevel.HARD, /* o */
			 Configuration.DifficultyLevel.EASY, /* p */
			 Configuration.DifficultyLevel.HARD, /* q */
			 Configuration.DifficultyLevel.EASY, /* r */
			 Configuration.DifficultyLevel.EASY, /* s */
			 Configuration.DifficultyLevel.MEDIUM, /* t */
			 Configuration.DifficultyLevel.HARD, /* u */
			 Configuration.DifficultyLevel.HARD, /* v */
			 Configuration.DifficultyLevel.EASY, /* w */
			 Configuration.DifficultyLevel.HARD, /* x */
			 Configuration.DifficultyLevel.HARD, /* y */
			 Configuration.DifficultyLevel.HARD, /* z */};
}
