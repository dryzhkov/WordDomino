package com.atobia.worddomino;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import com.atobia.worddomino.util.Configuration;
import com.atobia.worddomino.util.EnumGameState;
import com.atobia.worddomino.util.Game;
import com.atobia.worddomino.util.StartGameLoop;
import com.atobia.worddomino.util.Util;
import com.atobia.worddomino.util.WordDictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class StartGame extends SurfaceView {
    protected static final int RESULT_SPEECH = 1;

    private StartGameLoop startGameLoop;
    private Handler handler;
    private Context context;
    private Activity activity;
    public long startTime;

    private TextView QTV;
    public Game game;
    private Util util;

    public boolean isLoadGame = false;

    public long timeStartedListening;

    // UtteranceProgressListener can't take params so we resort to this bs
    public String lastAnswer;

    public StartGame(Activity activity) {
        super(activity);
        this.activity = activity;

        this.QTV = (TextView) this.activity.findViewById(R.id.quick_start_askquestion);

        handler = new Handler();
        startGameLoop = new StartGameLoop(this);
        SurfaceHolder holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                while (true) {
                    try {
                        startGameLoop.join();
                        break;
                    } catch (InterruptedException e) {
                    }
                }
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                startGameLoop.start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
            }
        });
        setFocusable(true);

        this.util = new Util();

        // Set up the text to speech by passing it the context
        this.util.SetUpTTS(this.activity);

        Configuration.LoadSettings(this.activity);
    }

    public void run() {
        if (this.isLoadGame) {
            this.game = util.LoadGame(this.activity);
        }

        // Loadgame can fail and return null. Start a new game in that case.
        if (this.isLoadGame || this.game == null) {
            this.game = new Game(this.activity);
            this.game.wd = this.util.LoadWordsFromFile(this.activity);
            this.game.CurrentState = EnumGameState.ASK_FOR_WORD;

            new CountDownTimer((int) Configuration.DEFAULT_TIME_TO_WAIT, Configuration.TIME_INCREMENT) {
                public void onTick(long millisUntilFinished) {
                    String strNumOfSeconds = Long.toString(millisUntilFinished / Configuration.TIME_INCREMENT);
                    String toSpeak = strNumOfSeconds;
                    QTV.setText("Game starts in: " + millisUntilFinished);
                    util.tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                }

                public void onFinish() {
                    startGameLoop.run();
                }
            }.start();
        } else {
            // The load game doesn't need the countdown.
            this.startGameLoop.run();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public void ListenForWord() {
        Intent myIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        myIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

        try {
            this.activity.startActivityForResult(myIntent, RESULT_SPEECH);
        } catch (ActivityNotFoundException aex) {
            Toast t = Toast.makeText(this.activity,
                    "Your device doesn't support Speech to Text",
                    Toast.LENGTH_SHORT);
            t.show();
        } catch (Exception ex) {
            Toast t = Toast.makeText(this.activity,
                    "An expected exception occurred :/",
                    Toast.LENGTH_SHORT);
            t.show();
        }
    }

    public void ListenerResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == this.activity.RESULT_OK && null != data) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    long timeStoppedListening = System.nanoTime();
                    double totalTime = (timeStoppedListening - timeStartedListening) / 1000000;
                    this.lastAnswer = text.get(0);

                    this.QTV.setText(this.lastAnswer + " took " + Double.toString(totalTime) + " milliseconds");

                    ProcessAnswer();
                }
                break;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    public void AskForWord() {
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
                game.CurrentState = EnumGameState.LISTEN_FOR_WORD;
                //ListenForWord();
            }
        };
        this.QTV.setText(strInstruction);

        this.util.tts.setOnUtteranceProgressListener(upl);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "messageID");

        this.util.tts.speak(strInstruction, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    public void ProcessAnswer() {
        // Wait some time before she responds
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
            }
        }, Configuration.RETORT_WAIT_TIME);
        // They gave an answer, let's make sure that it was a valid one
        String toSpeak;
        try {
            if (game.wd.MarkAsUsed(this.lastAnswer)) {
                toSpeak = this.lastAnswer + " is correct!";
            } else {
                // Update the amount of strikes
                int numOfStrikesLeft = game.FailedAnswer();

                // Ruh Roh
                if (numOfStrikesLeft < 0) {
                    // No more strikes, finish the game. You're OUT!
                    game.GameOver();

                    toSpeak = "3 strikes, game is over.";
                    this.util.tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                    QTV.setText(toSpeak);

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

            UtteranceProgressListener upl = new UtteranceProgressListener() {

                @Override
                public void onStart(String utteranceId) {
                    // Need to log the time that we started listening
                    timeStartedListening = System.nanoTime();
                }

                @Override
                public void onError(String utteranceId) {
                }

                @Override
                public void onDone(String utteranceId) {
                    game.CurrentState = EnumGameState.RETORT;
                    //Retort();
                }
            };
            this.util.tts.setOnUtteranceProgressListener(upl);

            HashMap<String, String> map = new HashMap<String, String>();
            map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ProcessAnswerResponse");

            this.util.tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, map);
            this.QTV.setText(toSpeak);

        } catch (Exception ex) {
            // Uh-oh
            toSpeak = "Sorry, an error has occurred.";

            this.util.tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            this.QTV.setText(toSpeak);
            Log.e("Exception", ex.getMessage());
        }
    }

    public void Retort() {
        char c = this.lastAnswer.charAt(0);

        String retort = game.wd.FindAnswer(c);
        // Find answer can return empty
        if ("".equals(retort)) {
            String toSpeak = "Well, this is embarrassing. I'm stumped, you win.";
            this.util.tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            this.QTV.setText(toSpeak);

            this.game.GameOver();
        } else {
            String toSpeak = "My turn. Something that starts with the letter " + Character.toUpperCase(c);
            toSpeak += " How about...." + retort;
            this.util.tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            this.QTV.setText(toSpeak);

            this.game.CurrentState = EnumGameState.ASK_FOR_WORD;
        }
    }
}
