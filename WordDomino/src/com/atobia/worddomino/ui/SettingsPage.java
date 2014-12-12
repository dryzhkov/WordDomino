package com.atobia.worddomino.ui;

/**
 * Created by dima on 12/11/2014.
 */
public class SettingsPage extends Page {
    private Runnable onStartFunc;
    private Runnable onFinishFunc;

    public SettingsPage(int resource, String title){
        super(resource, title);
    }

    public void setOnStartFunc(Runnable func){
        this.onStartFunc = func;
    }

    public void setOnFinishFunc(Runnable func){
        this.onFinishFunc = func;
    }

    @Override
    public void onStart(){
        if(onStartFunc != null) {
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
