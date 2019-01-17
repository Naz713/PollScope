package com.nazdesigns.polascope;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nazdesigns.polascope.GameStructure.TimeLapse;
import com.nazdesigns.polascope.USoT.DBCaller;
import com.nazdesigns.polascope.USoT.FBCaller;

import java.util.Arrays;
import java.util.List;

public class RecyclerFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private LinearTextAdapter mAdapter;
    private List<TimeLapse> mData;
    private String mText;
    private int[] mIndex;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mIndex = args.getIntArray("index");
        // TODO incializar texto y lista
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RecyclerView activityView = (RecyclerView) getView();
        GameActivity gameActivity = (GameActivity) getActivity();
        if( (activityView == null) || (gameActivity == null) ){
            // TODO: Hacer algo.
            return;
        }

        mRecyclerView = activityView;
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(gameActivity));

        mAdapter = new LinearTextAdapter(mData, mIndex);
        mRecyclerView.setAdapter(mAdapter);

        AppCompatTextView upTitleTextView = gameActivity.findViewById(R.id.toolbar_text);
        upTitleTextView.setText(mText);
    }

    public void setListener(LinearTextAdapter.onListListener listener){
        mAdapter.setListener(listener);
    }
}
