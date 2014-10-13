package com.example.words;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.example.words.util.WordDictionary;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainMenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
    }
    
    public void QuickStart_Clicked(View view)
    {
    	Toast.makeText(getApplicationContext(),
    		      "Quick Start Clicked!", 
    		      Toast.LENGTH_LONG).show();
    	
    }
    
    public void NewGame_Clicked(View view)
    {
    	Toast.makeText(getApplicationContext(),
    		      "New Game Clicked!", 
    		      Toast.LENGTH_LONG).show();

    }
    
    public void LoadGame_Clicked(View view)
    {
    	Toast.makeText(getApplicationContext(),
    		      "Load Game Clicked!", 
    		      Toast.LENGTH_LONG).show();

    }
    
    public void Settings_Clicked(View view)
    {
    	Toast.makeText(getApplicationContext(),
    		      "Settings Clicked!", 
    		      Toast.LENGTH_LONG).show();

    }
    
    public void HighScores_Clicked(View view)
    {
    	Toast.makeText(getApplicationContext(),
    		      "High Scores Clicked!", 
    		      Toast.LENGTH_LONG).show();

    }
    
    public void LoadWordsFromFile(){
    	WordDictionary myDictionary = new WordDictionary();
    	InputStream ins = getResources().openRawResource(R.raw.cities);
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
    }
}
