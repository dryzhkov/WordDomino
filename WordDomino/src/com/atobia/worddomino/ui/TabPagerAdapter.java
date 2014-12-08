package com.atobia.worddomino.ui;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.atobia.worddomino.SettingsActivity;

/**
 * Created by dima on 12/7/2014.
 */
public class TabPagerAdapter extends FragmentStatePagerAdapter{
    public TabPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {
        Fragment fragment = new SwipeTabFragment();
        Bundle args = new Bundle();
        args.putInt(SwipeTabFragment.ARG_OBJECT, index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return SettingsActivity.NUMBER_OF_TABS;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch(position){
            case 0:
                title = SettingsActivity.gameDifficultyTitle;
                break;
            case 1:
                title = SettingsActivity.firstPlayerTitle;
                break;
            default:
                break;
        }
        return title;
    }
}

