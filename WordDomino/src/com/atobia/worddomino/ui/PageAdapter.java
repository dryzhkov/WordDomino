package com.atobia.worddomino.ui;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import java.util.ArrayList;

/**
 * Created by dima on 12/7/2014.
 */
public class PageAdapter extends FragmentStatePagerAdapter{
    private ArrayList<Page> listOfPages;

    public PageAdapter(FragmentManager fm){
        super(fm);
        this.listOfPages = new ArrayList<Page>();
    }

    public void addPage(Page p){
        this.listOfPages.add(p);
    }
    @Override
    public Fragment getItem(int index) {
        PageFragment fragment = new PageFragment();
        fragment.parentPage = listOfPages.get(index);
        Bundle args = new Bundle();
        args.putInt(PageFragment.ARG_OBJECT, index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return this.listOfPages.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return this.listOfPages.get(position).getPageTitle();
    }
}

