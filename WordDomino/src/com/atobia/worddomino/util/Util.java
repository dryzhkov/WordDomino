package com.atobia.worddomino.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

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

    public static boolean SaveGame(Context c, Game curGame){
        if(curGame == null){
            return false;
        }
        try {
            String dataToSave = new Gson().toJson(curGame);
            File target = EnsureFileExists(c,
                    Configuration.RESUME_GAME_DIR_NAME,
                    Configuration.RESUME_GAME_FILE_NAME,
                    Context.MODE_PRIVATE);
            FileOutputStream outputStream = new FileOutputStream(target);
            outputStream.write(dataToSave.getBytes());
            outputStream.close();
            Configuration.SavedGameExists = true;
        } catch (Exception e) {
            Log.e("Util::SaveGame::Ex", e.toString());
            return false;
        }
        return true;
    }

    public static Game LoadGame(Context c){
        if(!Configuration.SavedGameExists){
            return null;
        }
        Game loadedGame = null;
        StringBuilder data = new StringBuilder();
        try{
            File target = EnsureFileExists(c,
                    Configuration.RESUME_GAME_DIR_NAME,
                    Configuration.RESUME_GAME_FILE_NAME,
                    Context.MODE_PRIVATE);
            InputStream inputStream = new FileInputStream(target);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = reader.readLine();
            while(line != null){
                if(!line.isEmpty()){
                    data.append(line);
                }
                line = reader.readLine();
            }

            if(data.length() > 0) { //non-empty file
                loadedGame =  new Gson().fromJson(data.toString(), Game.class);
            }
        }catch(Exception ioex){
            Log.e("Util::LoadGame::Ex", ioex.toString());
            loadedGame =  null;
        }
        return loadedGame;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    public void speak(String msg, Runnable onFinish){
        final Runnable r = onFinish;
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {

            @Override
            public void onStart(String utteranceId) {
            }

            @Override
            public void onError(String utteranceId) {
            }

            @Override
            public void onDone(String utteranceId) {
                if(utteranceId.equals(Configuration.UTTERANCE_KEY)) {
                    tts.stop();
                    if(r != null) {
                        r.run();
                    }
                }
            }
        });

        tts.speak(msg, TextToSpeech.QUEUE_FLUSH, null, Configuration.UTTERANCE_KEY);
    }

}
