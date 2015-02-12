package com.atobia.worddomino;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class RulesActivity extends Activity {

    TextView RuleTextOne, RuleTextTwo, RuleTextThree;
    Animation animationSlideInLeft, animationSlideOutRight;
    TextView curSlidingText;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rules);
        RuleTextOne = (TextView)findViewById(R.id.rule_text_one);
        RuleTextTwo = (TextView)findViewById(R.id.rule_text_two);
        RuleTextThree = (TextView)findViewById(R.id.rule_text_three);

        animationSlideInLeft = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        animationSlideInLeft.setDuration(1500);
        animationSlideInLeft.setAnimationListener(animationSlideInLeftListener);

        RuleTextOne.startAnimation(animationSlideInLeft);
    }

    @Override
    protected void onPause() {
        super.onPause();
        RuleTextOne.clearAnimation();
        RuleTextTwo.clearAnimation();
        RuleTextThree.clearAnimation();
    }

    AnimationListener animationSlideInLeftListener = new AnimationListener(){

        @Override
        public void onAnimationEnd(Animation animation) {
            if (curSlidingText == null) {
                curSlidingText = RuleTextOne;
                curSlidingText.startAnimation(animationSlideInLeft);
                curSlidingText.setVisibility(View.VISIBLE);
            } else if (curSlidingText == RuleTextOne) {
                curSlidingText.clearAnimation();
                curSlidingText = RuleTextTwo;
                curSlidingText.startAnimation(animationSlideInLeft);
                curSlidingText.setVisibility(View.VISIBLE);
            } else if (curSlidingText == RuleTextTwo) {
                curSlidingText.clearAnimation();
                curSlidingText = RuleTextThree;
                curSlidingText.startAnimation(animationSlideInLeft);
                curSlidingText.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationStart(Animation animation) {
        }};
}
