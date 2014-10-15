package com.example.words;

import java.util.Random;

import com.example.words.util.Util;
import com.example.words.util.WordDictionary;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;
public class QuickStart extends Activity {
	WordDictionary wd;
	TextView QTV;
	Util u;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_game);
        u = new Util(this);
        wd = u.LoadWordsFromFile();
        QTV = (TextView)findViewById(R.id.quick_start_askquestion);
    }
	
	protected void onStart() {
		super.onStart();
		new CountDownTimer(5000, 1000) {
			 Boolean saidInstruction = false;
		     public void onTick(long millisUntilFinished) {
	    	 	 // TODO: bug here that it doesn't say "Game starts in" 
		    	 String toSpeak = "";
		    	 if (!saidInstruction) {
		    		 saidInstruction = true;
		    	 	 toSpeak = "Game starts in ";
		    	 }
		    	 String strNumOfSeconds = Long.toString(millisUntilFinished / 1000);
		    	 toSpeak += strNumOfSeconds;
		    	 QTV.setText("Game starts in: " + millisUntilFinished / 1000);
		    	 u.SpeakText(toSpeak);
		     }

		     public void onFinish() {
		         QTV.setText("GO!");
		         StartCityLetterGame();
		     }
		  }.start();
	}
	
	protected void StartCityLetterGame() {
	   Random r = new Random();
	   char c = (char) (r.nextInt(26) + 'a');
	   String strInstruction = "Name a city that starts with the letter: " + Character.toUpperCase(c);
	   u.SpeakText(strInstruction);
	   QTV.setText(strInstruction);
	}
}
