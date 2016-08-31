package com.marcelotomazini.sneerapps.boardgames.chess;

import android.app.FragmentManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.nullpointergames.boardgames.Block;
import com.nullpointergames.boardgames.Piece;
import com.nullpointergames.boardgames.PieceColor;
import com.nullpointergames.boardgames.Position;
import com.nullpointergames.boardgames.chess.ChessGame;
import com.nullpointergames.boardgames.chess.PieceType;
import com.nullpointergames.boardgames.chess.exceptions.PromotionException;

import java.util.List;

import sneer.android.Message;
import sneer.android.PartnerSession;

public class SneerChessActivity extends AppCompatActivity implements BoardLayout.MoveListener {

    private PartnerSession session;
    private BoardLayout boardLayout;
    private ChessGame chessGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = joinSession();

        chessGame = new ChessGame(session.wasStartedByMe() ? PieceColor.WHITE : PieceColor.BLACK);
        setContentView(R.layout.activity_main);
        boardLayout = (BoardLayout)findViewById(R.id.board);
        boardLayout.addMoveListener(this);
        addBlocks();
    }

    protected PartnerSession joinSession() {
        return PartnerSession.join(this, new PartnerSession.Listener() {
            @Override
            public void onMessage(Message message) {
                move((String) message.payload());
            }

            @Override
            public void onUpToDate() {
                refresh();
            }
        });
    }

    private void refresh() {
        for (BlockLayout blockLayout : boardLayout.getBlockLayouts()) {
            for(Block block : chessGame.getBoard())
                if(block.equals(blockLayout.getBlock())) {
                    PieceView pieceView = createPieceView(block.piece());
                    blockLayout.removeAllViews();
                    blockLayout.addView(pieceView);
                    break;
                }
        }

        try {
            chessGame.verifyCheckAndCheckmate();
        } catch (RuntimeException e) {
            showMessage(e);
        }
    }

    private PieceView createPieceView(Piece piece) {
        return new PieceView(this, piece);
    }

    private void showMessage(RuntimeException e) {
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
    }

    private void addBlocks() {
        int lastColorUsed = Color.GRAY;
        for(Block block : chessGame.getBoard()) {
            BlockLayout blockLayout = new BlockLayout(this, lastColorUsed, block);
            boardLayout.add(blockLayout);

            if(!isInTheLastColumn(block))
                lastColorUsed = lastColorUsed == Color.GRAY ? Color.LTGRAY : Color.GRAY;
        }
    }

    private boolean isInTheLastColumn(Block block) {
        return block.position().col() == 'h';
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        session.close();
    }

    @Override
    public void onMove(BlockLayout fromBlock, BlockLayout toBlock) {
        Position from = fromBlock.getBlock().position();
        Position to = toBlock.getBlock().position();

        try {
            chessGame.clone().move(from, to);
            sendMove(from, to);
        } catch (PromotionException e) {
            sendMove(from, to);
            showPromotePieceDialog();
        } catch (RuntimeException e) {
            showMessage(e);
        }
    }

    @Override
    public void onSelect(BlockLayout selected) {
        List<Position> possibleMoves = chessGame.getPossibleMoves(selected.getBlock().position());

        for(Position position : possibleMoves)
            for (BlockLayout blockLayout : boardLayout.getBlockLayouts())
                if(blockLayout.getBlock().position().equals(position))
                    blockLayout.setBackgroundResource(R.color.Highlight);
    }

    @Override
    public void onDeselect() {}

    private void sendMove(Position from, Position to) {
        StringBuilder move = new StringBuilder()
                .append(from.col())
                .append(from.row())
                .append(to.col())
                .append(to.row());

        session.send(move);
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

    private void showPromotePieceDialog() {
        FragmentManager fragmentManager = getFragmentManager();

        final PromotePieceDialog newFragment = new PromotePieceDialog();
        newFragment.show(fragmentManager, "dialog");
        newFragment.onSelect(new PromotePieceDialog.SelectionListener() {
            @Override
            public void selected(PieceType pieceType) {
                chessGame.promoteTo(pieceType);

                StringBuilder move = new StringBuilder()
                        .append('p')
                        .append(pieceType.name());
                move(move.toString());

                refresh();
            }
        });
    }
}
