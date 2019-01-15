package com.nazdesigns.polascope;

import android.os.Bundle;
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

import java.util.List;

public class RecyclerFragment extends Fragment {
    private String mUserId;
    private RecyclerView mRecyclerView;
    private LinearTextAdapter mAdapter;
    private List<TimeLapse> mData;
    private AppBarLayout appBar;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if ( user != null){
            mUserId = user.getUid();
        }

    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View activityView = getView();
        if (activityView == null){
            return;
        }
        appBar = activityView.findViewById(R.id.app_bar);
        Toolbar toolbar = appBar.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        AppCompatTextView upTitleTextView = toolbar.findViewById(R.id.toolbar_text);

        mRecyclerView = activityView.findViewById(R.id.text_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        DBCaller db = new FBCaller(mUserId);
        mData = db.getAllGames();
        mAdapter = new LinearTextAdapter(mData);
        mRecyclerView.setAdapter(mAdapter);

        // TODO: Traer el texto de BD
        // upTitleTextView.setText("AAAAAAAAAAAAAAAHHHHHHHHHHHHHH");
        // upTitleTextView.setText();
    }
}
