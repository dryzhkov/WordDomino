package com.atobia.worddomino;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.atobia.worddomino.util.AchievementManager;
import com.atobia.worddomino.util.Configuration;
import com.atobia.worddomino.util.EnumGameState;
import com.atobia.worddomino.util.Game;
import com.atobia.worddomino.util.GameOverDialog;
import com.atobia.worddomino.util.StartGameLoop;
import com.atobia.worddomino.util.Util;
import com.atobia.worddomino.util.WordDictionary;
import java.util.ArrayList;
import java.util.Random;

public class StartGame extends SurfaceView {
    public StartGameLoop startGameLoop;
    public Context myContext;
    public Game game;
    public long timeStartedListening = 0;
    public String lastAnswer = "";
    public char startCharacter = ' ';

    private Handler handler;
    private Activity myActivity;
    private TextView TVQuestion;
    private TextView TVPoints;
    private TextView TVMultiplier;
    private TextView TVStrikes;
    private EditText ETAnswer;

    public StartGame(Activity activity) {
        super(activity);
        this.myActivity = activity;
        this.myContext = activity.getApplicationContext();
        this.TVQuestion = (TextView) this.myActivity.findViewById(R.id.quick_start_askquestion);
        this.TVPoints = (TextView) this.myActivity.findViewById(R.id.start_game_points);
        this.TVMultiplier = (TextView) this.myActivity.findViewById(R.id.start_game_multiplier);
        this.TVStrikes = (TextView) this.myActivity.findViewById(R.id.start_game_strikes);
        this.ETAnswer = (EditText) this.myActivity.findViewById(R.id.play_over_text);
        this.game = Configuration.LoadedGame;

        if(this.game == null){
            //new game
            this.game = new Game();
            this.game.wd = Util.LoadWordsFromFile(this.myActivity);
            this.game.CurrentState = EnumGameState.NEW_GAME;
        }
        setFocusable(true);
        // Show the score
        displayScoreBoard();

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
                    } catch (InterruptedException e) {}
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

