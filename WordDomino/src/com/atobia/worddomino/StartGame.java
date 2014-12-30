package com.atobia.worddomino;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.RecognizerIntent;
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
import java.util.Random;

public class StartGame extends SurfaceView {
    private StartGameLoop startGameLoop;
    private Handler handler;
    private Context myContext;
    private Activity myActivity;
    public long startTime;
    private TextView QTV;
    public Game game;
    public Util util;
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
        this.util = new Util(this.myContext);

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

    public void AskForWord() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(startCharacter == ' ') {
                    Random r = new Random();
                    startCharacter = (char) (r.nextInt(WordDictionary.arrayUpperBound) + 'a');
                }
                String message = "Name a city that starts with the letter: " + Character.toUpperCase(startCharacter);
                QTV.setText(message);
                util.speak(message, new Runnable() {
                    @Override
                    public void run() {
                        game.CurrentState = EnumGameState.LISTEN_FOR_WORD;
                    }
                });
            }
        });
    }

    public void ProcessAnswer() {
        // They gave an answer, let's make sure that it was a valid one
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String toSpeak;
                try {
                    //TODO: We need to check the first letter, before accepting the answer
                    boolean isOver = false;

                    if (game.wd.MarkAsUsed(lastAnswer)) {
                        toSpeak = lastAnswer + " is correct!";
                    } else {
                        // Update the amount of strikes
                        int numOfStrikesLeft = game.FailedAnswer();

                        // Ruh Roh
                        if (numOfStrikesLeft < 0) {
                            // No more strikes, finish the game. You're OUT!
                            toSpeak = "3 strikes, game is over.";
                            isOver = true;
                        } else {
                            // We still have a few more strikes
                            if (numOfStrikesLeft == 0) {
                                toSpeak = "Incorrect. This is your last strike.";
                            } else {
                                toSpeak = "Incorrect, " + Integer.toString(numOfStrikesLeft + 1) + " strikes left.";
                            }
                        }
                    }

                    QTV.setText(toSpeak);

                    if(!isOver) {
                        util.speak(toSpeak, new Runnable() {
                            @Override
                            public void run() {
                                game.CurrentState = EnumGameState.RETORT;
                            }
                        });
                    }else{
                        util.speak(toSpeak, new Runnable() {
                            @Override
                            public void run() {
                                game.CurrentState = EnumGameState.GAME_OVER;
                            }
                        });
                    }
                } catch (Exception ex) {
                    Log.e("Exception", ex.getMessage());
                }
            }
        }, Configuration.RETORT_WAIT_TIME);
    }

    public void Retort() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                //grab last character
                startCharacter = lastAnswer.charAt(lastAnswer.length() - 1);

                String retort = game.wd.FindAnswer(startCharacter);
                String toSpeak;
                // Find answer can return empty
                if ("".equals(retort)) {
                    toSpeak = "Well, this is embarrassing. I'm stumped, you win.";
                    util.speak(toSpeak, new Runnable() {
                        @Override
                        public void run() {
                            game.CurrentState = EnumGameState.GAME_OVER;
                        }
                    });
                } else {
                    toSpeak = "My turn. Something that starts with the letter " + Character.toUpperCase(startCharacter) + ". How about " + retort;
                    util.speak(toSpeak, new Runnable() {
                        @Override
                        public void run() {
                            game.CurrentState = EnumGameState.NEXT_ROUND;
                        }
                    });
                    startCharacter = retort.charAt(retort.length() - 1);
                }
                QTV.setText(toSpeak);
            }
        });
    }

    public void CountDown(){
        String toShow = "Game is about to start! Get Ready!";
        QTV.setText(toShow);

        String toSpeak = "Game starts in 3. 2. 1. Go: ";
        util.speak(toSpeak, new Runnable() {
            @Override
            public void run() {
                game.CurrentState = EnumGameState.ASK_FOR_WORD;
            }
        });
    }

    public void NextRound(){
        this.util.SaveGame(this.myContext, this.game);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String toSpeak = "Your turn.";
                QTV.setText(toSpeak);
                util.speak(toSpeak, new Runnable() {
                    @Override
                    public void run() {
                        game.CurrentState = EnumGameState.ASK_FOR_WORD;
                    }
                });
            }
        }, Configuration.RETORT_WAIT_TIME);
    }
}
