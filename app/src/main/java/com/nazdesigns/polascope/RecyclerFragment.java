package com.nazdesigns.polascope;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.nazdesigns.polascope.GameStructure.TimeLapse;
import com.nazdesigns.polascope.USoT.FBCaller;
import com.nazdesigns.polascope.Utilities.Common;

import java.util.List;

public class RecyclerFragment extends Fragment {
    final static private String TAG = "RecyclerFragment";
    private RecyclerView mRecyclerView;
    private LinearTextAdapter mAdapter;
    private LinearTextAdapter.SwipeHandler mSwipeHandler;
    private SwipeRefreshLayout mSwiperefresh;
    private ItemTouchHelper mItemTouchHelper;
    private String mFBId;
    private TimeLapse mTL;

    public static final String fbId = "fbId";

    public interface RefreshCallback{
        void completed();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Bundle args = getArguments();
        if (args != null){
            mFBId = args.getString(fbId,null);
        }

        mSwipeHandler = new LinearTextAdapter.SwipeHandler();
        mItemTouchHelper = new ItemTouchHelper(mSwipeHandler);

        GameActivity gameActivity = (GameActivity) context;
        mAdapter = new LinearTextAdapter(mFBId, gameActivity, mSwipeHandler);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mAdapter.detachListener();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
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
        RecyclerView activityView = (RecyclerView) getView().findViewById(R.id.text_recycler_view);
        final GameActivity gameActivity = (GameActivity) getActivity();
        if( (activityView == null) || (gameActivity == null) ){
            Log.e(TAG,"onActivityCreated: activity or view null");
            return;
        }


        mRecyclerView = activityView;
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayout = new LinearLayoutManager(gameActivity);
        mRecyclerView.setLayoutManager(linearLayout);
        mRecyclerView.setAdapter(mAdapter);

        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                mRecyclerView.getContext(), linearLayout.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        AppBarLayout appBarLayout = gameActivity.findViewById(R.id.app_bar);
        Toolbar toolbar = appBarLayout.findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);

        mSwiperefresh = (SwipeRefreshLayout) gameActivity.findViewById(R.id.swipe_refresh);

        mSwiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.refreshList(new RefreshCallback() {
                    @Override
                    public void completed() {
                        mSwiperefresh.setRefreshing(false);
                    }
                });
            }
        });

        final AppCompatTextView upTitleTextView = gameActivity.findViewById(R.id.toolbar_text);
        if (mFBId == null) {
            upTitleTextView.setText(getString(R.string.games_list_msg));
        } else {
            if (mTL == null) {
                FBCaller.getGame(mFBId, new FBCaller.onTLCallback() {
                    @Override
                    public void onTimeLapseResult(TimeLapse result) {
                        mTL = result;

                        AppBarLayout appBarLayout = gameActivity.findViewById(R.id.app_bar);
                        Toolbar toolbar = appBarLayout.findViewById(R.id.toolbar);

                        if (mTL.getTimeType() != TimeLapse.GAME_TYPE){
                            if (mTL.getIsLight()) {
                                toolbar.setLogo(R.mipmap.ic_light);
                            } else {
                                toolbar.setLogo(R.mipmap.ic_dark);
                            }
                        }

                        upTitleTextView.setText(mTL.getResume());

                        if(gameActivity.getSupportActionBar() != null){
                            Log.i(TAG, "Toolbar Found"+String.valueOf(mTL.getTimeType()));
                            switch(mTL.getTimeType()){
                                case TimeLapse.GAME_TYPE:
                                    toolbar.setTitle("Periodos");
                                    Log.i(TAG, "Periodos");
                                    break;
                                case TimeLapse.PERIOD_TYPE:
                                    toolbar.setTitle("Eventos");
                                    Log.i(TAG, "Eventos");
                                    break;
                                case TimeLapse.EVENT_TYPE:
                                    toolbar.setTitle("Escenas");
                                    Log.i(TAG, "Escenas");
                                    break;
                            }
                        }

                        gameActivity.setSupportActionBar(toolbar);
                    }
                });
            } else {
                if (mTL.getTimeType() != TimeLapse.GAME_TYPE) {
                    if (mTL.getIsLight()) {
                        toolbar.setLogo(R.mipmap.ic_light);
                    } else {
                        toolbar.setLogo(R.mipmap.ic_dark);
                    }
                }
                upTitleTextView.setText(mTL.getResume());
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater menuInflater) {
        if (mFBId == null){
            FBCaller.getPlayerGamesIds(new FBCaller.onListCallback() {
                @Override
                public void onListReturned(List<String> result) {
                    if(result.isEmpty()){
                        menu.findItem(R.id.add_to_empty).setVisible(true);
                    }
                }
            });
        } else {
            FBCaller.getGame(mFBId, new FBCaller.onTLCallback() {
                @Override
                public void onTimeLapseResult(TimeLapse result) {
                    mTL = result;

                    if(mTL.getSubEpochsIds()==null ||
                            mTL.getSubEpochsIds().isEmpty()){
                        menu.findItem(R.id.add_to_empty).setVisible(true);
                    }
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.log_out) {
            FirebaseAuth.getInstance().signOut();
            GameActivity activity = (GameActivity) this.getActivity();
            if (activity != null){
                activity.checkFBAuth();
            }
            return true;
        } else if (id == R.id.add_to_empty) {
            if (mFBId == null){
                // CREATE NEW GAME
                Log.i(TAG, "NEW GAME");
                Common.startCreateGameActivity(this.getContext(), TimeLapse.GAME_TYPE);
            } else {
                // CREATE TL
                Log.i(TAG, "NEW TL no GAME");
                //No podemos estar seguros de que mTl no es nulo, ponemos 1 para que no sea Game
                Common.startCreateActivity(this.getContext(), 1, mFBId, 0);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
