package com.atobia.worddomino.util;

/**
 * Created by dima on 1/25/2015.
 */

import android.app.Activity;
import android.util.Log;

import com.atobia.worddomino.R;
import com.google.android.gms.games.Games;

public class AchievementManager {
    private static Activity mActivity;
    private static final String TAG = "AchievementManager";

    public static void init(Activity activity){
        mActivity = activity;
    }

    public static void pushAchievements() {
        if (!Configuration.IsPlayerSignedIn()) {
            Log.d(TAG, "Not signed in. pushAchievements will now exit.");
            return;
        }
        Log.d(TAG, "Signed in. pushing achievements to the cloud.");
        if (Achievements.PRIME_ACCOMPLISHED) {
            Games.Achievements.unlock(Configuration.GMSClient, mActivity.getString(R.string.achievement_prime));
        }
        if (Achievements.BORED_ACCOMPLISHED){
            Games.Achievements.unlock(Configuration.GMSClient, mActivity.getString(R.string.achievement_bored));
        }
        if(Achievements.REALLY_BORED_ACCOMPLISHED) {
            Games.Achievements.unlock(Configuration.GMSClient, mActivity.getString(R.string.achievement_really_bored));
        }
        if(Achievements.STARTING_OUT_ACCOMPLISHED) {
            Games.Achievements.unlock(Configuration.GMSClient, mActivity.getString(R.string.achievement_starting_out));
        }
        if(Achievements.QUICK_RABBIT_ACCOMPLISHED) {
            Games.Achievements.unlock(Configuration.GMSClient, mActivity.getString(R.string.achievement_quick_rabbit));
        }
        if(Achievements.BOEING_WINS_ACCOMPLISHED) {
            Games.Achievements.unlock(Configuration.GMSClient, mActivity.getString(R.string.achievement_boeing_wins));
        }
    }

    public static class Achievements{
        public static boolean PRIME_ACCOMPLISHED = false;
        public static boolean BORED_ACCOMPLISHED = false;
        public static boolean REALLY_BORED_ACCOMPLISHED = false;
        public static boolean STARTING_OUT_ACCOMPLISHED = false;
        public static boolean QUICK_RABBIT_ACCOMPLISHED = false;
        public static boolean BOEING_WINS_ACCOMPLISHED = false;
    }
}


