package com.nazdesigns.polascope;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

class SwipeHandler extends ItemTouchHelper.Callback {

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
}