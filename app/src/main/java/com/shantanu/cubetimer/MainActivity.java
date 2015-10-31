package com.shantanu.cubetimer;

import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Chronometer;
import android.os.Handler;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.logging.LogRecord;

public class MainActivity extends AppCompatActivity {

    float average;
    int numberofsolves = 0;


    TextView time;
    long starttime = 0;
    long timeinms = 0;
    long timeswap = 0;
    long updatedtime = 0;
    int secs = 0;
    int mins = 0;
    int ms = 0;
    Handler handler = new Handler();
    Runnable updateTimer;
    RelativeLayout layout;
    Boolean timerRunning = false;
    Boolean waitingToStart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        time = (TextView) findViewById(R.id.tvTimer);
        layout = (RelativeLayout) findViewById(R.id.layout);


        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    if (!timerRunning) {
                        starttime  =0;
                        timeinms = 0;
                        ms = 0;
                        secs = 0;
                        mins = 0;
                        updatedtime = 0;
                        timeswap = 0;
                        time.setText("00.00.00");
                        time.setTextColor(Color.RED);
                        handler.removeCallbacks(updateTimer);
                    }else{
                        timeswap += timeinms;
                        time.setTextColor(Color.BLACK);
                        handler.removeCallbacks(updateTimer);
                        timerRunning = false;
                        waitingToStart = false;
                    }
                }

                else if(event.getAction() == MotionEvent.ACTION_UP){
                    if(!timerRunning&&!waitingToStart)
                        time.setTextColor(Color.BLACK);
                    if(waitingToStart){
                        timerRunning = true;
                        starttime = SystemClock.uptimeMillis();
                        handler.post(updateTimer);
                    }
                }

                return false;
            }


        });


        layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                time.setTextColor(Color.GREEN);
                waitingToStart = true;


                return false;
            }
        });





        updateTimer = new Runnable() {
            @Override
            public void run() {
                timeinms = SystemClock.uptimeMillis() - starttime;

                updatedtime = timeswap + timeinms;
                secs = (int) (updatedtime / 1000);
                mins = secs / 60;
                secs = secs % 60;
                ms = (int) (updatedtime % 1000)/10;
                time.setText(String.format("%02d.%02d.%02d", mins, secs, ms));

                handler.postDelayed(this, 0);
            }
        };


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
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }
}
