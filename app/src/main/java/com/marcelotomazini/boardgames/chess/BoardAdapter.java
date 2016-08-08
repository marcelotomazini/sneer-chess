package com.marcelotomazini.boardgames.chess;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;

public class BoardAdapter implements ListAdapter {

    protected final List<BlockLayout> itemList = new ArrayList<>();

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(final int index) {
        return itemList.get(index);
    }

    @Override
    public long getItemId(final int index) {
        return index;
    }

    @Override
    public int getItemViewType(final int arg0) {
        return 0;
    }

    @Override
    public BlockLayout getView(final int index, final View arg1, final ViewGroup arg2) {
        return itemList.get(index);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return itemList.isEmpty();
    }

    @Override
    public void registerDataSetObserver(final DataSetObserver arg0) {
    }

    @Override
    public void unregisterDataSetObserver(final DataSetObserver arg0) {
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(final int arg0) {
        return true;
    }

    public void add(BlockLayout blockLayout) {
        itemList.add(blockLayout);
    }
}
