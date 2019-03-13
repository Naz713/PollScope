package com.nazdesigns.polascope;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.nazdesigns.polascope.GameStructure.TimeLapse;
import com.nazdesigns.polascope.USoT.FBCaller;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
Responsable de llenar el recler view dado una lista de TimeLapse
 */

public class LinearTextAdapter extends RecyclerView.Adapter<LinearTextAdapter.TextViewHolder> {
    private List<String> mDataset;
    private String mFBId;
    private onListListener listener;

    public interface onListListener{
        void onClickListElement(String id);
    }

    public static class TextViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public TextView mResume;
        public TextView mLongText;
        public String mId;
        public WeakReference<onListListener> listListener;

        public TextViewHolder(View v, onListListener listener) {
            super(v);
            listListener = new WeakReference<>(listener);
            mResume = v.findViewById(R.id.resume);
            mLongText = v.findViewById(R.id.longText);
        }

        public void setId(String id){
            mId = id;
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
                intent.putExtra("fbId", mId);
                v.getContext().startActivity(intent);
            } else {
                listListener.get().onClickListElement(mId);
            }
            return false;
        }
    }

    public LinearTextAdapter(String id) {
        mFBId = id;
        if (mFBId == null){
            mDataset = FBCaller.getPlayerGames();
        } else {
            mDataset = FBCaller.getSubEpochs(mFBId);
        }
        this.listener = null;
    }

    public void setListener(onListListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public LinearTextAdapter.TextViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                     int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.text_layout, parent, false);
        return new TextViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull TextViewHolder holder, int position) {
        String childFBId = mDataset.get(position);
        holder.setId(childFBId);
        TimeLapse childTimeLapse = FBCaller.getGame(mFBId);
        if(childTimeLapse.isLight()){
            holder.mResume.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.ic_light,0,0, 0);
        }
        else {
            holder.mResume.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.ic_dark,0,0, 0);
        }
        holder.mResume.setText(childTimeLapse.getResume());
        holder.mLongText.setText(childTimeLapse.getBody());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}