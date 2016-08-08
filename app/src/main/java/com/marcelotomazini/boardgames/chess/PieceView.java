package com.marcelotomazini.boardgames.chess;

import android.content.Context;
import android.widget.ImageView;

import com.nullpointergames.boardgames.Piece;

public class PieceView extends ImageView {

    public PieceView(final Context context, final Piece piece) {
        super(context);

        String imageName = "white_0";
        if(piece != null)
            imageName = piece.color().name().toLowerCase() + "_" + String.valueOf(piece.type().name().charAt(2)).toLowerCase();

        final int identifier = getResources().getIdentifier(imageName, "drawable", context.getPackageName());
        setImageResource(identifier);
    }
}
