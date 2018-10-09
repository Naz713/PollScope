package com.nazdesigns.polascope;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

public class LinearTextAdapter extends RecyclerView.Adapter<LinearTextAdapter.TextViewHolder> {
    private List<String> mDataset;

    public static class TextViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public TextViewHolder(View v) {
            super(v);
            mTextView = v.findViewById(R.id.text_view);
        }
    }

    public LinearTextAdapter(List<String> myDataset) {
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
    public void onBindViewHolder(TextViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(mDataset.get(position));

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}