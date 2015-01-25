package com.atobia.worddomino.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.atobia.worddomino.R;
import com.google.gson.Gson;

public class Util {
    private Context context;
    private TextToSpeech tts;

    public Util(Context c){
        this.context = c;
        tts = new TextToSpeech(this.context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR){
                    tts.setLanguage(Locale.US);
                }else{
                    Toast t = Toast.makeText(context,
                            "Text To Speech is not supported",
                            Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        });
    }
    public static WordDictionary LoadWordsFromFile(Context c){
        WordDictionary myDictionary = new WordDictionary();
        InputStream ins = c.getResources().openRawResource(R.raw.cities);
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

    public static int AtoI(char c){
        if(c == ' '){
            return WordDictionary.arrayUpperBound;
        }
        return c - WordDictionary.firstLetter;
    }

    public static char ItoA(int i){
        if(i == WordDictionary.arrayUpperBound){
            return ' ';
        }
        return (char) (i + WordDictionary.firstLetter);
    }

    public static File EnsureFileExists(Context c, String dirName, String fileName, int mode) throws IOException {
        File targetDir = c.getDir(dirName, mode);
        File targetFile = new File(targetDir, fileName);
        //targetFile.delete();
        if(!targetFile.exists()) {
            targetFile.createNewFile();
        }
        return targetFile;
    }

    public static byte[] GameToBytes(Game input){
        try {
            String dataToSave = new Gson().toJson(input);
            Log.d("Util::GameToBytes::Ex", dataToSave);
            return dataToSave.getBytes();
        }catch(Exception ex){
            Log.e("Util::GameToBytes::Ex", ex.toString());
        }
        return null;
    }
    public static Game BytesToGame(byte[] input){
        Game rtnGame = null;
        if(input != null){
            try {
                String dataToLoad = new String(input);
                Log.d("Util::GameToBytes::Ex", dataToLoad);
                rtnGame = new Gson().fromJson(dataToLoad, Game.class);
            }catch(Exception ex){
                Log.e("Util::BytesToGame::Ex", ex.toString());
            }
        }
        return rtnGame;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    public void speak(String msg, Runnable onFinish){
        final Runnable r = onFinish;
        final String uniqueID = UUID.randomUUID().toString();
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {

            @Override
            public void onStart(String utteranceId) {
            }

            @Override
            public void onError(String utteranceId) {
            }

            @Override
            public void onDone(String utteranceId) {
                if(utteranceId.equals(uniqueID)) {
                    tts.stop();
                    if(r != null) {
                        r.run();
                    }
                }
            }
        });

        HashMap<String, String> params = new HashMap<>();

        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, uniqueID);

        tts.speak(msg,TextToSpeech.QUEUE_FLUSH, params);
    }

}
