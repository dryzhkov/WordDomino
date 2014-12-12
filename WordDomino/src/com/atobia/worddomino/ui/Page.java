package com.atobia.worddomino.ui;

import java.util.concurrent.Callable;

/**
 * Created by dima on 12/11/2014.
 */
public abstract class Page {
    //members
    private int resourceId;
    private String pageTitle;

    public Page(int r, String t){
        this.resourceId = r;
        this.pageTitle = t;
    }

    public int getResourceId(){
        return this.resourceId;
    }
    public String getPageTitle(){ return this.pageTitle; }

    abstract void onStart();
    abstract void onFinish();
}
