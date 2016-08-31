package com.marcelotomazini.sneerapps.boardgames.chess;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

public class BoardLayout extends GridView {

    private final List<MoveListener> moveListeners = new ArrayList<>();

    private BlockLayout selected;

    public BoardLayout(Context context) {
        super(context);
        setAdapter();
    }

    public BoardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAdapter();
    }

    public List<BlockLayout> getBlockLayouts() {
        return getAdapter().getBlockLayouts();
    }

    public interface MoveListener {
        void onMove(BlockLayout from, BlockLayout to);
        void onSelect(BlockLayout selected);
        void onDeselect();
    }

    public void addMoveListener(MoveListener moveListener) {
        moveListeners.add(moveListener);
    }

    @Override
    public BoardAdapter getAdapter() {
        return (BoardAdapter) super.getAdapter();
    }

    public void add(BlockLayout blockLayout) {
        blockLayout.setOnTouchListener(new BlockTouchListener());
        getAdapter().add(blockLayout);
    }

    private void setOriginalBackgroundColor() {
        for(BlockLayout b : getBlockLayouts())
            b.setOriginalBackgroundColor();
    }

    private void dispatchMoveEvent(BlockLayout v) {
        for(MoveListener listener : moveListeners)
            listener.onMove(selected, v);
    }

    private void dispatchSelectEvent(BlockLayout blockLayout) {
        for(MoveListener listener : moveListeners)
            listener.onSelect(blockLayout);
    }

    private void dispatchDeselectEvent() {
        for(MoveListener listener : moveListeners)
            listener.onDeselect();
    }

    private void setAdapter() {
        setAdapter(new BoardAdapter());
    }

    private class BlockTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            BlockLayout blockLayout = (BlockLayout)v;
            if(selected == null) {
                dispatchSelectEvent(blockLayout);
                select(blockLayout);
                return false;
            }

            if(selected.equals(v)) {
                dispatchDeselectEvent();
                deselect();
                return false;
            }

            if(selected != null) {
                dispatchMoveEvent(blockLayout);
                deselect();
            }

            return false;
        }

        private void select(BlockLayout blockLayout) {
            selected = blockLayout;
            blockLayout.setBackgroundResource(R.color.Highlight);
        }

        private void deselect() {
            selected = null;
            setOriginalBackgroundColor();
        }
    }
}
