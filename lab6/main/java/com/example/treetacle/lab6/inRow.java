package com.example.treetacle.lab6;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class inRow extends AppCompatActivity {
    public static final String STATUS = "Status";
    public static final String MOVES = "Moves";
    public static final String GAME_ID = "Game_id";
    public static final String PLAYER = "Player";
    public static final int NEW_GAME = 0;
    public static final int YOUR_TURN = 1;
    public static final int WAIT = 2;
    public static final int ERROR = 3;
    public static final int CONNECTION = 4;
    public static final int NETWORK_ERROR = 5;
    public static final int WIN = 6;
    public static final int LOSE = 7;

    private int status;
    private int game_id;
    private String moves;
    private int player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_row);
        status=getIntent().getIntExtra(inRow.STATUS, inRow.NEW_GAME);
        game_id=getIntent().getIntExtra(inRow.GAME_ID, inRow.NEW_GAME);
        player=getIntent().getIntExtra(inRow.PLAYER, 1);
        hints(status);

        GridView gv = (GridView) findViewById(R.id.gridView);
        moves = getIntent().getStringExtra(inRow.MOVES);
        gv.setAdapter(new inRowBoard(this,moves));
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if(status!=inRow.WAIT) {
                    status = inRow.WAIT;
                    hints(inRow.CONNECTION);

                    GridView gv = (GridView) findViewById(R.id.gridView);
                    inRowBoard game = (inRowBoard)gv.getAdapter();
                    if(game.add(arg3)!=null) gv.setAdapter(game);
                    else hints(inRow.ERROR);

                    Intent intencja = new Intent( getApplicationContext(), HttpService.class);
                    PendingIntent pendingResult = createPendingResult(HttpService.IN_ROW, new Intent(),0);

                    if(game_id == inRow.NEW_GAME) {
                        intencja.putExtra(HttpService.URL, HttpService.LINES);
                        intencja.putExtra(HttpService.METHOD, HttpService.POST);
                    } else {
                        intencja.putExtra(HttpService.URL, HttpService.LINES+game_id);
                        intencja.putExtra(HttpService.METHOD, HttpService.PUT);
                    }

                    intencja.putExtra(HttpService.PARAMS, "moves=" + moves + arg3 );
                    intencja.putExtra(HttpService.RETURN, pendingResult);
                    startService(intencja);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==HttpService.IN_ROW) {
            try {
                JSONObject response = new JSONObject(data.getStringExtra(HttpService.RESPONSE));
                if (resultCode == 200) {
                    if(game_id==0) game_id = response.getInt("game_id");

                    GridView gv = (GridView) findViewById(R.id.gridView);
                    inRowBoard game = (inRowBoard)gv.getAdapter();
                    int game_status = game.checkWin();
                    if (game_status==0)   hints(inRow.WAIT);
                    else{
                        if(game_status==player) hints(inRow.WIN);
                        else hints(inRow.LOSE);
                    }

                } else{
                    if(resultCode==500) hints(inRow.NETWORK_ERROR);
                    else hints(inRow.ERROR);
                }
                Thread.sleep(5000);
                refresh(null);

            }catch(Exception ex){
                hints(inRow.ERROR);
                ex.printStackTrace();
            }

        }else if(requestCode==HttpService.REFRESH){
            try {
                JSONObject response = new JSONObject(data.getStringExtra(HttpService.RESPONSE));
                GridView gv = (GridView) findViewById(R.id.gridView);
                moves = response.getString("moves");
                inRowBoard game = new inRowBoard(this,moves);
                gv.setAdapter(game);

                if(response.getInt("status")==player){
                    if(game.checkWin()==player) {
                        hints(inRow.WIN);
                    }else if(game.checkWin()!=0){
                        hints(inRow.LOSE);
                    }else {
                        status = inRow.YOUR_TURN;
                        hints(status);
                    }
                }else{
                    Thread.sleep(5000);
                    refresh(null);
                }

            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }

    private void hints(int status){
        TextView hint = (TextView)findViewById(R.id.inRowHint);
        switch(status){
            case inRow.YOUR_TURN:
                hint.setText(getString(R.string.your_turn));
                break;
            case inRow.WAIT:
                hint.setText(getString(R.string.wait));
                break;
            case inRow.ERROR:
                hint.setText(getString(R.string.error));
                break;
            case inRow.CONNECTION:
                hint.setText(getString(R.string.connection));
                break;
            case inRow.NETWORK_ERROR:
                hint.setText(getString(R.string.network_error));
                break;
            case inRow.WIN:
                hint.setText(getString(R.string.win));
                break;
            case inRow.LOSE:
                hint.setText(getString(R.string.lose));
                break;
            default:
                hint.setText(getString(R.string.new_game));
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_menu, menu);
        return true;
    }

    public void refresh(MenuItem item){
        Intent intencja = new Intent( getApplicationContext(), HttpService.class);
        PendingIntent pendingResult = createPendingResult(HttpService.REFRESH, new Intent(),0);
        intencja.putExtra(HttpService.URL, HttpService.LINES+game_id);
        intencja.putExtra(HttpService.METHOD, HttpService.GET);
        intencja.putExtra(HttpService.RETURN, pendingResult);
        startService(intencja);
    }

}
