package com.example.words;

import com.example.words.util.Util;
import com.example.words.util.WordDictionary;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
public class QuickStart extends Activity {
	WordDictionary wd;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_game);
        Util u = new Util(this);
        wd = u.LoadWordsFromFile();
    }
	
	protected void onStart() {
		super.onStart();
		TextView t = (TextView)findViewById(R.id.quick_start_askquestion);
		t.setText("blah");
	}
}
