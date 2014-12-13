package com.atobia.worddomino;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.widget.TableLayout;
import android.widget.TableRow.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;

import com.atobia.worddomino.ui.PageAdapter;
import com.atobia.worddomino.ui.PageFragment;
import com.atobia.worddomino.ui.StatsPage;
import com.atobia.worddomino.ui.ZoomOutPageTransformer;
import com.atobia.worddomino.util.Configuration;
import com.atobia.worddomino.util.StatsManager;

public class StatsActivity extends FragmentActivity {
    private int[] gameDifficultyId = {Configuration.DifficultyLevel.EASY,
            Configuration.DifficultyLevel.MEDIUM,
            Configuration.DifficultyLevel.HARD};
    private String[] gameDifficultyName = {"Easy", "Medium", "Hard"};
    private StatsManager statsManager;
    private PageAdapter pageAdapter;
    private ViewPager viewPager;
    private String textColor = "#000000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        statsManager = new StatsManager(this);
        pageAdapter = new PageAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());

        if(!statsManager.IsInitiated()) {
            return;
        }

        StatsPage gamesPlayedPage = new StatsPage(R.layout.stats_totalgames_tab, getResources().getString(R.string.total_games_played_title));
        gamesPlayedPage.setOnStartFunc(new Runnable() {
            @Override
            public void run() {
                PageFragment frag = (PageFragment) pageAdapter.instantiateItem(viewPager, 0);
                TableLayout totalGames = (TableLayout)frag.rootView.findViewById(R.id.tblTotalGames);
                TableRow newRow = null;
                TextView newView = null;
                for(int i = 0; i < gameDifficultyId.length; i++) {
                    //create new row for total games
                    newRow = new TableRow(StatsActivity.this.getApplicationContext());
                    newRow.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT));
                    //column 1: difficulty name
                    newView = new TextView(StatsActivity.this.getApplicationContext());
                    newView.setText(gameDifficultyName[i]);
                    newView.setTextColor(Color.parseColor(textColor));
                    newRow.addView(newView);
                    //column 2: number of games played
                    newView = new TextView(StatsActivity.this.getApplicationContext());
                    newView.setText(String.valueOf(statsManager.GetTotalGames(gameDifficultyId[i])));
                    newView.setTextColor(Color.parseColor(textColor));
                    newRow.addView(newView);
                    //add row to the total games table
                    totalGames.addView(newRow, i);
                }
            }
        });
        pageAdapter.addPage(gamesPlayedPage);

        StatsPage highScorePage = new StatsPage(R.layout.stats_highscore_tab, getResources().getString(R.string.highest_score_title));
        highScorePage.setOnStartFunc(new Runnable() {
            @Override
            public void run() {
                PageFragment frag = (PageFragment) pageAdapter.instantiateItem(viewPager, 1);
                TableLayout highestScore = (TableLayout)frag.rootView.findViewById(R.id.tblHighestScores);
                TableRow newRow = null;
                TextView newView = null;
                for(int i = 0; i < gameDifficultyId.length; i++){
                    //create new row for highest scores
                    newRow = new TableRow(StatsActivity.this.getApplicationContext());
                    newRow.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT));
                    //column 1: difficulty name
                    newView = new TextView(StatsActivity.this.getApplicationContext());
                    newView.setText(gameDifficultyName[i]);
                    newView.setTextColor(Color.parseColor(textColor));
                    newRow.addView(newView);
                    //column 2: score
                    newView = new TextView(StatsActivity.this.getApplicationContext());
                    newView.setText(String.valueOf(statsManager.GetHighestScore(gameDifficultyId[i])));
                    newView.setTextColor(Color.parseColor(textColor));
                    newRow.addView(newView);
                    //add row to the highest score table
                    highestScore.addView(newRow, i);
                }
            }
        });
        pageAdapter.addPage(highScorePage);

        StatsPage longestStreakPage = new StatsPage(R.layout.stats_streak_tab, getResources().getString(R.string.longest_streak_title));
        longestStreakPage.setOnStartFunc(new Runnable() {
            @Override
            public void run() {
                PageFragment frag = (PageFragment) pageAdapter.instantiateItem(viewPager, 2);
                TableLayout longestStreaks = (TableLayout)frag.rootView.findViewById(R.id.tblLongestStreaks);
                TableRow newRow = null;
                TextView newView = null;
                for(int i = 0; i < gameDifficultyId.length; i++) {
                    newRow = new TableRow(StatsActivity.this.getApplicationContext());
                    newRow.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT));
                    //column 1: difficulty name
                    newView = new TextView(StatsActivity.this.getApplicationContext());
                    newView.setText(gameDifficultyName[i]);
                    newView.setTextColor(Color.parseColor(textColor));
                    newRow.addView(newView);
                    //column 2: streak value
                    newView = new TextView(StatsActivity.this.getApplicationContext());
                    newView.setText(String.valueOf(statsManager.GetLongestStreak(gameDifficultyId[i])));
                    newView.setTextColor(Color.parseColor(textColor));
                    newRow.addView(newView);
                    //add row to the longest streak table
                    longestStreaks.addView(newRow, i);
                }
            }
        });
        pageAdapter.addPage(longestStreakPage);

        StatsPage bestLetterPage = new StatsPage(R.layout.stats_bestletter_tab, getResources().getString(R.string.best_letter_title));
        bestLetterPage.setOnStartFunc(new Runnable() {
            @Override
            public void run() {
                PageFragment frag = (PageFragment) pageAdapter.instantiateItem(viewPager, 3);
                TextView bestLetterView = (TextView)frag.rootView.findViewById(R.id.tvBestLetter);

                StatsManager.Letter bestLetter = statsManager.GetBestLetter();
                if(bestLetter != null){
                    bestLetterView.setText(
                            String.format("Your best letter is '%s' with the current success ration of percent %.2f percent.",
                                    bestLetter.GetCharacter(),
                                    bestLetter.GetSuccessRatio() * 100));
                }else{
                    bestLetterView.setText("Unknown. Try playing a few more games.");
                }
            }
        });
        pageAdapter.addPage(bestLetterPage);

        viewPager.setAdapter(pageAdapter);
    }

    /*
    private void TestUsage(){
        Configuration.LoadSettings(this);
        StatsManager myStatsManager = new StatsManager(this);
        if(myStatsManager.IsInitiated()) {
            myStatsManager.IncrementTotalGames(Configuration.GameDifficulty);
            myStatsManager.UpdateHighestScore(400, Configuration.GameDifficulty);
            myStatsManager.UpdateStreak(25, Configuration.GameDifficulty);


            myStatsManager.UpdateLetterProgress("seattle", true);
            myStatsManager.UpdateLetterProgress("seattle", false);
            myStatsManager.UpdateLetterProgress("seattle", false);
            myStatsManager.UpdateLetterProgress("seattle", false);
            myStatsManager.UpdateLetterProgress("seattle", false);
            myStatsManager.UpdateLetterProgress("amega", true);
            myStatsManager.UpdateLetterProgress("amega", true);
            myStatsManager.UpdateLetterProgress("amega", false);
            myStatsManager.UpdateLetterProgress("amega", false);
            myStatsManager.UpdateLetterProgress("xmen", true);
            myStatsManager.UpdateLetterProgress("amega", false);
            myStatsManager.SaveStats();
            StatsManager.Letter myLetter = myStatsManager.GetBestLetter();
        }

    }
    */
}
