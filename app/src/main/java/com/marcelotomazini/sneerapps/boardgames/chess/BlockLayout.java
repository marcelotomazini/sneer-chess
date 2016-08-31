package com.marcelotomazini.sneerapps.boardgames.chess;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.TableRow;

import com.nullpointergames.boardgames.Block;

public class BlockLayout extends FrameLayout {

    private final Block block;
    private final int originalBackgroundColor;

    public BlockLayout(final Context context, final int originalBackgroundColor, final Block block) {
        super(context);
        this.block = block;
        this.originalBackgroundColor = originalBackgroundColor;
        setOriginalBackgroundColor();
        setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, 100, 1));
    }

    public void setOriginalBackgroundColor() {
        setBackgroundColor(originalBackgroundColor);
    }

    public Block getBlock() {
        return block;
    }
}

