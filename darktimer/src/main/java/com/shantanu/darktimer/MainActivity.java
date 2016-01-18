package com.shantanu.darktimer;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.DismissOverlayView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends WearableActivity {

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    TextView time,mClockView;
    RelativeLayout layout;

    long starttime = 0;
    long timeinms = 0;
    long timeswap = 0;
    long updatedtime = 0;
    int secs = 0;
    int mins = 0;
    int ms = 0;
    Runnable updateTimer;
    Boolean timerRunning = false;
    Boolean waitingToStart = false;
    Handler handler = new Handler();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        time = (TextView) findViewById(R.id.tvTimer);
        layout = (RelativeLayout) findViewById(R.id.layout);
        mClockView = (TextView) findViewById(R.id.clock);

        setAmbientEnabled();


        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    if (!timerRunning) {
                        starttime = 0;
                        timeinms = 0;
                        updatedtime = 0;
                        timeswap = 0;
                        time.setText(timeToString(0));
                        time.setTextColor(Color.RED);
                        handler.removeCallbacks(updateTimer);


                        time.setTextColor(Color.GREEN);
                        waitingToStart = true;
                        return  true;


                    } else {

                        timeswap += timeinms;


                        time.setTextColor(Color.WHITE);
                        handler.removeCallbacks(updateTimer);

                        timerRunning = false;
                        waitingToStart = false;


                        return true;
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {

                    if (!timerRunning && !waitingToStart)
                        time.setTextColor(Color.WHITE);
                    if (waitingToStart) {
                        waitingToStart = false;
                        timerRunning = true;
                        starttime = SystemClock.uptimeMillis();
                        handler.post(updateTimer);
                        time.setTextColor(Color.WHITE);
                        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                        long[] vibrationPattern = {300};
                        vibrator.vibrate(vibrationPattern, -1);

                    }
                }

                return false;
            }


        });


        updateTimer = new Runnable() {
            @Override
            public void run() {
                timeinms = SystemClock.uptimeMillis() - starttime;

                updatedtime = timeswap + timeinms;
                time.setText(timeToString(updatedtime));

                handler.postDelayed(this, 0);
            }
        };
    }
    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        mClockView.setTextColor(Color.WHITE);
        time.setTextColor(Color.WHITE);
        if(timerRunning)
            time.setText("Timer running");
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        mClockView.setTextColor(Color.BLACK);

        if(timerRunning){
            timeswap += timeinms;

            time.setText(timeToString(updatedtime));
            handler.removeCallbacks(updateTimer);


            timerRunning = false;
            waitingToStart = false;
        }
        super.onExitAmbient();
    }

    private void updateDisplay() {
        if (isAmbient()) {
            //mContainerView.setBackgroundColor(getResources().getColor(android.R.color.black));

            mClockView.setVisibility(View.VISIBLE);

            mClockView.setText(AMBIENT_DATE_FORMAT.format(new Date()));
        } else {

            mClockView.setVisibility(View.GONE);
        }
    }

    String timeToString(long time){
        secs = (int) (time / 1000);
        mins = secs / 60;
        secs = secs % 60;
        ms = (int) (time % 1000) / 10;
        return(mins > 0 ? (String.format("%d.%02d.%02d", mins, secs, ms)) : (String.format((secs > 9 ? "%02d." : "%01d.") + "%02d", secs, ms)));
    }
}
