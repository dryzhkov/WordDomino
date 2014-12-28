package com.atobia.worddomino;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
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
                for(int i = 0; i < gameDifficultyId.length; i++) {
                    //add row to the total games table
                    TableRow newRow = createTableRow(gameDifficultyName[i],
                            String.valueOf(statsManager.GetTotalGames(gameDifficultyId[i])));
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
                for(int i = 0; i < gameDifficultyId.length; i++){
                    //add row to the highest score table
                    TableRow newRow = createTableRow(gameDifficultyName[i],
                            String.valueOf(statsManager.GetHighestScore(gameDifficultyId[i])));
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
                for(int i = 0; i < gameDifficultyId.length; i++) {
                    //add row to the longest streak table
                    TableRow newRow = createTableRow(gameDifficultyName[i],
                            String.valueOf(statsManager.GetLongestStreak(gameDifficultyId[i])));
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
                bestLetterView.setTextSize(TypedValue.COMPLEX_UNIT_PT, StatsPage.txtSize);
                bestLetterView.setTextColor(Color.parseColor(StatsPage.textColor));
                StatsManager.Letter bestLetter = statsManager.GetBestLetter();
                if(bestLetter != null){
                    bestLetterView.setText(
                            String.format("Your best letter is '%s' with a success rate of %.2f percent.",
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

    private TableRow createTableRow(String col1, String col2){
        TableRow row = new TableRow(StatsActivity.this.getApplicationContext());
        row.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT));
        //column 1
        TextView view = new TextView(StatsActivity.this.getApplicationContext());
        view.setText(col1);
        view.setTextSize(TypedValue.COMPLEX_UNIT_PT, StatsPage.txtSize);
        view.setTextColor(Color.parseColor(StatsPage.textColor));
        row.addView(view);
        //column 2
        view = new TextView(StatsActivity.this.getApplicationContext());
        view.setText(col2);
        view.setTextSize(TypedValue.COMPLEX_UNIT_PT, StatsPage.txtSize);
        view.setTextColor(Color.parseColor(StatsPage.textColor));
        row.addView(view);
        return row;
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
