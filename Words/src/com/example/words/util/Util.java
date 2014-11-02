package com.example.words.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

import android.content.Context;
import android.net.sip.SipSession.Listener;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.widget.Toast;

import com.example.words.R;

public class Util {
	Context context;
	WordDictionary myDictionary;
	public TextToSpeech tts;
	public Util() {}
	
	public WordDictionary LoadWordsFromFile(){
    	myDictionary = new WordDictionary();
    	InputStream ins = this.context.getResources().openRawResource(R.raw.cities);
        BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
        
        String line;
		try {
			line = reader.readLine();
			while(line != null){
				if(!line.isEmpty()){
					myDictionary.Add(line);
				}
	        	line = reader.readLine();
	        }
		}catch (IOException ioe){
			ioe.printStackTrace();
		}catch (Exception e){
			e.printStackTrace();
		}
		return myDictionary;
    }
	
	public void SetUpTTS(Context c){
		this.context = c;
		tts = new TextToSpeech(c.getApplicationContext(), new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int status) {
				if(status != TextToSpeech.ERROR){
					tts.setLanguage(Locale.US);
				}else{
					Toast t = Toast.makeText(Util.this.context.getApplicationContext(), 
							"Text To Speech is not supported", 
							Toast.LENGTH_SHORT);
					t.show();
				}
			}
		});
	}
	
	public void SpeakText(String str) {
		tts.speak(str, TextToSpeech.QUEUE_FLUSH, null);
	}
}
