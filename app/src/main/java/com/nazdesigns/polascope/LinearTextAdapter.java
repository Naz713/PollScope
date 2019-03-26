package com.nazdesigns.polascope;

import android.content.Intent;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.nazdesigns.polascope.GameStructure.TimeLapse;
import com.nazdesigns.polascope.USoT.FBCaller;
import java.lang.ref.WeakReference;
import java.util.List;

/*
Responsable de llenar el recler view dado una lista de TimeLapse
 */

public class LinearTextAdapter extends RecyclerView.Adapter<LinearTextAdapter.TextViewHolder> {
    private List<String> mDataset;
    private String mFBId;
    private onListListener listener;
    private LinearTextAdapter.SwipeHandler mSwipeHandler;

    public interface onListListener{
        void onClickListElement(String id);
    }

    static class SwipeHandler extends ItemTouchHelper.Callback {

        private LinearTextAdapter.TextViewHolder swipedViewHolder;

        SwipeHandler() {}

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder) {
            LinearTextAdapter.TextViewHolder myViewHolder = (LinearTextAdapter.TextViewHolder) viewHolder;
            if (swipedViewHolder != myViewHolder) {
                return makeMovementFlags(0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
            } else {
                return 0;
            }
        }

        @Override public boolean onMove(@NonNull RecyclerView recyclerView,
                                        @NonNull RecyclerView.ViewHolder viewHolder,
                                        @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            undo();
            swipedViewHolder = (LinearTextAdapter.TextViewHolder) viewHolder;
            if (direction == ItemTouchHelper.RIGHT) {
                swipedViewHolder.showNestedTimeLapses();
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                @NonNull RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {
            LinearTextAdapter.TextViewHolder myViewHolder = (LinearTextAdapter.TextViewHolder) viewHolder;
            if (dX < 0) {
                getDefaultUIUtil().onDraw(c, recyclerView, myViewHolder.mResume, (dX*2/5), dY, actionState, isCurrentlyActive);
            } else if (dX > 0) {
                getDefaultUIUtil().onDraw(c, recyclerView, myViewHolder.mResume, (dX * 3 / 5), dY, actionState, isCurrentlyActive);
            }
        }

        @Override public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                              RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState,
                                              boolean isCurrentlyActive) {
            LinearTextAdapter.TextViewHolder myViewHolder = (LinearTextAdapter.TextViewHolder) viewHolder;
            if (dX < 0) {
                getDefaultUIUtil().onDrawOver(c, recyclerView, myViewHolder.mResume, (dX*2/5), dY, actionState, isCurrentlyActive);
            } else if (dX > 0) {
                getDefaultUIUtil().onDrawOver(c, recyclerView, myViewHolder.mResume, (dX * 3 / 5), dY, actionState, isCurrentlyActive);
            }
        }

        @Override public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (viewHolder != null) {
                LinearTextAdapter.TextViewHolder myViewHolder = (LinearTextAdapter.TextViewHolder) viewHolder;
                getDefaultUIUtil().onSelected(myViewHolder.itemView);
            }
        }

        void undo() {
            if (swipedViewHolder != null) {
                getDefaultUIUtil().clearView(swipedViewHolder.mResume);
                swipedViewHolder = null;
            }
        }

    }

    public static class TextViewHolder extends RecyclerView.ViewHolder
                                        implements View.OnClickListener, View.OnLongClickListener {
        public TextView mResume;
        public TextView mLongText;
        public ImageButton mEdit;
        public ImageButton mAdd;
        public ImageButton mUndo;
        public String mId;
        public WeakReference<onListListener> listListener;
        public WeakReference<LinearTextAdapter.SwipeHandler> mSwipeHandler;

        public TextViewHolder(View v, onListListener listener, LinearTextAdapter.SwipeHandler swipeHandler) {
            super(v);
            listListener = new WeakReference<>(listener);
            mSwipeHandler = new WeakReference<>(swipeHandler);
            mResume = v.findViewById(R.id.resume);
            mLongText = v.findViewById(R.id.long_text);
            mEdit = v.findViewById(R.id.button_edit);
            mAdd = v.findViewById(R.id.button_add);
            mUndo = v.findViewById(R.id.button_undo);
            v.setOnClickListener(this);
            v.setOnLongClickListener(this);
            mEdit.setOnClickListener(this);
            mAdd.setOnClickListener(this);
            mUndo.setOnClickListener(this);

        }

        public void setId(String id){
            mId = id;
        }

        @Override
        public void onClick(View v) {
            int viewId = v.getId();

            switch (viewId){
                case R.id.button_edit:
                    startEditActivity(v);
                    return;

                case R.id.button_undo:
                    mSwipeHandler.get().undo();
                    return;

                case R.id.button_add:
                    // TODO: Hacer logica para agragar nuevo time lapse
                    return;
            }
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

    public LinearTextAdapter(String id, onListListener listener, LinearTextAdapter.SwipeHandler swipeHandler) {
        mFBId = id;
        if (mFBId == null){
            mDataset = FBCaller.getPlayerGames();
        } else {
            mDataset = FBCaller.getSubEpochs(mFBId);
        }
        this.listener = listener;
        this.mSwipeHandler = swipeHandler;
    }

    public void detachListener(){
        this.listener = null;
    }

    @NonNull
    @Override
    public LinearTextAdapter.TextViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                     int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.text_layout, parent, false);
        return new TextViewHolder(v, listener, mSwipeHandler);
    }

    @Override
    public void onBindViewHolder(@NonNull TextViewHolder holder, int position) {
        String childFBId = mDataset.get(position);
        holder.setId(childFBId);
        TimeLapse childTimeLapse = FBCaller.getGame(holder.itemView.getContext(), childFBId);
        if(childTimeLapse.isLight()){
            //holder.mResume.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.ic_light,0,0, 0);
            holder.mResume.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.backgroundLight));
            holder.mLongText.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.backgroundLight));
        }
        else {
            //holder.mResume.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.ic_dark,0,0, 0);
            holder.mResume.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.backgroundDark));
            holder.mLongText.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.backgroundDark));

        }
        holder.mResume.setText(childTimeLapse.getResume());
        holder.mLongText.setText(childTimeLapse.getBody());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}