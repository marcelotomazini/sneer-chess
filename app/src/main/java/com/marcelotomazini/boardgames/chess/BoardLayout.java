package com.marcelotomazini.boardgames.chess;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import com.nullpointergames.boardgames.Block;
import com.nullpointergames.boardgames.Piece;
import com.nullpointergames.boardgames.PieceColor;
import com.nullpointergames.boardgames.Position;
import com.nullpointergames.boardgames.chess.ChessGame;
import com.nullpointergames.boardgames.chess.PieceType;
import com.nullpointergames.boardgames.chess.exceptions.PromotionException;

import java.util.List;

public class BoardLayout extends GridView {

    public BoardLayout(Context context) {
        super(context);
    }

    public BoardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public interface OnMoveListener {
        void onMove(String move);
    }

    private class BlockTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(selected == null) {
                select(v);
                highlightPossibleMoves(chessGame.getPossibleMoves(selected.getPosition()));

                if (chessGame.find(selected.getPosition()).piece().color() != myColor)
                    deselect();

                return false;
            }

            if(selected.equals(v)) {
                deselect();
                return false;
            }

            if(selected != null) {
                if(chessGame.find(selected.getPosition()).piece().color() == myColor)
                    move(((BlockLayout)v).getPosition());
                deselect();
                refresh();
            }

            return false;
        }
    }

    private BlockLayout selected;
    private ChessGame chessGame;
    private PieceColor myColor;
    private OnMoveListener onMoveListener;

    public void init(Context context, boolean wasStartedByMe) {
        this.myColor = wasStartedByMe ? PieceColor.WHITE : PieceColor.BLACK;
        this.chessGame = new ChessGame(myColor);
        setAdapter(new BoardAdapter());
        setBackgroundColor(android.graphics.Color.WHITE);
        drawBoard();
        refresh();
        onMoveListener = (OnMoveListener) context;
    }

    @Override
    public BoardAdapter getAdapter() {
        return (BoardAdapter) super.getAdapter();
    }

    public void move(String move) {
        if(move.charAt(0) == 'p')
            chessGame.promoteTo(PieceType.valueOf(move.substring(1)));
        else
            try {
                Position from = new Position(move.charAt(0), Integer.valueOf(move.substring(1, 2)));
                Position to = new Position(move.charAt(2), Integer.valueOf(move.substring(3, 4)));
                chessGame.moveWithoutVerification(from, to);
            } catch (Exception e) {}


    }

    public void refresh() {
        for(int i = 0; i < getAdapter().getCount(); i++) {
            BlockLayout view = getAdapter().getView(i, null, null);
            view.removeAllViews();
            PieceView pieceView = createPieceView(chessGame.find(view.getPosition()).piece());
            view.addView(pieceView);
        }

        try {
            chessGame.verifyGame();
        } catch (RuntimeException e) {
            showMessage(e);
        }
    }

    private void drawBoard() {
        int lastColorUsed = Color.GRAY;

        if(myColor == PieceColor.WHITE)
            for(int i = 8; i >= 1; i--)
                for(int j = 'a'; j <= 'h'; j++) {
                    newBlockLayout(lastColorUsed, j, i);

                    if(j != 'h')
                        lastColorUsed = lastColorUsed == Color.GRAY ? Color.LTGRAY : Color.GRAY;
                }
        else
            for(int i = 1; i <= 8; i++)
                for(int j = 'a'; j <= 'h'; j++) {
                    newBlockLayout(lastColorUsed, j, i);

                    if(j != 'h')
                        lastColorUsed = lastColorUsed == Color.GRAY ? Color.LTGRAY : Color.GRAY;
                }
    }

    private void newBlockLayout(int color, int col, int row) {
        Block block = chessGame.find(new Position((char) col, row));
        BlockLayout blockLayout = new BlockLayout(getContext(), color, block.position());
        blockLayout.setOnTouchListener(new BlockTouchListener());
        add(blockLayout);
    }

    private PieceView createPieceView(Piece piece) {
        return new PieceView(getContext(), piece);
    }

    private void add(BlockLayout blockLayout) {
        getAdapter().add(blockLayout);
    }

    private BlockLayout getBlock(Position position) {
        for(BlockLayout b : getAdapter().itemList)
            if(b.getPosition().equals(position))
                return b;

        throw new RuntimeException();
    }

    private void setOriginalBackgroundColor() {
        for(BlockLayout b : getAdapter().itemList)
            b.setOriginalBackgroundColor();
    }

    public void select(View block) {
        selected = (BlockLayout)block;
    }

    public void highlightPossibleMoves(List<Position> possibleMoves) {
        setOriginalBackgroundColor();
        for(Position position : possibleMoves) {
            BlockLayout blockLayout = getBlock(position);
            blockLayout.setBackgroundColor(Color.CYAN);
        }
    }

    private void move(Position to) {
        try {
            chessGame.move(selected.getPosition(), to);
            sendMove(to);
        } catch (PromotionException e) {
            sendMove(to);
            showPromotePieceDialog();
        } catch (RuntimeException e) {
            showMessage(e);
        }
    }

    private void sendMove(Position to) {
        StringBuilder move = new StringBuilder()
                .append(selected.getPosition().col())
                .append(selected.getPosition().row())
                .append(to.col())
                .append(to.row());
        onMoveListener.onMove(move.toString());
    }

    private void showMessage(RuntimeException e) {
        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
    }

    private void showPromotePieceDialog() {
        FragmentManager fragmentManager = ((Activity) getContext()).getFragmentManager();

        final PromotePieceDialog newFragment = new PromotePieceDialog();
        newFragment.show(fragmentManager, "dialog");
        newFragment.onSelect(new PromotePieceDialog.SelectionListener() {
            @Override
            public void selected(PieceType pieceType) {
                chessGame.promoteTo(pieceType);

                StringBuilder move = new StringBuilder()
                        .append('p')
                        .append(pieceType.name());
                onMoveListener.onMove(move.toString());

                refresh();
            }
        });
    }

    private void deselect() {
        selected = null;
        setOriginalBackgroundColor();
    }
}
