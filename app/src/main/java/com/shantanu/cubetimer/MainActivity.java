package com.shantanu.cubetimer;

import android.content.res.Configuration;
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;

import java.util.logging.LogRecord;

public class MainActivity extends AppCompatActivity {

    double average = 0;
    int numberofsolves = 0;

    ProgressBar progress;
    ProgressBar redProgress;

    TextView avg,time;
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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        avg = (TextView) findViewById(R.id.tvAvg);
        time = (TextView) findViewById(R.id.tvTimer);
        layout = (RelativeLayout) findViewById(R.id.layout);

        progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setMax(1000);
        redProgress = (ProgressBar) findViewById(R.id.redProgress);
        redProgress.setMax(1000);
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!timerRunning) {
                        starttime = 0;
                        timeinms = 0;
                        ms = 0;
                        secs = 0;
                        mins = 0;
                        updatedtime = 0;
                        timeswap = 0;
                        time.setText("00.00");
                        time.setTextColor(Color.RED);
                        handler.removeCallbacks(updateTimer);


                    } else {
                        numberofsolves++;
                        timeswap += timeinms;
                        average = (average * (numberofsolves - 1) + timeswap) / numberofsolves;

                        secs = (int) (average / 1000);
                        mins = secs / 60;
                        secs = secs % 60;
                        ms = (int) (average % 1000) / 10;
                        avg.setText(mins > 0 ? (String.format("Average : %d.%02d.%02d", mins, secs, ms)) : (String.format("Average : " + (secs > 9 ? "%02d." : "%01d.") + "%02d", secs, ms)));
                        time.setTextColor(Color.BLACK);
                        handler.removeCallbacks(updateTimer);
                        timerRunning = false;
                        waitingToStart = false;


                        return true;
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (!timerRunning && !waitingToStart)
                        time.setTextColor(Color.BLACK);
                    if (waitingToStart) {
                        waitingToStart = false;
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
                if(!timerRunning) {
                    time.setTextColor(Color.GREEN);
                    waitingToStart = true;
                }


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
                time.setText(mins > 0 ? (String.format("%d.%02d.%02d", mins, secs, ms)) : (String.format((secs > 9 ? "%02d." : "%01d.") + "%02d", secs, ms)));

                if(average != 0){
                    progress.setProgress((int)(((float)updatedtime/(float)average)*1000));
                }

                if(updatedtime>average && average>0){
                    redProgress.setProgress((int)((1.0F-((float)average/(float)updatedtime))*1000));
                }else
                    redProgress.setProgress(0);


                handler.postDelayed(this, 0);
            }
        };


    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }*/

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