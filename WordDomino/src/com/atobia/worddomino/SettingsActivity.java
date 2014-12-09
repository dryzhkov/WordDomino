package com.atobia.worddomino;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.atobia.worddomino.util.Configuration;
import com.atobia.worddomino.ui.TabPagerAdapter;

public class SettingsActivity extends FragmentActivity {
    public static final int NUMBER_OF_TABS = 2;
    public static String gameDifficultyTitle;
    public static String firstPlayerTitle;

    TabPagerAdapter pageAdapter;
    ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        gameDifficultyTitle = getResources().getString(R.string.game_difficulty_title);
        firstPlayerTitle = getResources().getString(R.string.first_player_title);

        pageAdapter = new TabPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
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
