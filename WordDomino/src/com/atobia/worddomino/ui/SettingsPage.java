package com.atobia.worddomino.ui;

/**
 * Created by dima on 12/11/2014.
 */
public class SettingsPage extends Page {
    public SettingsPage(int resource, String title){
        super(resource, title);
    }

    @Override
    public void onStart(){
        if(this.onStartFunc != null) {
            onStartFunc.run();
        }
    }

    @Override
    public void onFinish(){
        if(onFinishFunc != null) {
            onFinishFunc.run();
        }
    }
}