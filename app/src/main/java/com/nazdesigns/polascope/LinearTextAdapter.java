package com.nazdesigns.polascope;

import android.content.Intent;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.LinearLayout;
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

    public static class TextViewHolder extends RecyclerView.ViewHolder
                                        implements View.OnClickListener, View.OnLongClickListener {
        public LinearLayout foregroundView;
        public TextView mResume;
        public TextView mLongText;
        public String mId;
        public WeakReference<onListListener> listListener;

        public TextViewHolder(View v, onListListener listener) {
            super(v);
            listListener = new WeakReference<>(listener);
            foregroundView = v.findViewById(R.id.foreground);
            mResume = foregroundView.findViewById(R.id.resume);
            mLongText = foregroundView.findViewById(R.id.long_text);
            v.setOnClickListener(this);
            v.setOnLongClickListener(this);
        }

        public void setId(String id){
            mId = id;
        }

        @Override
        public void onClick(View v) {
            //TODO: hacer algo para deshacer el swipe si es necesario
            /*
            * (un)Display long text View
            */
            View longText = v.findViewById(R.id.long_text);
            if (longText.getVisibility() == View.GONE) {
                longText.setVisibility(View.VISIBLE);
            } else {
                longText.setVisibility(View.GONE);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            int long_visible = v.findViewById(R.id.long_text).getVisibility();
            if (long_visible == View.VISIBLE) {
                startEditActivity(v);
                return true;
            }
            return false;
        }

        public void showNestedTimeLapses(){
            /*
             * Change Fragment to show nested TimeLapses
             */
            listListener.get().onClickListElement(mId);
        }
        public void startEditActivity(View v){
            /*
             * Start Edit Activity
             */
            Intent intent = new Intent(v.getContext(), EditActivity.class);
            intent.putExtra("fbId", mId);
            v.getContext().startActivity(intent);
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

    public void detacchListener(){
        this.listener = null;
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
        TimeLapse childTimeLapse = FBCaller.getGame(holder.itemView.getContext(), childFBId);
        if(childTimeLapse.isLight()){
            //holder.mResume.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.ic_light,0,0, 0);
            holder.foregroundView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.backgroundLight));
        }
        else {
            //holder.mResume.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.ic_dark,0,0, 0);
            holder.foregroundView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.backgroundDark));

        }
        holder.mResume.setText(childTimeLapse.getResume());
        holder.mLongText.setText(childTimeLapse.getBody());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}