package com.example.treetacle.lab3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Kliknięto przycisk FAB", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        Bundle extras = data.getExtras();
        Bitmap imageBitmap = (Bitmap) extras.get("data");
        ConstraintLayout lay = (ConstraintLayout) findViewById(R.id.cont);
        lay.setBackground(new BitmapDrawable(getResources(), imageBitmap));
    }

    public void kliknij(View view){
        Toast.makeText(getApplicationContext(), "Kliknięto przycisk BUTTON", Toast.LENGTH_SHORT).show();
        Intent intencja = new Intent(this, LoginActivity.class);
        startActivity(intencja);
    }

    public void losuj(View view){
        TextView viewById = (TextView) findViewById(view.getId());
        int [] imgtab = {
                android.R.drawable.ic_delete,
                android.R.drawable.ic_media_previous ,
                android.R.drawable.ic_media_pause,
                android.R.drawable.ic_media_next,
                R.drawable.icon1,
                R.drawable.icon2,
        };
        Random rand = new Random();
        int r = rand.nextInt(imgtab.length);
        viewById.setCompoundDrawablesWithIntrinsicBounds(0,0,0,imgtab[r]);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                Toast.makeText(getApplicationContext(), "settings", Toast.LENGTH_SHORT).show();
                Intent intencja = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intencja, REQUEST_IMAGE_CAPTURE);
                break;
            case R.id.action_item:
                Toast.makeText(getApplicationContext(), "item", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_search:
                Toast.makeText(getApplicationContext(), "search", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_switch:
                Toast.makeText(getApplicationContext(), "switch", Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
