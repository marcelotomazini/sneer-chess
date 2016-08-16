package com.marcelotomazini.boardgames.chess;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import sneer.android.Message;
import sneer.android.PartnerSession;

public class SneerChessActivity extends AppCompatActivity implements BoardLayout.OnMoveListener {

    private PartnerSession session;
    private BoardLayout boardLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = PartnerSession.join(this, new PartnerSession.Listener() {
            @Override
            public void onMessage(Message message) {
                boardLayout.move((String) message.payload());
            }

            @Override
            public void onUpToDate() {
                boardLayout.refresh();
            }
        });

        renderBoard();
    }

    private void renderBoard() {
        boardLayout = new BoardLayout(this, session.wasStartedByMe());
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.addView(boardLayout);
        setContentView(linearLayout);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        session.close();
    }

    @Override
    public void onMove(String move) {
        session.send(move);
    }
}
