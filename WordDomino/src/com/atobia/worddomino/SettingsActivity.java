package com.atobia.worddomino;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.atobia.worddomino.ui.SettingsPage;
import com.atobia.worddomino.ui.PageFragment;
import com.atobia.worddomino.ui.ZoomOutPageTransformer;
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

        SettingsPage playOverBluetoothPage = new SettingsPage(R.layout.settings_playoverbluetooth_tab,
                getResources().getString(R.string.play_type_title));

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
                PageFragment frag = (PageFragment)pageAdapter.instantiateItem(viewPager, 0);
                Object a = frag.rootView.findViewById(R.id.radioGroupGameDiff);
                RadioGroup rgDifficulty = (RadioGroup)a;
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

        playOverBluetoothPage.setOnStartFunc(new Runnable() {
            @Override
            public void run() {
                PageFragment frag = (PageFragment) pageAdapter.instantiateItem(viewPager, 1);
                RadioGroup rgPlayOverBluetooth = (RadioGroup)frag.rootView.findViewById(R.id.radioGroupPlayOverBluetooth);
                //set first player
                if(Configuration.PlayOverBluetooth){
                    rgPlayOverBluetooth.check(R.id.rb_playtype_bluetooth);
                }else{
                    rgPlayOverBluetooth.check(R.id.rb_playtype_textbox);
                }
            }
        });

        playOverBluetoothPage.setOnFinishFunc(new Runnable() {
            @Override
            public void run() {
                PageFragment frag = (PageFragment) pageAdapter.instantiateItem(viewPager, 1);
                RadioGroup rgPlayOverBluetooth = (RadioGroup)frag.rootView.findViewById(R.id.radioGroupPlayOverBluetooth);
                View selectedButton = rgPlayOverBluetooth.findViewById(rgPlayOverBluetooth.getCheckedRadioButtonId());
                int selectedIndex = rgPlayOverBluetooth.indexOfChild(selectedButton);

                Configuration.PlayOverBluetooth = (selectedIndex == 0);
            }
        });
        pageAdapter = new PageAdapter(getSupportFragmentManager());
        pageAdapter.addPage(gameDiffPage);
        //pageAdapter.addPage(firstPlayerPage);
        pageAdapter.addPage(playOverBluetoothPage);
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        viewPager.setAdapter(pageAdapter);

        //Configuration.LoadSettings(this);
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
