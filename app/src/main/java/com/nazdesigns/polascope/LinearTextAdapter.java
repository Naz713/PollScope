package com.nazdesigns.polascope;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.nazdesigns.polascope.GameStructure.TimeLapse;
import com.nazdesigns.polascope.USoT.FBCaller;
import com.nazdesigns.polascope.Utilities.Common;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/*
Responsable de llenar el recler view dado una lista de TimeLapse
 */

public class LinearTextAdapter extends RecyclerView.Adapter<LinearTextAdapter.TextViewHolder> {
    private List<TimeLapse> mDataset;
    private List<String> mIds;
    private String mFBId;
    private onListListener listener;
    private LinearTextAdapter.SwipeHandler mSwipeHandler;

    public interface onListListener{
        void onClickListElement(String id);
    }

    public static class SwipeHandler extends ItemTouchHelper.Callback {

        private LinearTextAdapter.TextViewHolder swipedViewHolder;

        SwipeHandler() {}

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder) {
            return makeMovementFlags(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        }

        @Override public boolean onMove(@NonNull RecyclerView recyclerView,
                                        @NonNull RecyclerView.ViewHolder viewHolder,
                                        @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            //undo(swipedViewHolder);
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
            myViewHolder.mLongText.setVisibility(View.GONE);
            if (dX < 0) {
                getDefaultUIUtil().onDraw(c, recyclerView, myViewHolder.mForeground, (dX/3), dY, actionState, isCurrentlyActive);
            } else if (dX > 0) {
                getDefaultUIUtil().onDraw(c, recyclerView, myViewHolder.mForeground, (dX*2/3), dY, actionState, isCurrentlyActive);
            }
        }

        @Override public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                              RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState,
                                              boolean isCurrentlyActive) {
            LinearTextAdapter.TextViewHolder myViewHolder = (LinearTextAdapter.TextViewHolder) viewHolder;
            myViewHolder.mLongText.setVisibility(View.GONE);
            if (dX < 0) {
                getDefaultUIUtil().onDrawOver(c, recyclerView, myViewHolder.mForeground, (dX/3), dY, actionState, isCurrentlyActive);
            } else if (dX > 0) {
                getDefaultUIUtil().onDrawOver(c, recyclerView, myViewHolder.mForeground, (dX*2/3), dY, actionState, isCurrentlyActive);
            }
        }

