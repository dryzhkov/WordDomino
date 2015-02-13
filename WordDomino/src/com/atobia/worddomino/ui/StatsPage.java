package com.atobia.worddomino.ui;

/**
 * Created by dima on 12/12/2014.
 */
public class StatsPage extends Page {

    public StatsPage(int resource, String title){
        super(resource, title);
    }

    @Override
    public void onStart(){
        if(this.onStartFunc != null) {
            onStartFunc.run();
        }
    }

    @Override
    public void onFinish(){}
}