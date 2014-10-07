package com.example.speechandtextdemo;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends ActionBarActivity {
	protected static final int RESULT_SPEECH = 1;
	
	private ImageButton btnListen;
	private ImageButton btnSpeak;
    private TextView txtText;
    private EditText txtInput;
    private TextToSpeech tts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        txtText = (TextView)findViewById(R.id.txtText);
        txtInput = (EditText)findViewById(R.id.txtInput);
        btnListen = (ImageButton)findViewById(R.id.btnListen);
        btnSpeak = (ImageButton)findViewById(R.id.btnSpeak);
        
        btnListen.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
				myIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
				
				try{
					startActivityForResult(myIntent, RESULT_SPEECH);
					txtText.setText("");
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
		});
        
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
			
			@Override
			public void onInit(int status) {
				if(status != TextToSpeech.ERROR){
					tts.setLanguage(Locale.US);
				}else{
					Toast t = Toast.makeText(getApplicationContext(), 
							"Text To Speech is not supported", 
							Toast.LENGTH_SHORT);
					t.show();
				}
			}
		});
        
        btnSpeak.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String toSpeak = txtInput.getText().toString();
		        Toast.makeText(getApplicationContext(), 
		        				toSpeak,
		        				Toast.LENGTH_SHORT).show();
		        tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
			}
		} );
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
 
        switch (requestCode) {
	        case RESULT_SPEECH: {
	            if (resultCode == RESULT_OK && null != data) {
	            	ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
	                txtText.setText(text.get(0));
	            }
	            break;
	        }
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
