package com.nazdesigns.polascope;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nazdesigns.polascope.USoT.FBCaller;

public class RecyclerFragment extends Fragment {
    private String TAG = "RecyclerFragment";
    private RecyclerView mRecyclerView;
    private LinearTextAdapter mAdapter;
    private String mFBId;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Bundle args = getArguments();
        if (args != null){
            mFBId = args.getString("fbId",null);
        }

        mAdapter = new LinearTextAdapter(mFBId);
        GameActivity gameActivity = (GameActivity) context;
        mAdapter.setListener(gameActivity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mAdapter.detacchListener();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        LinearLayoutManager linearLayout = new LinearLayoutManager(gameActivity);
        mRecyclerView.setLayoutManager(linearLayout);
        mRecyclerView.setAdapter(mAdapter);

        SwipeHandler swipeHandler = new SwipeHandler();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeHandler);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                linearLayout.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        AppCompatTextView upTitleTextView = gameActivity.findViewById(R.id.toolbar_text);
        String text;
        if (mFBId == null) {
            text = getString(R.string.games_list_msg);
        } else {
            text = FBCaller.getResume(mFBId);
            boolean isLight = FBCaller.getLight(mFBId);

            AppBarLayout appBarLayout = gameActivity.findViewById(R.id.app_bar);
            Toolbar toolbar = appBarLayout.findViewById(R.id.toolbar);
            if (isLight){
                toolbar.setLogo(R.mipmap.ic_light);
            } else {
                toolbar.setLogo(R.mipmap.ic_dark);
            }
        }
        upTitleTextView.setText(text);
    }

//    public void setListener(LinearTextAdapter.onListListener listener){
//        mAdapter.setListener(listener);
//    }
}
