package com.atobia.worddomino;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class RulesActivity extends Activity {

    TextView RuleTextOne, RuleTextTwo, RuleTextThree, RuleTextFour, RuleTextFive, RuleTextSix;
    Animation animationSlideInLeft;
    TextView curSlidingText;
    String curTextSlider = "";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rules);
        RuleTextOne = (TextView)findViewById(R.id.rule_text_one);
        RuleTextTwo = (TextView)findViewById(R.id.rule_text_two);
        RuleTextThree = (TextView)findViewById(R.id.rule_text_three);
        RuleTextFour = (TextView)findViewById(R.id.rule_text_four);
        RuleTextFive = (TextView)findViewById(R.id.rule_text_five);
        RuleTextSix = (TextView)findViewById(R.id.rule_text_six);

        animationSlideInLeft = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        animationSlideInLeft.setDuration(1500);
        animationSlideInLeft.setAnimationListener(animationSlideInLeftListener);

        RuleTextOne.startAnimation(animationSlideInLeft);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.clearAnimations();
    }

    private void clearAnimations() {
        RuleTextOne.clearAnimation();
        RuleTextTwo.clearAnimation();
        RuleTextThree.clearAnimation();
        RuleTextFour.clearAnimation();
        RuleTextFive.clearAnimation();
        RuleTextSix.clearAnimation();
    }

    AnimationListener animationSlideInLeftListener = new AnimationListener(){

        @Override
        public void onAnimationEnd(Animation animation) {
            Log.d("starting animating", "number " + curTextSlider);
            if (curSlidingText == null) {
                curTextSlider = "one";
                curSlidingText = RuleTextOne;
                curSlidingText.startAnimation(animationSlideInLeft);
                curSlidingText.setVisibility(View.VISIBLE);
            } else if (curSlidingText == RuleTextOne && curSlidingText.getVisibility() == View.VISIBLE) {
                curTextSlider = "two";
                curSlidingText.clearAnimation();
                curSlidingText = RuleTextTwo;
                curSlidingText.startAnimation(animationSlideInLeft);
                curSlidingText.setVisibility(View.VISIBLE);
            } else if (curSlidingText == RuleTextTwo && curSlidingText.getVisibility() == View.VISIBLE) {
                curTextSlider = "three";
                curSlidingText.clearAnimation();
                curSlidingText = RuleTextThree;
                curSlidingText.startAnimation(animationSlideInLeft);
                curSlidingText.setVisibility(View.VISIBLE);
            } else if (curSlidingText == RuleTextThree && curSlidingText.getVisibility() == View.VISIBLE) {
                curTextSlider = "four";
                curSlidingText.clearAnimation();
                curSlidingText = RuleTextFour;
                curSlidingText.startAnimation(animationSlideInLeft);
                curSlidingText.setVisibility(View.VISIBLE);
            } else if (curSlidingText == RuleTextFour && curSlidingText.getVisibility() == View.VISIBLE) {
                curTextSlider = "five";
                curSlidingText.clearAnimation();
                curSlidingText = RuleTextFive;
                curSlidingText.startAnimation(animationSlideInLeft);
                curSlidingText.setVisibility(View.VISIBLE);
            } else if (curSlidingText == RuleTextFive && curSlidingText.getVisibility() == View.VISIBLE) {
                curTextSlider = "six";
                curSlidingText.clearAnimation();
                curSlidingText = RuleTextSix;
                curSlidingText.startAnimation(animationSlideInLeft);
                curSlidingText.setVisibility(View.VISIBLE);
            }
            Log.d("end animating", "number " + curTextSlider);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationStart(Animation animation) {
        }};
}
