package com.example.words;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.example.words.util.Util;
import com.example.words.util.WordDictionary;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ActivityNotFoundException;
import android.content.Intent;

public class QuickStart extends Activity {
	protected static final int RESULT_SPEECH = 1;
	protected static final int START_TIME_TO_WAIT = 5000;
	protected static final int TIME_INCREMENT = 1000;
	
	protected static final int RETORT_WAIT_TIME = 2000;
	
	private WordDictionary wd;
	private TextView QTV;
	private Util u;
	private long timeStartedListening;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_game);
        u = new Util();
        
        // Set up the text to speech by passing it the context
        u.SetUpTTS(this);
        wd = u.LoadWordsFromFile(this);
        QTV = (TextView)findViewById(R.id.quick_start_askquestion);
    }
	
	protected void onStart() {
		super.onStart();
		new CountDownTimer(START_TIME_TO_WAIT, TIME_INCREMENT) {
			 public void onTick(long millisUntilFinished) {
				 String strNumOfSeconds = Long.toString(millisUntilFinished / TIME_INCREMENT);
				 String toSpeak = strNumOfSeconds;
				 QTV.setText("Game starts in: " + millisUntilFinished / TIME_INCREMENT);
				 u.tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
			 }
			
			 public void onFinish() {
			     StartCityLetterGame();
			 }
		}.start();
	}
	
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
	protected void StartCityLetterGame() {
	   Random r = new Random();
	   char c = (char) (r.nextInt(26) + 'a');
	   String strInstruction = "Name a city that starts with the letter: " + Character.toUpperCase(c);
	   UtteranceProgressListener upl = new UtteranceProgressListener() {
			
			@Override
			public void onStart(String utteranceId) {
				// Need to log the time that we started listening
			    timeStartedListening = System.nanoTime();
			}
			
			@Override
			public void onError(String utteranceId) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onDone(String utteranceId) {
				SetUpListen();
			}
		};
		u.tts.setOnUtteranceProgressListener(upl);
		
		HashMap<String, String> map = new HashMap<String, String>();
	    map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"messageID");
	    
		u.tts.speak(strInstruction, TextToSpeech.QUEUE_FLUSH, map); 
	    QTV.setText(strInstruction);
	}
	
	private void SetUpListen() {
		Intent myIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		myIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");		 
		 
		try{
			startActivityForResult(myIntent, RESULT_SPEECH);
			
			new CountDownTimer(START_TIME_TO_WAIT, TIME_INCREMENT) {
				 public void onTick(long millisUntilFinished) {
					 String strNumOfSeconds = Long.toString(millisUntilFinished / TIME_INCREMENT);
					 String toSpeak = strNumOfSeconds;
					 QTV.setText("Game starts in: " + millisUntilFinished / TIME_INCREMENT);
					 u.tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
				 }
				
				 public void onFinish() {
				     StartCityLetterGame();
				 }
			}.start();
			
			
		}catch (ActivityNotFoundException aex){
			Toast t = Toast.makeText(getApplicationContext(), 
										"Your device doesn't support Speech to Text", 
										Toast.LENGTH_SHORT);
			t.show();
		}catch(Exception ex){
			Toast t = Toast.makeText(getApplicationContext(), 
					"An expected exception occurred :/", 
					Toast.LENGTH_SHORT);
			t.show();
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
 
        switch (requestCode) {
	        case RESULT_SPEECH: {
	            if (resultCode == RESULT_OK && null != data) {
	            	ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
	            	long timeStoppedListening = System.nanoTime();
	            	double totalTime = (timeStoppedListening - timeStartedListening) / 1000000;
	            	QTV.setText(text.get(0) + " took " + Double.toString(totalTime) + " milliseconds");
	            	
	            	retort("");
	            }
	            break;
	        }
        }
    }
	
	protected void retort(String answer) {
		// Wait some time before she responds
		Handler handler = new Handler(); 
	    handler.postDelayed(new Runnable() { 
	         public void run() { 
	         } 
	    }, RETORT_WAIT_TIME);
	    
	    if (answer == "") {
	    	// Incorrect answer will pass an empty value
	    	String toSpeak = "Wow, you suck";
	    	QTV.setText(toSpeak);
	    	u.tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
	    } else {
	    	// Correct answer
	    	String toSpeak = "Huzzah!";
	    	QTV.setText(toSpeak);
	    	u.tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
	    }
	    
	}
}
