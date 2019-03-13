package com.nazdesigns.polascope;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.nazdesigns.polascope.GameStructure.TimeLapse;
import com.nazdesigns.polascope.USoT.FBCaller;

import java.lang.ref.WeakReference;

public class RecyclerFragment extends Fragment {
    private String TAG = "RecyclerFragment";
    private RecyclerView mRecyclerView;
    private LinearTextAdapter mAdapter;
    private String mFBId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

        mFBId = args.getString("fbId",null);
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
            Log.e(TAG,"onActivityCreated: activity or view null");
            return;
        }

        mRecyclerView = activityView;
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(gameActivity));

        mAdapter = new LinearTextAdapter(mFBId);
        mRecyclerView.setAdapter(mAdapter);

        AppCompatTextView upTitleTextView = gameActivity.findViewById(R.id.toolbar_text);
        String text;
        if (mFBId == null) {
            text = getString(R.string.app_name);
        } else {
            text = FBCaller.getResume(mFBId);
        }
        upTitleTextView.setText(text);
    }

    public void setListener(LinearTextAdapter.onListListener listener){
        mAdapter.setListener(listener);
    }
}
