package com.marcelotomazini.boardgames.chess;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;

import sneer.android.Message;
import sneer.android.PartnerSession;

public class SneerChessActivity extends Activity implements BoardLayout.OnMoveListener {

    private PartnerSession session;
    private BoardLayout boardLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = PartnerSession.join(this, new PartnerSession.Listener() {
            @Override
            public void onMessage(Message message) {
                //Called for every message sent by you and by your partner.
                boardLayout.move((String) message.payload());
            }

            @Override
            public void onUpToDate() {
                //Called when there are no more messages pending in the session.
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
