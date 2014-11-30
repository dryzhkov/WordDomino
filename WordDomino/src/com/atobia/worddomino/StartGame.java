package com.atobia.worddomino;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.atobia.worddomino.util.Configuration;
import com.atobia.worddomino.util.Game;
import com.atobia.worddomino.util.Util;
import com.atobia.worddomino.util.WordDictionary;

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

    private TextView QTV;
    private long timeStartedListening;
    private Game game;
    private Util util;

    // UtteranceProgressListener can't take params so we resort to this bs
    private String lastAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_game);

        this.util = new Util();

        // Set up the text to speech by passing it the context
        this.util.SetUpTTS(this);
        QTV = (TextView) findViewById(R.id.quick_start_askquestion);

        Configuration.LoadSettings(this);
        this.game = new Game(this);
        this.game.wd = this.util.LoadWordsFromFile(this);
    }

    protected void onStart() {
        super.onStart();
        new CountDownTimer((int)Configuration.DEFAULT_TIME_TO_WAIT, Configuration.TIME_INCREMENT) {
            public void onTick(long millisUntilFinished) {
                String strNumOfSeconds = Long.toString(millisUntilFinished / Configuration.TIME_INCREMENT);
                String toSpeak = strNumOfSeconds;
                QTV.setText("Game starts in: " + millisUntilFinished / Configuration.TIME_INCREMENT);
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
        this.util.tts.setOnUtteranceProgressListener(upl);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "messageID");

        this.util.tts.speak(strInstruction, TextToSpeech.QUEUE_FLUSH, map);
        QTV.setText(strInstruction);
    }

    private void SetUpListen() {
        Intent myIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        myIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

        try {
            startActivityForResult(myIntent, RESULT_SPEECH);

            new CountDownTimer((int)Configuration.DEFAULT_TIME_TO_WAIT, Configuration.TIME_INCREMENT) {
                public void onTick(long millisUntilFinished) {
                    String strNumOfSeconds = Long.toString(millisUntilFinished
                            / Configuration.TIME_INCREMENT);
                    String toSpeak = strNumOfSeconds;
                    QTV.setText("Game starts in: " + millisUntilFinished
                            / Configuration.TIME_INCREMENT);
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
        }, Configuration.RETORT_WAIT_TIME);
        // They gave an answer, let's make sure that it was a valid one
        String toSpeak = "";
        try {
            if (game.wd.MarkAsUsed(answer)) {
                toSpeak = answer + " is correct!";
            } else {
                // Update the amount of strikes
                int numOfStrikesLeft = game.FailedAnswer();
    
                // Ruh Roh
                if (numOfStrikesLeft < 0) {
                    // No more strikes, finish the game. You're OUT!
                    game.GameOver();
                    toSpeak = "3 strikes, game is over.";
                    this.util.SpeakAndOutPut(QTV, toSpeak);
    
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
            ProcessAnswerResponse(toSpeak);
        } catch (Exception ex) {
            // Uh-oh
            toSpeak = "Sorry, an error has occurred.";
            this.util.SpeakAndOutPut(QTV, toSpeak);
            Log.e("Exception", ex.getMessage());
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    protected void ProcessAnswerResponse(String toSpeak) {
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
                // Interesting bug, if I uncomment the setUpListen() she will
                // call it. But will not call Retort?
                // Am I missing something obvious?
                // SetUpListen()
                Log.i("here", "right before retort");
                Retort();
            }
        };
        this.util.tts.setOnUtteranceProgressListener(upl);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ProcessAnswerResponse");

        this.util.tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, map);
        QTV.setText(toSpeak);
    }

    private void Retort() {
        Toast.makeText(getApplicationContext(), "In retort", Toast.LENGTH_LONG).show();
        char c = this.lastAnswer.charAt(0);

        String retort = game.wd.FindAnswer(c);
        // Find answer can return empty
        if ("".equals(retort)) {
            String toSpeak = "Well, this is embarrassing. I'm stumped, you win.";
            this.util.SpeakAndOutPut(QTV, toSpeak);
        } else {
            String toSpeak = "My turn. Something that starts with the letter " + Character.toUpperCase(c);
            toSpeak += " How about...." + retort;
            this.util.SpeakAndOutPut(QTV, toSpeak);
        }
    }
}
