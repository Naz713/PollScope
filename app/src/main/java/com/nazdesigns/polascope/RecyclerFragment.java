package com.nazdesigns.polascope;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nazdesigns.polascope.GameStructure.TimeLapse;

import java.util.List;

public class RecyclerFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private LinearTextAdapter mAdapter;
    private List<TimeLapse> mData;
    private AppBarLayout appBar;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        appBar = activity.findViewById(R.id.app_bar);
        Toolbar toolbar = appBar.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        mRecyclerView = activity.findViewById(R.id.text_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(activity.getApplicationContext()));
        return inflater.inflate(R.layout.fragment_recycler, container, false);
    }
}
