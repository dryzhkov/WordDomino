package com.atobia.worddomino.ui;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PageFragment extends Fragment {
    public static final String ARG_OBJECT = "fragment_args";
    public View rootView;
    public Page parentPage;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        Bundle args = getArguments();
        /*
            page index can be accessed as follows, if needed
            pageIndex = args.getInt(ARG_OBJECT);
         */
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(this.parentPage.getResourceId(), container, false);
        this.parentPage.onStart();
        return rootView;
    }

    @Override
    public void onStop (){
        super.onStop();
        this.parentPage.onFinish();
    }
}