package com.marcelotomazini.sneerapps.boardgames.chess;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.TableRow;

import com.nullpointergames.boardgames.Position;

public class BlockLayout extends FrameLayout {

    private final Position position;
    private final int originalBackgroundColor;

    public BlockLayout(final Context context, final int originalBackgroundColor, final Position position) {
        super(context);
        this.position = position;
        this.originalBackgroundColor = originalBackgroundColor;
        setOriginalBackgroundColor();
        setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, 100, 1));
    }

    public void setOriginalBackgroundColor() {
        setBackgroundColor(originalBackgroundColor);
    }

    public Position getPosition() {
        return position;
    }
}

