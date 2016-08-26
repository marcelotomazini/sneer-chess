package com.marcelotomazini.sneerapps.boardgames.chess;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nullpointergames.boardgames.chess.PieceType;

public class PromotePieceDialog extends DialogFragment {

    private SelectionListener listener;

    public void onSelect(SelectionListener listener) {
        this.listener = listener;
    }

    interface SelectionListener {
        void selected(PieceType view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_piece_to_promote, container);
        setCancelable(false);

        View queen = view.findViewById(R.id.queen);
        queen.setOnClickListener(promoteTo(PieceType.QUEEN));

        View bishop = view.findViewById(R.id.bishop);
        bishop.setOnClickListener(promoteTo(PieceType.BISHOP));

        View knight = view.findViewById(R.id.knight);
        knight.setOnClickListener(promoteTo(PieceType.KNIGHT));

        View rook = view.findViewById(R.id.rook);
        rook.setOnClickListener(promoteTo(PieceType.ROOK));

        return view;
    }

    private View.OnClickListener promoteTo(final PieceType pieceType) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.selected(pieceType);
                dismiss();
            }
        };
    }
}
