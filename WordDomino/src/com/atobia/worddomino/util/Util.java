package com.atobia.worddomino.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.UUID;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import com.atobia.worddomino.R;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.games.snapshot.Snapshots;
import com.google.gson.Gson;

public class Util {

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

    public static void SaveGame(Game g){
        final boolean createIfMissing = true;
        final String SNAP_SHOT_NAME = "Snapshot_0";
        final byte[] data  = GameToBytes(g);

        AsyncTask<Void, Void, Boolean> updateTask = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                if (!Configuration.GMSClient.isConnected()) {
                    Configuration.GMSClient.connect();
                }
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                Snapshots.OpenSnapshotResult open = Games.Snapshots.open(
                        Configuration.GMSClient, SNAP_SHOT_NAME, createIfMissing).await();

                if (!open.getStatus().isSuccess()) {
                    Log.d("Util::SaveGame", "Could not open Snapshot for update.");
                    return false;
                }

                int maxSnapshotSize = Games.Snapshots.getMaxDataSize(Configuration.GMSClient);
                int dataLength = data.length;

                if(dataLength > maxSnapshotSize)
                {
                    Log.d("Util::SaveGame", "Game content size (" + dataLength + ") is larger than maximum allowed snapshot size of " + maxSnapshotSize + " bytes. Will exit now.");
                    return false;
                }
                // Change data but leave existing metadata
                Snapshot snapshot = open.getSnapshot();
                snapshot.getSnapshotContents().writeBytes(data);

                Snapshots.CommitSnapshotResult commit = Games.Snapshots.commitAndClose(
                        Configuration.GMSClient, snapshot, SnapshotMetadataChange.EMPTY_CHANGE).await();

                if (!commit.getStatus().isSuccess()) {
                    Log.d("Util::SaveGame", "Failed to commit Snapshot.");
                    return false;
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if(result){
                    Log.d("Util::SaveGame", "Success Saving Same.");
                    Configuration.SavedGameExists = true;
                }else{
                    Log.d("Util::SaveGame", "Failure Saving Same.");
                }
            }
        };
        updateTask.execute();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    public static void speak(String msg, Runnable onFinish){
        if(Configuration.TTS == null){
            throw new IllegalStateException("TextToSpeech has not been instantiated.");
        }
        final Runnable r = onFinish;
        final String uniqueID = UUID.randomUUID().toString();
        Configuration.TTS.setOnUtteranceProgressListener(new UtteranceProgressListener() {

            @Override
            public void onStart(String utteranceId) {
            }

            @Override
            public void onError(String utteranceId) {
                Configuration.TTS.stop();
            }

            @Override
            public void onDone(String utteranceId) {
                if(utteranceId.equals(uniqueID)) {
                    Configuration.TTS.stop();
                    if(r != null) {
                        r.run();
                    }
                }
            }
        });

        HashMap<String, String> params = new HashMap<>();

        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, uniqueID);
        Log.d("Util::Speak()", "Now speaking word: [" + msg + "]");
        Configuration.TTS.speak(msg,TextToSpeech.QUEUE_FLUSH, params);
    }
}
