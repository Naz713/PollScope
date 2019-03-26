package com.nazdesigns.polascope;

import android.content.Intent;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
                getDefaultUIUtil().onDraw(c, recyclerView, myViewHolder.mForeground, (dX/2), dY, actionState, isCurrentlyActive);
            } else if (dX > 0) {
                getDefaultUIUtil().onDraw(c, recyclerView, myViewHolder.mForeground, (dX/2), dY, actionState, isCurrentlyActive);
            }
        }

        @Override public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                              RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState,
                                              boolean isCurrentlyActive) {
            LinearTextAdapter.TextViewHolder myViewHolder = (LinearTextAdapter.TextViewHolder) viewHolder;
            myViewHolder.mLongText.setVisibility(View.GONE);
            if (dX < 0) {
                getDefaultUIUtil().onDrawOver(c, recyclerView, myViewHolder.mForeground, (dX/2), dY, actionState, isCurrentlyActive);
            } else if (dX > 0) {
                getDefaultUIUtil().onDrawOver(c, recyclerView, myViewHolder.mForeground, (dX/2), dY, actionState, isCurrentlyActive);
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
        public ImageButton mEdit;
        public ImageButton mAdd;
        public ImageButton mUndo;
        public String mId;
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

            mEdit = v.findViewById(R.id.button_edit);
            mAdd = v.findViewById(R.id.button_add);
            mUndo = v.findViewById(R.id.button_undo);

            mResume.setOnClickListener(this);
            mLongText.setOnClickListener(this);
            mForeground.setOnLongClickListener(this);
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

            switch (viewId) {
                case R.id.button_edit:
                    Log.i(TAG,"Boton Edit presionado");
                    startEditActivity(v);
                    break;

                case R.id.button_undo:
                    Log.i(TAG,"Boton Undo presionado");
                    mSwipeHandler.get().undo(this);
                    break;

                case R.id.button_add:
                    Log.i(TAG,"Boton Add presionado");
                    // TODO: Hacer logica para agragar nuevo time lapse
                    break;

                case R.id.long_text:
                case R.id.resume:
                    /*
                     * (un)Display long text View
                     */
                    Log.i(TAG,"Boton Resume presionado");
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