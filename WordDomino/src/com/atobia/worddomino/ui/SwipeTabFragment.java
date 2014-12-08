package com.atobia.worddomino.ui;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import com.atobia.worddomino.R;
import com.atobia.worddomino.util.Configuration;

public class SwipeTabFragment extends Fragment {
    public static final String ARG_OBJECT = "settings_fragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        int curPageIndex = args.getInt(ARG_OBJECT);
        View rootView = null;
        switch(curPageIndex){
            case 0:
                rootView = inflater.inflate(R.layout.settings_gamediff_tab, container, false);
                RadioGroup rgDifficulty = (RadioGroup)rootView.findViewById(R.id.radioGroupGameDiff);
                //set game difficulty
                if(Configuration.GameDifficulty == Configuration.DifficultyLevel.HARD){
                    rgDifficulty.check(R.id.rb_gamediff_hard);
                }else if(Configuration.GameDifficulty == Configuration.DifficultyLevel.MEDIUM){
                    rgDifficulty.check(R.id.rb_gamediff_medium);
                }else{ //default: easy
                    rgDifficulty.check(R.id.rb_gamediff_easy);
                }
                break;
            case 1:
                rootView = inflater.inflate(R.layout.settings_firstplayer_tab, container, false);
                RadioGroup rgFirstPlayer = (RadioGroup)rootView.findViewById(R.id.radioGroupFirstPlayer);
                //set first player
                if(Configuration.FirstPlayer == Configuration.PlayerType.Player){
                    rgFirstPlayer.check(R.id.rb_firstplayer_player);
                }else{ //default: AI
                    rgFirstPlayer.check(R.id.rb_firstplayer_ai);
                }
                break;
            default:
                break;
        }
        return rootView;
    }

    @Override
    public void onStop (){
        super.onStop();
        View rootView;
        int selectedOptionId;
        View selectedButton;
        int selectedIndex;
        rootView = this.getView();
        Bundle args = getArguments();
        int curPageIndex = args.getInt(ARG_OBJECT);

        switch(curPageIndex){
            case 0: //game difficulty
                RadioGroup rgDifficulty = (RadioGroup)rootView.findViewById(R.id.radioGroupGameDiff);
                selectedOptionId = rgDifficulty.getCheckedRadioButtonId();
                selectedButton = rgDifficulty.findViewById(selectedOptionId);
                selectedIndex = rgDifficulty.indexOfChild(selectedButton);
                if(selectedIndex == 2){
                    Configuration.GameDifficulty = Configuration.DifficultyLevel.HARD;
                }else if(selectedIndex == 1){
                    Configuration.GameDifficulty = Configuration.DifficultyLevel.MEDIUM;
                }else{ //default: easy
                    Configuration.GameDifficulty = Configuration.DifficultyLevel.EASY;
                }
                break;
            case 1: //first player
                RadioGroup rgFirstPlayer = (RadioGroup)rootView.findViewById(R.id.radioGroupFirstPlayer);
                selectedOptionId = rgFirstPlayer.getCheckedRadioButtonId();
                selectedButton = rgFirstPlayer.findViewById(selectedOptionId);
                selectedIndex = rgFirstPlayer.indexOfChild(selectedButton);

                if(selectedIndex == 1){
                    Configuration.FirstPlayer = Configuration.PlayerType.Player;
                }else{ //default: AI
                    Configuration.FirstPlayer = Configuration.PlayerType.AI;
                }
                break;
            default:
                break;
        }
    }
}