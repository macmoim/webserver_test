package com.macmoim.pang.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.macmoim.pang.R;

/**
 * Created by P11872 on 2015-07-23.
 */
public class MyFragment extends Fragment {

    private View mListView;

    //Method to return instance of the fragment. Passing in position to show which position is currently being shown in the fragment
    public static MyFragment getInstance(int position) {

        //Construct the fragment
        MyFragment myFragment = new MyFragment();

        //New bundle instance
        Bundle args = new Bundle();

        //Passing in the Integer position of the fragment into the argument
        args.putInt("position", position);

        //Setting the argument of the fragment to be the position
        myFragment.setArguments(args);

        //Return the fragment
        return myFragment;
    }

    @Override
    //This will handle how the fragment will display content
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle saveInstanceState) {
        //Inflate the fragment_main layout
//        View layout = inflater.inflate(R.layout.fragment_main, container, false);
//
//        //Initialising the text view
//        mListView = (View) layout.findViewById(R.id.list);
//        mListView.setVisibility(View.VISIBLE);

        Log.d("TTT", "MyFragment::onCreate");

        //Getting a reference to the TextView (as defined in fragment_main) and set it to a value
        Bundle bundle = getArguments();
        return null;
        //return layout;
    }
}
