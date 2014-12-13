package com.atobia.worddomino;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.atobia.worddomino.ui.DepthPageTransformer;
import com.atobia.worddomino.ui.SettingsPage;
import com.atobia.worddomino.ui.PageFragment;
import com.atobia.worddomino.util.Configuration;
import com.atobia.worddomino.ui.PageAdapter;

public class SettingsActivity extends FragmentActivity {
    private PageAdapter pageAdapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SettingsPage gameDiffPage = new SettingsPage(R.layout.settings_gamediff_tab,
                getResources().getString(R.string.game_difficulty_title));

        SettingsPage firstPlayerPage = new SettingsPage(R.layout.settings_firstplayer_tab,
                getResources().getString(R.string.first_player_title));

        gameDiffPage.setOnStartFunc(new Runnable() {
            @Override
            public void run() {
                PageFragment frag = (PageFragment) pageAdapter.instantiateItem(viewPager, 0);
                RadioGroup rgDifficulty = (RadioGroup) frag.rootView.findViewById(R.id.radioGroupGameDiff);
                //set game difficulty
                if (Configuration.GameDifficulty == Configuration.DifficultyLevel.HARD) {
                    rgDifficulty.check(R.id.rb_gamediff_hard);
                } else if (Configuration.GameDifficulty == Configuration.DifficultyLevel.MEDIUM) {
                    rgDifficulty.check(R.id.rb_gamediff_medium);
                } else { //default: easy
                    rgDifficulty.check(R.id.rb_gamediff_easy);
                }
            }
        });

        gameDiffPage.setOnFinishFunc(new Runnable() {
            @Override
            public void run() {
                PageFragment frag = (PageFragment) pageAdapter.instantiateItem(viewPager, 0);
                RadioGroup rgDifficulty = (RadioGroup) frag.rootView.findViewById(R.id.radioGroupGameDiff);
                View selectedButton = rgDifficulty.findViewById(rgDifficulty.getCheckedRadioButtonId());
                int selectedIndex = rgDifficulty.indexOfChild(selectedButton);
                if (selectedIndex == 2) {
                    Configuration.GameDifficulty = Configuration.DifficultyLevel.HARD;
                } else if (selectedIndex == 1) {
                    Configuration.GameDifficulty = Configuration.DifficultyLevel.MEDIUM;
                } else { //default: easy
                    Configuration.GameDifficulty = Configuration.DifficultyLevel.EASY;
                }
            }
        });

        firstPlayerPage.setOnStartFunc(new Runnable() {
            @Override
            public void run() {
                PageFragment frag = (PageFragment) pageAdapter.instantiateItem(viewPager, 1);
                RadioGroup rgFirstPlayer = (RadioGroup)frag.rootView.findViewById(R.id.radioGroupFirstPlayer);
                //set first player
                if(Configuration.FirstPlayer == Configuration.PlayerType.Player){
                    rgFirstPlayer.check(R.id.rb_firstplayer_player);
                }else{ //default: AI
                    rgFirstPlayer.check(R.id.rb_firstplayer_ai);
                }
            }
        });

        firstPlayerPage.setOnFinishFunc(new Runnable() {
            @Override
            public void run() {
                PageFragment frag = (PageFragment) pageAdapter.instantiateItem(viewPager, 1);
                RadioGroup rgFirstPlayer = (RadioGroup)frag.rootView.findViewById(R.id.radioGroupFirstPlayer);
                View selectedButton = rgFirstPlayer.findViewById(rgFirstPlayer.getCheckedRadioButtonId());
                int selectedIndex = rgFirstPlayer.indexOfChild(selectedButton);

                if(selectedIndex == 1){
                    Configuration.FirstPlayer = Configuration.PlayerType.Player;
                }else{ //default: AI
                    Configuration.FirstPlayer = Configuration.PlayerType.AI;
                }
            }
        });
        pageAdapter = new PageAdapter(getSupportFragmentManager());
        pageAdapter.addPage(gameDiffPage);
        pageAdapter.addPage(firstPlayerPage);
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setPageTransformer(true, new DepthPageTransformer());
        viewPager.setAdapter(pageAdapter);

        Configuration.LoadSettings(this);
}

    @Override
    public void onBackPressed(){
        Configuration.SaveSettings(this);
        SettingsActivity.this.finish();
    }

    @Override
    public void onStop(){
        super.onStop();
        Configuration.SaveSettings(this);
        Toast.makeText(getApplicationContext(),
                "Saving Settings",
                Toast.LENGTH_SHORT).show();
    }
}
