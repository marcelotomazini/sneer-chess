package com.marcelotomazini.sneerapps.boardgames.chess;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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

        setContentView(R.layout.activity_main);
        boardLayout = (BoardLayout)findViewById(R.id.board);
        boardLayout.init(this, session.wasStartedByMe());
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
