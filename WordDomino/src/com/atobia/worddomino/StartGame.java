package com.atobia.worddomino;

import android.annotation.TargetApi;
import android.app.Activity;
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
    private StartGameLoop startGameLoop;
    private Handler handler;
    private Context myContext;
    private Activity myActivity;
    public long startTime;
    private TextView QTV;
    public Game game;
    private Util util;
    public boolean isLoadGame = false;
    public long timeStartedListening;
    public String lastAnswer;
    public char startCharacter = ' ';

    public StartGame(Activity activity) {
        super(activity);
        this.myActivity = activity;
        this.myContext = activity.getApplicationContext();
        this.QTV = (TextView) this.myActivity.findViewById(R.id.quick_start_askquestion);
        setFocusable(true);
        this.util = new Util();
        this.util.SetUpTTS(this.myContext);
        Configuration.LoadSettings(this.myContext);

        if (this.isLoadGame) {
            this.game = util.LoadGame(this.myContext);
        }

        if(!this.isLoadGame || this.game == null){
            this.game = new Game(this.myContext);
            this.game.wd = this.util.LoadWordsFromFile(this.myActivity);
            this.game.CurrentState = EnumGameState.NEW_GAME;
        }

        handler = new Handler();
        startGameLoop = new StartGameLoop(this);
        SurfaceHolder holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                startGameLoop.shouldRun = false;
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
                startGameLoop.shouldRun = true;
                startGameLoop.start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public void ListenForWord() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Intent myIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                myIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
                myIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
                myIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hint Message Goes Here");
                myIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);

                try {
                    myActivity.startActivityForResult(myIntent, Configuration.VOICE_RECOGNITION_REQUEST_CODE);
                } catch (Exception ex) {

                    Log.e("ListenForWord", ex.toString());
                }
            }
        });
    }

    public void ListenerResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Configuration.VOICE_RECOGNITION_REQUEST_CODE){
            if (resultCode == this.myActivity.RESULT_OK && null != data) {
                ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                long timeStoppedListening = System.nanoTime();
                double totalTime = (timeStoppedListening - timeStartedListening) / 1000000;
                this.lastAnswer = text.get(0).toLowerCase();
                this.QTV.setText(this.lastAnswer + " took " + Double.toString(totalTime) + " milliseconds");
                ProcessAnswer();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    public void AskForWord() {
        handler.post(new Runnable() {
            @Override
            public void run() {
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
                    }
                };

                if(startCharacter == ' ') {
                    Random r = new Random();
                    startCharacter = (char) (r.nextInt(WordDictionary.arrayUpperBound) + 'a');
                }
                String strInstruction = "Name a city that starts with the letter: " + Character.toUpperCase(startCharacter);
                QTV.setText(strInstruction);
                util.tts.setOnUtteranceProgressListener(upl);
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "messageID");

                util.tts.speak(strInstruction, TextToSpeech.QUEUE_FLUSH, map);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    public void ProcessAnswer() {
        // They gave an answer, let's make sure that it was a valid one
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String toSpeak;
                try {
                    //TODO: We need to check the first letter, before accepting the answer

                    if (game.wd.MarkAsUsed(lastAnswer)) {
                        toSpeak = lastAnswer + " is correct!";
                    } else {
                        // Update the amount of strikes
                        int numOfStrikesLeft = game.FailedAnswer();

                        // Ruh Roh
                        if (numOfStrikesLeft < 0) {
                            // No more strikes, finish the game. You're OUT!
                            game.GameOver();

                            toSpeak = "3 strikes, game is over.";
                            util.tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
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
                        }
                    };
                    util.tts.setOnUtteranceProgressListener(upl);

                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ProcessAnswerResponse");

                    util.tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, map);
                    QTV.setText(toSpeak);

                } catch (Exception ex) {
                    // Uh-oh
                    toSpeak = "Sorry, an error has occurred.";

                    util.tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                    QTV.setText(toSpeak);
                    Log.e("Exception", ex.getMessage());
                }
            }
        }, Configuration.RETORT_WAIT_TIME);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    public void Retort() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                //grab last character
                char c = lastAnswer.charAt(lastAnswer.length() - 1);

                String retort = game.wd.FindAnswer(c);
                // Find answer can return empty
                if ("".equals(retort)) {
                    String toSpeak = "Well, this is embarrassing. I'm stumped, you win.";
                    util.tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                    QTV.setText(toSpeak);
                    game.GameOver();
                } else {
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
                            game.CurrentState = EnumGameState.NEXT_ROUND;
                        }
                    };
                    util.tts.setOnUtteranceProgressListener(upl);
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ProcessAnswerResponse");

                    startCharacter = retort.charAt(retort.length() - 1);
                    String toSpeak = "My turn. Something that starts with the letter " + Character.toUpperCase(c) + ". How about " + retort;
                    util.tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, map);
                    QTV.setText(toSpeak);
                }
            }
        });
    }

    public void CountDown(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new CountDownTimer((int) Configuration.DEFAULT_TIME_TO_WAIT, Configuration.TIME_INCREMENT) {
                    public void onTick(long millisUntilFinished) {
                        String strNumOfSeconds = Long.toString(millisUntilFinished / Configuration.TIME_INCREMENT);
                        String toSpeak = strNumOfSeconds;
                        QTV.setText("Game starts in: " + strNumOfSeconds);
                        util.tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                    }

                    public void onFinish() {
                        game.CurrentState = EnumGameState.ASK_FOR_WORD;
                    }
                }.start();
            }
        }, Configuration.RETORT_WAIT_TIME);
    }

    public void NextRound(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String toSpeak = "Your turn.";
                util.tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                QTV.setText(toSpeak);
                game.CurrentState = EnumGameState.ASK_FOR_WORD;
            }
        }, Configuration.RETORT_WAIT_TIME);
    }
}
