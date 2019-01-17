package com.nazdesigns.polascope;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nazdesigns.polascope.GameStructure.TimeLapse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
Responsable de llenar el recler view dado una lista de TimeLapse
 */

public class LinearTextAdapter extends RecyclerView.Adapter<LinearTextAdapter.TextViewHolder> {
    private List<TimeLapse> mDataset;
    private int[] mIndex;

    public static class TextViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public TextView mResume;
        public TextView mLongText;
        public int[] mIndex;

        public TextViewHolder(View v) {
            super(v);
            mResume = v.findViewById(R.id.resume);
            mLongText = v.findViewById(R.id.longText);
        }

        public void setIndex(int[] index){
            mIndex = index;
        }

        @Override
        public void onClick(View v) {
            View longText = v.findViewById(R.id.longText);
            if (longText.getVisibility() == View.GONE) {
                longText.setVisibility(View.VISIBLE);
            } else {
                longText.setVisibility(View.GONE);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (v.getId() == R.id.longText) {
                Intent intent = new Intent(v.getContext(), EditActivity.class);
                intent.putExtra("index", mIndex);
                v.getContext().startActivity(intent);
            } else {
                /*
                TODO: llama a GameActivity para renderizar fragmento con la información del sub timelapse
                */
            }
            return false;
        }
    }

    public LinearTextAdapter(List<TimeLapse> myDataset, int[] index) {
        mDataset = myDataset;
        mIndex = index;
    }

    @NonNull
    @Override
    public LinearTextAdapter.TextViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                     int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.text_layout, parent, false);
        return new TextViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TextViewHolder holder, int position) {
        int[] subIndex = mIndex.clone();
        for (int i : mIndex){
            if(i == -1){
                mIndex[i] = position;
                break;
            }
        }
        holder.setIndex(subIndex);
        if(mDataset.get(position).getLight()){
            holder.mResume.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.ic_light,0,0, 0);
        }
        else {
            holder.mResume.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.ic_dark,0,0, 0);
        }
        holder.mResume.setText(mDataset.get(position).getResume());
        holder.mLongText.setText(mDataset.get(position).getBody());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}