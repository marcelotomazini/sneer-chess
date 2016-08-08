package com.marcelotomazini.boardgames.chess;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
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

    public interface OnMoveListener {
        void onMove(String move);
    }

    private class BlockTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(selected == null) {
                select(v);
                highlightPossibleMoves(chessGame.getPossibleMoves(selected.getPosition()));
                return false;
            }

            if(selected.equals(v)) {
                deselect();
                return false;
            }

            if(selected != null) {
                Position to = ((BlockLayout)v).getPosition();
                StringBuilder move = new StringBuilder()
                        .append(selected.getPosition().col())
                        .append(selected.getPosition().row())
                        .append(to.col())
                        .append(to.row());
                onMoveListener.onMove(move.toString());
//                move(((BlockLayout)v).getPosition());
                deselect();
                refresh();
            }

            return false;
        }
    }

    private BlockLayout selected;
    private ChessGame chessGame;
    private OnMoveListener onMoveListener;

    public BoardLayout(Context context, boolean wasStartedByMe) {
        super(context);
        this.chessGame = new ChessGame(wasStartedByMe ? PieceColor.WHITE : PieceColor.BLACK);
        setAdapter(new BoardAdapter());
        setBackgroundColor(android.graphics.Color.WHITE);
        setNumColumns(8);
        drawBoard();
        refresh();
        onMoveListener = (OnMoveListener) context;
    }

    @Override
    public BoardAdapter getAdapter() {
        return (BoardAdapter) super.getAdapter();
    }

    public void move(String move) {
        Position from = new Position(move.charAt(0), move.charAt(1));
        Position to = new Position(move.charAt(2), move.charAt(3));

        try {
            chessGame.move(from, to);
        } catch (PromotionException e) {
            showPromotePieceDialog();
        } catch (RuntimeException e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void refresh() {
        for(int i = 0; i < getAdapter().getCount(); i++) {
            BlockLayout view = getAdapter().getView(i, null, null);
            view.removeAllViews();
            PieceView pieceView = createPieceView(chessGame.find(view.getPosition()).piece());
            view.addView(pieceView);
        }
    }

    private void drawBoard() {
        int lastColorUsed = Color.GRAY;
        int count = 1;

        for(int i = 8; i >= 1; i--) {
            for(int j = 'a'; j <= 'h'; j++) {
                Block block = chessGame.find(new Position((char) j, i));
                BlockLayout blockLayout = new BlockLayout(getContext(), lastColorUsed, block.position());
                blockLayout.setOnTouchListener(new BlockTouchListener());
                add(blockLayout);

                if(count % 8 != 0)
                    lastColorUsed = lastColorUsed == Color.GRAY ? Color.LTGRAY : Color.GRAY;
                count++;
            }
        }
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
        } catch (PromotionException e) {
            showPromotePieceDialog();
        } catch (RuntimeException e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void showPromotePieceDialog() {
        FragmentManager fragmentManager = ((Activity) getContext()).getFragmentManager();

        final PromotePieceDialog newFragment = new PromotePieceDialog();
        newFragment.show(fragmentManager, "dialog");
        newFragment.onSelect(new PromotePieceDialog.SelectionListener() {
            @Override
            public void selected(PieceType pieceType) {
                chessGame.promoteTo(pieceType);
                refresh();
            }
        });
    }

    private void deselect() {
        selected = null;
        setOriginalBackgroundColor();
    }
}
