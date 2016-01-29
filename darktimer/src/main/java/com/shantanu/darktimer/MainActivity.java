package com.shantanu.darktimer;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends WearableActivity {

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    TextView time,previous,avg5,avg12;
    RelativeLayout layout;

    List<Long> times;

    ProgressBar progress;
    ProgressBar redProgress;
    long starttime = 0;
    long timeinms = 0;
    long timeswap = 0;
    long updatedtime = 0;
    int secs = 0;
    int mins = 0;
    int ms = 0;
    long average = 0;
    Runnable updateTimer,longpress;
    Boolean timerRunning = false;
    Boolean waitingToStart = false;
    Handler handler = new Handler();
    Handler lpress = new Handler();

    private int longClickDuration = 2000;
    private boolean isLongPress = false;

    Button delall,delone,back;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                time = (TextView) findViewById(R.id.tvTimer);
                previous = (TextView) findViewById(R.id.previous);
                avg5 = (TextView) findViewById(R.id.avg5);
                avg12 = (TextView) findViewById(R.id.avg12);
                layout = (RelativeLayout) findViewById(R.id.layout);
                progress = (ProgressBar) findViewById(R.id.progressBar);
                redProgress = (ProgressBar) findViewById(R.id.redProgress);
                delall = (Button) findViewById(R.id.delall);
                delone = (Button) findViewById(R.id.delone);
                back = (Button) findViewById(R.id.back);
                delall.setVisibility(View.INVISIBLE);
                delone.setVisibility(View.INVISIBLE);
                back.setVisibility(View.INVISIBLE);
                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideButtons();
                    }
                });
                delall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        times.clear();
                        updateStats();

                    }
                });
                delone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!times.isEmpty()) {
                            times.remove(0);
                            updateStats();
                        }
                    }
                });
                times = new ArrayList<>();


                setAmbientEnabled();


                progress.setMax(100);
                redProgress.setMax(100);
                resetProgressBars();
                layout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        if (event.getAction() == MotionEvent.ACTION_DOWN) {

                            isLongPress = true;


                            lpress.postDelayed(longpress, longClickDuration);
                            if (!timerRunning) {
                                starttime = 0;
                                timeinms = 0;
                                updatedtime = 0;
                                timeswap = 0;
                                time.setText(timeToString(0));
                                time.setTextColor(Color.RED);
                                handler.removeCallbacks(updateTimer);
                                resetProgressBars();

                                time.setTextColor(Color.GREEN);
                                waitingToStart = true;
                                return true;


                            } else {

                                timeswap += timeinms;
                                times.add(0, updatedtime);

                                time.setTextColor(Color.WHITE);
                                handler.removeCallbacks(updateTimer);


                                updateStats();

                                timerRunning = false;
                                waitingToStart = false;

                                isLongPress = false;

                                return false;
                            }
                        } else if (event.getAction() == MotionEvent.ACTION_UP) {

                            lpress.removeCallbacks(longpress);
                            isLongPress = false;
                            if (!timerRunning && !waitingToStart)
                                time.setTextColor(Color.WHITE);
                            if (waitingToStart) {
                                waitingToStart = false;
                                timerRunning = true;
                                starttime = SystemClock.elapsedRealtime();
                                handler.post(updateTimer);
                                time.setTextColor(Color.WHITE);


                            }
                        }

                        return true;
                    }


                });
            }
        });




        longpress = new Runnable() {
            @Override
            public void run() {

                if(isLongPress) {


                    showButtons();
                    timerRunning = waitingToStart = false;
                    isLongPress = false;
                    handler.removeCallbacks(longpress);
                }
            }
        };

        updateTimer = new Runnable() {
            @Override
            public void run() {
                timeinms = SystemClock.elapsedRealtime() - starttime;

                updatedtime = timeswap + timeinms;


                if(!isAmbient()) {
                    time.setText(timeToString(updatedtime));
                    if (average != 0) {
                        progress.setProgress((int) (((float) updatedtime / (float) average) * 100));
                    }

                    if (updatedtime > average && average > 0) {
                        redProgress.setProgress((int) ((1.0F - ((float) average / (float) updatedtime)) * 100));
                    } else
                        redProgress.setProgress(0);

                }else{
                    resetProgressBars();
                    progress.setProgress(100);
                }

                handler.postDelayed(this, 10);
            }
        };
    }
    @Override
    public void onEnterAmbient(Bundle ambientDetails) {

        previous.setVisibility(View.INVISIBLE);
        avg5.setVisibility(View.INVISIBLE);
        avg12.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.INVISIBLE);
        redProgress.setVisibility(View.INVISIBLE);


        hideButtons();
        if(timerRunning){
            time.setTextSize(20);
            time.setText("Timer running");
        }


        handler.removeCallbacks(updateTimer);
        super.onEnterAmbient(ambientDetails);

    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();

    }

    @Override
    public void onExitAmbient() {
        time.setTextSize(30);

        previous.setVisibility(View.VISIBLE);
        avg5.setVisibility(View.VISIBLE);
        avg12.setVisibility(View.VISIBLE);
        progress.setVisibility(View.VISIBLE);
        redProgress.setVisibility(View.VISIBLE);



        if(timerRunning){

            timeinms = SystemClock.elapsedRealtime() - starttime;

            updatedtime = timeswap + timeinms;


            if(!isAmbient()) {
                time.setText(timeToString(updatedtime));
                if (average != 0) {
                    progress.setProgress((int) (((float) updatedtime / (float) average) * 100));
                }

                if (updatedtime > average && average > 0) {
                    redProgress.setProgress((int) ((1.0F - ((float) average / (float) updatedtime)) * 100));
                } else
                    redProgress.setProgress(0);

            }else{
                resetProgressBars();
                progress.setProgress(100);
            }


            timeswap += timeinms;

            times.add(0,updatedtime);
            previous.setText(timeToString(updatedtime));
            time.setText(timeToString(updatedtime));
            handler.removeCallbacks(updateTimer);


            timerRunning = false;
            waitingToStart = false;
        }
        super.onExitAmbient();
    }

    String timeToString(long time){
        secs = (int) (time / 1000);
        mins = secs / 60;
        secs = secs % 60;
        ms = (int) (time % 1000) / 10;
        return(mins > 0 ? (String.format("%d:%02d.%02d", mins, secs, ms)) : (String.format((secs > 9 ? "%02d." : "%01d.") + "%02d", secs, ms)));
    }
    public void resetProgressBars(){
        progress.setProgress(0);
        redProgress.setProgress(0);

    }

    long average(List<Long> list,int number){
        long sum=0,max = 0,min = Long.MAX_VALUE;

        for(int i=0;i<number;i++){
            sum+=list.get(i);
            if(list.get(i)>max)max = list.get(i);
            if(list.get(i)<min)min = list.get(i);

        }
        sum = sum-max-min;
        return sum/(number-2);

    }
    long mean(List<Long> list){
        long sum=0;

        for(int i=0;i<list.size();i++){
            sum+=list.get(i);
        }
        return sum/list.size();

    }

    void hideButtons(){
        delall.setVisibility(View.INVISIBLE);
        delone.setVisibility(View.INVISIBLE);
        back.setVisibility(View.INVISIBLE);
    }
    void showButtons(){
        delall.setVisibility(View.VISIBLE);
        delone.setVisibility(View.VISIBLE);
        back.setVisibility(View.VISIBLE);
    }
    void updateStats(){
        if(!times.isEmpty()) {
            previous.setText(timeToString(times.get(0)));
            average = mean(times);

            if (times.size() >= 5) {
                avg5.setText(timeToString(average(times, 5)));
                average = average(times, 5);
            }
            if (times.size() >= 12) {
                avg12.setText(timeToString(average(times, 12)));
            }
        }
        else{
            previous.setText("Previous");
            avg5.setText("Avg5");
            avg12.setText("Avg12");
        }
    }
}
