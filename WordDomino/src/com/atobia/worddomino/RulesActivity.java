package com.atobia.worddomino;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class RulesActivity extends Activity {
    private Animation animationSlideInLeft;
    private int currentIndexOfRule = 0;
    private TextView[] AllRules;

    final static int timeToSlide = 1500;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rules);

        AllRules = new TextView[6];

        AllRules[0] = (TextView)findViewById(R.id.rule_text_one);
        AllRules[1] = (TextView)findViewById(R.id.rule_text_two);
        AllRules[2] = (TextView)findViewById(R.id.rule_text_three);
        AllRules[3] = (TextView)findViewById(R.id.rule_text_four);
        AllRules[4] = (TextView)findViewById(R.id.rule_text_five);
        AllRules[5] = (TextView)findViewById(R.id.rule_text_six);

        animationSlideInLeft = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        animationSlideInLeft.setDuration(timeToSlide);
        animationSlideInLeft.setAnimationListener(animationSlideInLeftListener);

        AllRules[0].startAnimation(animationSlideInLeft);
        AllRules[0].setVisibility(View.VISIBLE);

        new CountDownTimer(timeToSlide * (AllRules.length + 1), timeToSlide) {

            public void onTick(long millisUntilFinished) {
                clearAnimations();
                if (currentIndexOfRule < AllRules.length) {
                    AllRules[currentIndexOfRule].startAnimation(animationSlideInLeft);
                    AllRules[currentIndexOfRule].setVisibility(View.VISIBLE);
                    currentIndexOfRule++;
                }
            }

            public void onFinish() {}
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.clearAnimations();
    }

    private void clearAnimations() {
        for (int i = 0; i < AllRules.length; i++) {
            AllRules[i].clearAnimation();
        }
    }

    AnimationListener animationSlideInLeftListener = new AnimationListener(){

        @Override
        public void onAnimationEnd(Animation animation) { }

        @Override
        public void onAnimationRepeat(Animation animation) { }

        @Override
        public void onAnimationStart(Animation animation) { }
    };
}
