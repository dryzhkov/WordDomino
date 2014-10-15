package com.example.words.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;

import com.example.words.R;

public class Util {
	Context context;
	public Util(Context c) {
		this.context = c;
	}
	
	public WordDictionary LoadWordsFromFile(){
    	WordDictionary myDictionary = new WordDictionary();
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
}
