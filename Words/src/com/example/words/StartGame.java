package com.example.words;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.example.words.util.Configuration;
import com.example.words.util.Game;
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
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ActivityNotFoundException;
import android.content.Intent;

public class StartGame extends Activity {
    protected static final int RESULT_SPEECH = 1;
    protected static final int START_TIME_TO_WAIT = 5000;
    protected static final int TIME_INCREMENT = 1000;

    protected static final int RETORT_WAIT_TIME = 2000;

    private WordDictionary wd;
    private TextView QTV;
    private Util util;
    private long timeStartedListening;
    private Game game;

    // UtteranceProgressListener can't take params so we resort to this bs
    private String lastAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_game);
        util = new Util();

        // Set up the text to speech by passing it the context
        util.SetUpTTS(this);
        wd = util.LoadWordsFromFile(this);
        QTV = (TextView) findViewById(R.id.quick_start_askquestion);

        Configuration.LoadSettings(this);
        game = new Game(this);
    }

    protected void onStart() {
        super.onStart();
        new CountDownTimer(START_TIME_TO_WAIT, TIME_INCREMENT) {
            public void onTick(long millisUntilFinished) {
                String strNumOfSeconds = Long.toString(millisUntilFinished / TIME_INCREMENT);
                String toSpeak = strNumOfSeconds;
                QTV.setText("Game starts in: " + millisUntilFinished / TIME_INCREMENT);
                util.tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            }

            public void onFinish() {
                StartCityLetterGame();
            }
        }.start();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    protected void StartCityLetterGame() {
        Random r = new Random();
        char c = (char) (r.nextInt(WordDictionary.arrayUpperBound) + 'a');
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
                QTV.setText("Error");
            }

            @Override
            public void onDone(String utteranceId) {
                SetUpListen();
            }
        };
        util.tts.setOnUtteranceProgressListener(upl);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "messageID");

        util.tts.speak(strInstruction, TextToSpeech.QUEUE_FLUSH, map);
        QTV.setText(strInstruction);
    }

    private void SetUpListen() {
        Intent myIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        myIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

        try {
            startActivityForResult(myIntent, RESULT_SPEECH);

            new CountDownTimer(START_TIME_TO_WAIT, TIME_INCREMENT) {
                public void onTick(long millisUntilFinished) {
                    String strNumOfSeconds = Long.toString(millisUntilFinished
                            / TIME_INCREMENT);
                    String toSpeak = strNumOfSeconds;
                    QTV.setText("Game starts in: " + millisUntilFinished
                            / TIME_INCREMENT);
                    util.tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                }

                public void onFinish() {
                    StartCityLetterGame();
                }
            }.start();

        } catch (ActivityNotFoundException aex) {
            Toast t = Toast.makeText(getApplicationContext(),
                    "Your device doesn't support Speech to Text",
                    Toast.LENGTH_SHORT);
            t.show();
        } catch (Exception ex) {
            Toast t = Toast.makeText(getApplicationContext(),
                    "An expected exception occurred :/", Toast.LENGTH_SHORT);
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
                    QTV.setText(text.get(0) + " took " + Double.toString(totalTime)
                            + " milliseconds");
    
                    ProcessAnswer(text.get(0));
                }
                break;
            }
        }
    }

    protected void ProcessAnswer(String answer) {
        this.lastAnswer = answer;
        // Wait some time before she responds
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
            }
        }, RETORT_WAIT_TIME);
        // They gave an answer, let's make sure that it was a valid one
        String toSpeak = "";
        try {
            if (wd.MarkAsUsed(answer)) {
                toSpeak = answer + " is correct!";
            } else {
                // Update the amount of strikes
                int numOfStrikesLeft = game.FailedAnswer();
    
                // Ruh Roh
                if (numOfStrikesLeft < 0) {
                    // No more strikes, finish the game. You're OUT!
                    game.GameOver();
    
                    toSpeak = "3 strikes, game is over.";
                    QTV.setText(toSpeak);
                    util.tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
    
                    // Todo Go back to main menu
                    return;
                } else {
                    // We still have a few more strikes
                    if (numOfStrikesLeft == 0) {
                        toSpeak = "Incorrect. This is your last strike.";
                    } else {
                        toSpeak = "Incorrect, " + Integer.toString(numOfStrikesLeft + 1) + " strikes left.";
                    }
                }
            }
        } catch (Exception ex) {
            // Uh-oh
            QTV.setText("Sorry, an error has occurred.");
            Log.e("Exception", ex.getMessage());
        }
        ProcessAnswerResponse(toSpeak);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    protected void ProcessAnswerResponse(String toSpeak) {
        Log.i("here", toSpeak);
        UtteranceProgressListener upl = new UtteranceProgressListener() {

            @Override
            public void onStart(String utteranceId) {
                // Need to log the time that we started listening
                timeStartedListening = System.nanoTime();
            }

            @Override
            public void onError(String utteranceId) {
                // TODO Auto-generated method stub
                QTV.setText("Error");
            }

            @Override
            public void onDone(String utteranceId) {
                // Interesting bug, if I uncomment the setUpListen() she will
                // call it. But will not call Retort?
                // Am I missing something obvious?
                // SetUpListen()
                Log.i("here", "right before retort");
                Retort();
            }
        };
        util.tts.setOnUtteranceProgressListener(upl);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ProcessAnswerResponse");

        util.tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, map);
        QTV.setText(toSpeak);
    }

    private void Retort() {
        Toast.makeText(getApplicationContext(), "In retort", Toast.LENGTH_LONG).show();
        char c = this.lastAnswer.charAt(0);

        String retort = wd.FindAnswer(c);
        // Find answer can return empty
        if ("".equals(retort)) {
            String toSpeak = "Well, this is embarrassing. I'm stumped, you win.";
            QTV.setText(toSpeak);
            util.tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
        } else {
            String toSpeak = "My turn. Something that starts with the letter " + Character.toUpperCase(c);
            toSpeak += " How about...." + retort;
            QTV.setText(toSpeak);
            util.tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
}