        @Override public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (viewHolder != null) {
                LinearTextAdapter.TextViewHolder myViewHolder = (LinearTextAdapter.TextViewHolder) viewHolder;
                getDefaultUIUtil().onSelected(myViewHolder.itemView);
            }
        }

        void undo(@Nullable TextViewHolder textViewHolder) {
            if (textViewHolder != null) {
                getDefaultUIUtil().clearView(textViewHolder.mForeground);
            }
        }

    }

    public static class TextViewHolder extends RecyclerView.ViewHolder
                                        implements View.OnClickListener, View.OnLongClickListener {
        public LinearLayout mForeground;
        public TextView mResume;
        public TextView mLongText;
        public ImageButton mAdd_up;
        public ImageButton mAdd_down;
        public String mId;
        public TimeLapse mTL;
        public WeakReference<onListListener> listListener;
        public WeakReference<LinearTextAdapter.SwipeHandler> mSwipeHandler;

        public static String TAG = "TextViewHolder";

        public TextViewHolder(View v, onListListener listener,
                              LinearTextAdapter.SwipeHandler swipeHandler) {
            super(v);
            listListener = new WeakReference<>(listener);
            mSwipeHandler = new WeakReference<>(swipeHandler);

            mForeground = v.findViewById(R.id.foreground);
            mResume = mForeground.findViewById(R.id.resume);
            mLongText = mForeground.findViewById(R.id.long_text);

            mAdd_up = v.findViewById(R.id.button_add_up);
            mAdd_down = v.findViewById(R.id.button_add_down);

            mResume.setOnClickListener(this);
            mLongText.setOnClickListener(this);
            mLongText.setOnLongClickListener(this);
            mAdd_up.setOnClickListener(this);
            mAdd_down.setOnClickListener(this);

        }

        public void setTLs(TimeLapse tl, String id){
            mTL = tl;
            mId = id;
        }

        @Override
        public void onClick(View v) {
            int viewId = v.getId();

            switch (viewId) {
                case R.id.button_add_up:
                    Log.i(TAG,"Boton Add Up presionado");
                    if (mTL.getTimeType() == TimeLapse.GAME_TYPE){
                        Common.startCreateGameActivity(v.getContext(), mTL.getTimeType());
                    } else {
                        Common.startCreateActivity(v.getContext(), 1, mTL.getRaiz(), mId, 1);
                    }
                    break;

                case R.id.button_add_down:
                    Log.i(TAG,"Boton Add Down presionado");
                    if (mTL.getTimeType() == TimeLapse.GAME_TYPE){
                        Common.startCreateGameActivity(v.getContext(), mTL.getTimeType());
                    } else {
                        Common.startCreateActivity(v.getContext(),1, mTL.getRaiz(), mId, -1);
                    }
                    break;

                case R.id.long_text:
                case R.id.resume:
                    /*
                     * (un)Display long text View
                     */
                    Log.i(TAG,"LongText Visibility change");
                    //View longText = v.findViewById(R.id.long_text);
                    View longText = mLongText;
                    if (longText.getVisibility() == View.GONE) {
                        longText.setVisibility(View.VISIBLE);
                        mSwipeHandler.get().undo(this);
                    } else {
                        longText.setVisibility(View.GONE);
                    }
                    break;
            }
        }

        @Override
        public boolean onLongClick(View v) {
            int long_visible = v.getVisibility();
            if (long_visible == View.VISIBLE) {
                Common.startEditActivity(v.getContext(), mId, mTL.getTimeType());
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
    }

    public LinearTextAdapter(String id, onListListener listener,
                             LinearTextAdapter.SwipeHandler swipeHandler) {
        mFBId = id;
        mDataset = new ArrayList<>();
        mIds = new ArrayList<>();
        this.listener = listener;
        this.mSwipeHandler = swipeHandler;
        refreshList(new RecyclerFragment.RefreshCallback() {
            @Override
            public void completed() {}
        });
    }

    public void refreshList(final RecyclerFragment.RefreshCallback callback){
        final LinearTextAdapter textAdapter = this;
        if (mFBId != null) {
            FBCaller.getTLlist(mFBId, new FBCaller.onListTLCallback() {
                @Override
                public void onListTimeLapseResult(List<TimeLapse> result, List<String> ids) {
                    if (result != null) {
                        mDataset = result;
                        mIds = ids;
                        textAdapter.notifyDataSetChanged();
                        callback.completed();
                    }
                }
            });
        } else {
            FBCaller.getPlayerGames(new FBCaller.onListTLCallback() {
                @Override
                public void onListTimeLapseResult(List<TimeLapse> result, List<String> ids) {
                    if (result != null) {
                        mDataset = result;
                        mIds = ids;
                        textAdapter.notifyDataSetChanged();
                        callback.completed();
                    }
                }
            });
        }
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
    public void onBindViewHolder(@NonNull final TextViewHolder holder, int position) {
        TimeLapse childTimeLapse = mDataset.get(position);
        String childFBId = mIds.get(position);

        holder.setTLs(childTimeLapse, childFBId);

        if(childTimeLapse.getIsLight()){
            holder.mResume.setBackgroundColor(holder.itemView.getContext().getResources()
                    .getColor(R.color.backgroundLight));
            holder.mLongText.setBackgroundColor(holder.itemView.getContext().getResources()
                    .getColor(R.color.backgroundLight));
        } else {
            holder.mResume.setBackgroundColor(holder.itemView.getContext().getResources()
                    .getColor(R.color.backgroundDark));
            holder.mLongText.setBackgroundColor(holder.itemView.getContext().getResources()
                    .getColor(R.color.backgroundDark));

        }

        if  (childTimeLapse.getSubEpochsIds() == null ||
                childTimeLapse.getSubEpochsIds().isEmpty()) {
            holder.mResume.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,
                    R.mipmap.empty, 0);
        }

        holder.mResume.setText(childTimeLapse.getResume());
        holder.mLongText.setText(childTimeLapse.getBody());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}