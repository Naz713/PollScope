package com.nazdesigns.polascope;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;

/*
Responsable de llenar el recler view dado una lista de TimeLapse
 */

public class LinearTextAdapter extends RecyclerView.Adapter<LinearTextAdapter.TextViewHolder> {
    private List<TimeLapse> mDataset;

    public static class TextViewHolder extends RecyclerView.ViewHolder {
        public TextView mResume;
        public TextView mLongText;
        public TextViewHolder(View v) {
            super(v);
            mResume = v.findViewById(R.id.resume);
            mLongText = v.findViewById(R.id.longText);
        }
    }

    public LinearTextAdapter(List<TimeLapse> myDataset) {
        mDataset = myDataset;
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
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if(mDataset.get(position).isLight){
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