    public void AskForWord() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(startCharacter == ' ') {
                    Random r = new Random();
                    startCharacter = (char) (r.nextInt(WordDictionary.arrayUpperBound) + 'a');
                }
                String message = (Configuration.PlayOverBluetooth ? "Name" : "Type") + " a city that starts with the letter: " + Character.toUpperCase(startCharacter);
                TVQuestion.setText(message);
                Util.speak(message, new Runnable() {
                    @Override
                    public void run() {
                        if (Configuration.PlayOverBluetooth) {
                            game.CurrentState = EnumGameState.LISTEN_FOR_WORD;
                        } else {
                            game.CurrentState = EnumGameState.WAIT_FOR_TYPING;
                        }
                    }
                });
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
                ProcessAnswer(totalTime);
            }
        }
    }

    public void WaitForTyping() {
        InputMethodManager imm = (InputMethodManager) this.myActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

        this.ETAnswer.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView et, int keyCode, KeyEvent event) {
                if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
                        keyCode == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(ETAnswer.getWindowToken(), 0);

                    long timeStoppedListening = System.nanoTime();
                    double totalTime = (timeStoppedListening - timeStartedListening) / 1000000;
                    lastAnswer = ETAnswer.getText().toString().toLowerCase();
                    ProcessAnswer(totalTime);
                    return true;
                }
                return false;
            }
        });
    }

    public void ProcessAnswer(double totalTime) {
        final double totalTimeToAnswer = totalTime;
        // They gave an answer, let's make sure that it was a valid one
        handler.post(new Runnable() {
            @Override
            public void run() {
                String toSpeak = "";
                boolean noStrikesLeft = false;
                boolean isValidAnswer = true;

                //Answer is incorrect if:
                //1. Not in the dictionary of valid words
                //2. Doesn't start with the required letter
                try {
                    if (lastAnswer == null || lastAnswer.length() < WordDictionary.minWordLength) {
                        toSpeak = "Invalid answer.";
                        isValidAnswer = false;
                    } else if(lastAnswer == Configuration.SECRET_ANSWER /*boeing*/) {
                        toSpeak = "Mother-flower, you win the game.";
                        AchievementManager.Achievements.BOEING_WINS_ACCOMPLISHED = true;
                    } else if(lastAnswer.charAt(0) != startCharacter){
                        toSpeak = "Incorrect. " +  capitalizeFirstLetter(lastAnswer) + " does not start with letter " + startCharacter + ".";
                        isValidAnswer = false;
                    } else {
                        WordDictionary.WDResponse response = game.wd.MarkAsUsed(lastAnswer);
                        if (response == WordDictionary.WDResponse.INVALID) {
                            toSpeak = "Incorrect. " + capitalizeFirstLetter(lastAnswer) + " is not a valid city.";
                            isValidAnswer = false;
                        }else if (response == WordDictionary.WDResponse.BEEN_USED){
                            toSpeak = "Sorry. " + capitalizeFirstLetter(lastAnswer) + " has already been used.";
                            isValidAnswer = false;
                        } else if (totalTimeToAnswer < game.timeLimit) {
                            toSpeak = capitalizeFirstLetter(lastAnswer) + " is a valid city but you went over the time limit to answer the question.";
                            isValidAnswer = false;
                        } else {
                            toSpeak = capitalizeFirstLetter(lastAnswer) + " is correct!";
                        }
                    }

                    if(!isValidAnswer){
                        // Update the amount of strikes
                        int numOfStrikesLeft = game.FailedAnswer();
                        // Ruh Roh
                        if (numOfStrikesLeft < 1) {
                            // No more strikes, finish the game. You're OUT!
                            toSpeak += " 3 strikes.";
                            noStrikesLeft = true;
                        } else if (numOfStrikesLeft == 1){
                            // We still have a few more strikes
                            toSpeak += " This is your last strike.";
                        } else {
                            toSpeak += " " + Integer.toString(numOfStrikesLeft) + " strikes left.";
                        }
                    } else {
                        // Answer is correct! Add some points.
                        game.updateScore(totalTimeToAnswer);
                    }

                    // Update the score
                    displayScoreBoard();

                    TVQuestion.setText(toSpeak);
                    //Game is over if answer is not valid and player has no strikes left.
                    if(!isValidAnswer && noStrikesLeft) {
                        Util.speak(toSpeak, new Runnable() {
                            @Override
                            public void run() {
                                game.CurrentState = EnumGameState.GAME_OVER;
                            }
                        });
                    }else{
                        Util.speak(toSpeak, new Runnable() {
                            @Override
                            public void run() {
                                game.CurrentState = EnumGameState.RETORT;
                            }
                        });
                    }
                } catch (Exception ex) {
                    Log.e("Exception", ex.getMessage());
                }
            }
        });
    }

    public void displayScoreBoard() {
        TVPoints.setText("Points: " + this.game.getCurrentRunningScore());
        TVMultiplier.setText("Multiplier X" + this.game.getMultiplier());
        TVStrikes.setText("Strikes Left: " + this.game.getNumOfStrikesLeft());
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
                    Util.speak(toSpeak, new Runnable() {
                        @Override
                        public void run() {
                        game.CurrentState = EnumGameState.GAME_WON;
                        }
                    });
                } else {
                    toSpeak = "My turn. Something that starts with the letter " + Character.toUpperCase(startCharacter) + ". How about " + capitalizeFirstLetter(retort);
                    Util.speak(toSpeak, new Runnable() {
                        @Override
                        public void run() {
                            game.CurrentState = EnumGameState.NEXT_ROUND;
                        }
                    });
                    startCharacter = retort.charAt(retort.length() - 1);
                }
                TVQuestion.setText(toSpeak);
            }
        });
    }

    public void CountDown(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                String toSpeak = "Game starts in - 3. 2. 1. Go!";
                TVQuestion.setText(toSpeak);
                Util.speak(toSpeak, new Runnable() {
                    @Override
                    public void run() {
                        game.CurrentState = EnumGameState.ASK_FOR_WORD;
                    }
                });
            }
        });
    }

    public void NextRound(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                String toSpeak = "Your turn.";
                TVQuestion.setText(toSpeak);
                Util.speak(toSpeak, new Runnable() {
                    @Override
                    public void run() {
                        game.CurrentState = EnumGameState.ASK_FOR_WORD;
                    }
                });
            }
        });
    }

    public void GameOver(Boolean wonGame){
        this.startGameLoop.shouldRun = false;
        //push achievements
        AchievementManager.pushAchievements();
        this.game.GameOver(this.myContext);

        GameOverDialog gDialog = new GameOverDialog();
        gDialog.wonGame = wonGame;
        gDialog.onAttach(this.myActivity);
        gDialog.show(this.myActivity.getFragmentManager(), "Game_Over_Dialog");
    }

    private String capitalizeFirstLetter(String givenString){
        String[] arr = givenString.split(" ");
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < arr.length; i++) {
            sb.append(Character.toUpperCase(arr[i].charAt(0)))
                    .append(arr[i].substring(1)).append(" ");
        }
        return sb.toString().trim();
    }
}
