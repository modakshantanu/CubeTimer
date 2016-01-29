package com.shantanu.cubetimer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.os.Handler;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.gnehzr.tnoodle.scrambles.Puzzle;
import net.gnehzr.tnoodle.svglite.Dimension;
import net.gnehzr.tnoodle.svglite.Svg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    String[] catList;
    Category[] category;
    Category currentcat = new Category();

    long average = 0,a5 =0,a12 = 0;
    int numberofsolves = 0;
    int session;

    ProgressBar progress;
    ProgressBar redProgress;

    Toolbar toolbar;
    LinearLayout solveLayout1,solveLayout2;
    TextView avg,time,solves,scramble,A5view,A12view;
    long starttime = 0;
    long timeinms = 0;
    long timeswap = 0;
    long updatedtime = 0;
    int secs = 0;
    int mins = 0;
    int ms = 0;
    Handler handler = new Handler();
    genScramble genScramble = new genScramble();
    Runnable updateTimer;
    RelativeLayout layout;
    Boolean timerRunning = false;
    Boolean waitingToStart = false;
    Boolean longPress;
    boolean justcreated = true;
    String scrambleString;

    SharedPreferences.Editor editor;
    SharedPreferences preferences;

    Spinner sessionSpinner;
    ArrayAdapter<String> sessionAdapter;

    Spinner spinner;
    ArrayAdapter<String> adapter;
    List<String> spinnerArray,sessionArray;
    ScrambleGenerator generator = new ScrambleGenerator();

    DBHandler database;

    void setupVariables(){

        A12view = (TextView) findViewById(R.id.tvA12);
        A5view = (TextView) findViewById(R.id.tvA5);
        scramble = (TextView) findViewById(R.id.tvScramble);
        avg = (TextView) findViewById(R.id.tvAvg);
        time = (TextView) findViewById(R.id.tvTimer);
        layout = (RelativeLayout) findViewById(R.id.layout);
        solves = (TextView) findViewById(R.id.tvCount);
        solveLayout1 = (LinearLayout) findViewById(R.id.layout1);
        solveLayout2 = (LinearLayout) findViewById(R.id.layout2);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        progress = (ProgressBar) findViewById(R.id.progressBar);
        redProgress = (ProgressBar) findViewById(R.id.redProgress);
        spinner = (Spinner) findViewById(R.id.spinner);
        sessionSpinner = (Spinner) findViewById(R.id.sessionspinner);

        database = new DBHandler(this,null,null,0);


        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        editor = preferences.edit();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        progress.setMax(1000);
        redProgress.setMax(1000);


        if(preferences.getInt("_versioncode",-1)!=BuildConfig.VERSION_CODE||true)
            initSharedPrefs();
        getSharedPrefs();

        session = database.getMaxSession(currentcat.name);
        Log.e("max",String.valueOf(currentcat.name));

        updateSessionSpinner(database.getMaxSession(currentcat.name));

        String[] displaylist = new String[catList.length];
        for(int i=0;i<catList.length;i++){
            displaylist[i] =catList[i].substring(1).replace("_"," ");

        }

        spinnerArray =  new ArrayList<>(Arrays.asList(displaylist));

        adapter = new ArrayAdapter<>(this,R.layout.spinnerhead, spinnerArray);
        adapter.setDropDownViewResource(R.layout.spinnerstyle);
        spinner.setAdapter(adapter);

        Log.e("puzz", String.valueOf(currentcat.puzzle.id));
        spinner.setSelection(currentcat.puzzle.id);
        spinner.setOnItemSelectedListener(this);



        justcreated = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupVariables();
        showStats();

        scramble.setText("Generating Scramble...");
        genScramble.execute();



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
                        resetProgressBars();

                        if (!longPress)
                            if (!timerRunning) {
                                time.setTextColor(Color.GREEN);
                                waitingToStart = true;

                            }

                    } else {
                        numberofsolves++;
                        timeswap += timeinms;

                        addTime(updatedtime);

                        showStats();
                        time.setTextColor(Color.WHITE);
                        handler.removeCallbacks(updateTimer);

                        timerRunning = false;
                        waitingToStart = false;

                        newScramble();

                        return true;
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (!timerRunning && !waitingToStart)
                        time.setTextColor(Color.WHITE);
                    if (waitingToStart) {
                        waitingToStart = false;
                        timerRunning = true;
                        starttime = SystemClock.uptimeMillis();


                        genScramble = new genScramble();
                        if (genScramble.getStatus() != AsyncTask.Status.RUNNING)
                            genScramble.execute();
                        handler.post(updateTimer);
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
        layout.setOnLongClickListener(new View.OnLongClickListener() {


            @Override
            public boolean onLongClick(View v) {
                if (longPress)
                    if (!timerRunning) {
                        time.setTextColor(Color.GREEN);
                        waitingToStart = true;
                    }


                return false;
            }
        });




    }



    @Override
    protected void onPostResume() {

        longPress = preferences.getBoolean("longpress", true);

        super.onPostResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        for(int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            SpannableString spanString = new SpannableString(menu.getItem(i).getTitle().toString());
            spanString.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spanString.length(), 0); //fix the color to white
            item.setTitle(spanString);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        switch(id){
            case R.id.action_settings:
                Intent i = new Intent(this,SettingsActivity.class);
                startActivity(i);
                break;
            case R.id.delete:

                if(!timerRunning) {
                    modifyDialogBox();

                }else {

                    time.setTextColor(Color.WHITE);
                    time.setText("0.00");
                    handler.removeCallbacks(updateTimer);
                    progress.setProgress(0);
                    redProgress.setProgress(0);
                    timerRunning = false;
                    waitingToStart = false;

                }
                break;
            case R.id.rotate:
                int orientation=this.getResources().getConfiguration().orientation;
                if(orientation==Configuration.ORIENTATION_PORTRAIT) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                }else{
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                }
                break;
            case R.id.reset:
                database.deleteAllSolves(currentcat.name,session);
                resetProgressBars();
                showStats();
                break;
            case R.id.newsession:
                if(database.getSolveCount(currentcat.name,session)!=0) {
                    session = database.getMaxSession(currentcat.name);
                    session++;
                    Log.e("newsession", String.valueOf(session));
                    updateSessionSpinner(session);
                    showStats();
                }
                break;


        }


        return super.onOptionsItemSelected(item);
    }

    void modifyDialogBox(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this,4);
        builder.setTitle("Modify solve")
        .setNegativeButton(" Cancel ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        })
        .setItems(R.array.Modifies, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(database.getSolveCount(currentcat.name,session)!=0)
                    switch (which){
                        case 0:

                            database.deletePrevious(currentcat.name,session);
                            Toast.makeText(getApplicationContext(),"Previous solve deleted",Toast.LENGTH_SHORT).show();
                            showStats();
                            break;
                        case 1:
                            database.addPenalty(currentcat.name, session, Penalty.PLUS2);
                            Toast.makeText(getApplicationContext(),"+2",Toast.LENGTH_SHORT).show();
                            showStats();
                            break;
                        case 2:
                            database.addPenalty(currentcat.name,session,Penalty.DNF);
                            Toast.makeText(getApplicationContext(),"DNF",Toast.LENGTH_SHORT).show();
                            showStats();
                            break;
                        case 3:
                            database.addPenalty(currentcat.name,session,Penalty.NONE);
                            Toast.makeText(getApplicationContext(),"Penalty removed",Toast.LENGTH_SHORT).show();
                            showStats();
                            break;


                    }
            }
        }).show();

    }
    public void calculateAverages() {


        Solve[] s = database.getAllSolves(currentcat.name,session);
        numberofsolves = s.length;

        if (numberofsolves != 0) {

            a5 = 0;
            a12 = 0;
            long max = 0;
            long min = Long.MAX_VALUE;
            long totaltime = 0;
            for (int i = 0; i < s.length; i++) {
                totaltime += s[i].solvetime;
            }
            average = totaltime / numberofsolves;
            max =  0;
            if (numberofsolves >= 5) {
                for (int i = 0; i < 5; i++) {
                    max = Math.max(max, s[i].solvetime);
                    min = Math.min(min, s[i].solvetime);
                }

                for(int i=0;i<5;i++){

                    if(s[i].solvetime!=max&&s[i].solvetime!=min){
                        a5+=s[i].solvetime;
                    }

                }
                a5/=3;

            }else
                a5 = 0;

            max = 0;
            min = Long.MAX_VALUE;
            if (numberofsolves >= 12) {
                for (int i = 0; i < 12; i++) {
                    max = Math.max(max, s[i].solvetime);
                    min = Math.min(min, s[i].solvetime);
                }
                for(int i=0;i<12;i++){
                    if(s[i].solvetime!=max&&s[i].solvetime!=min)
                        a12+=s[i].solvetime;
                }
                a12/=10;

            }else
                a12 = 0;


        } else {
            average = 0;
            a5 = 0;
            a12 = 0;
        }
    }


    public void showStats(){
        calculateAverages();
        avg.setText(average==0?" - ":timeToString(average));
        solves.setText(String.valueOf(numberofsolves));
        A5view.setText(a5 == 0 ? " - " : timeToString(a5));
        A12view.setText(a12 == 0 ? " - " : timeToString(a12));

        showTimes(getSolves());


    }

    public void newScramble(){

        scramble.setTextSize(currentcat.puzzle.getFontSize());
        scramble.setText(scrambleString);

    }

     private class genScramble extends AsyncTask<Void,Void,Void>{

         @Override
         protected Void doInBackground(Void... params) {

             scrambleString = generator.getScramble(currentcat.puzzle);
             return null;
         }

         @Override
         protected void onPostExecute(Void aVoid) {
             super.onPostExecute(aVoid);
             if(!timerRunning) {
                 scramble.setTextSize(currentcat.puzzle.getFontSize());
                 scramble.setText(scrambleString);
             }
         }
     }


    public void resetProgressBars(){
        progress.setProgress(0);
        redProgress.setProgress(0);

    }

    void initSharedPrefs() {

        catList = database.getCatList();
        editor.putString("_currentcat", "_3x3");
        editor.putInt("_versioncode", BuildConfig.VERSION_CODE);
        editor.commit();

    }
    public void saveSharedPrefs(){
        editor.putString("_currentcat", currentcat.name);
        editor.commit();
    }
    public void getSharedPrefs(){
        catList = database.getCatList();
        int[] puzzleids= database.getPuzzleIds();
        category = new Category[database.getCatCount()];


        currentcat.name = preferences.getString("_currentcat","_3x3");
        for(int i=0;i<category.length;i++){

            category[i]=new Category();
            category[i].name = catList[i];
            category[i].puzzle = com.shantanu.cubetimer.Puzzle.getById(puzzleids[i]);
            if(currentcat.name.equals(category[i].name)) {
                currentcat.puzzle = com.shantanu.cubetimer.Puzzle.getById(puzzleids[i]);
            }

        }

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Spinner s = (Spinner) parent;

        switch (s.getId()) {
            case R.id.spinner:
                if (!justcreated) {
                    currentcat = category[position];
                    Log.e("puzzle", currentcat.puzzle.name());
                    showStats();
                    updateSessionSpinner(database.getMaxSession(currentcat.name));
                    session = database.getMaxSession(currentcat.name);

                    scramble.setText("Generating Scramble...");
                    genScramble = new genScramble();
                    if (genScramble.getStatus() != AsyncTask.Status.RUNNING)
                        genScramble.execute();

                    saveSharedPrefs();

                } else
                    justcreated = false;
                    break;

            case R.id.sessionspinner:
                if(database.getSolveCount(currentcat.name,session)!=0) {
                    Log.e("pos", String.valueOf(database.getMaxSession(currentcat.name) - position));
                    session = database.getMaxSession(currentcat.name) - position;
                    showStats();
                }
                break;
        }
    }

    public void addTime(long time){
        Solve s= new Solve();
        s.solvetime = time;
        s.penalty = Penalty.NONE;
        database.addSolve(s,currentcat.name,session);

    }

    public void showTimes(Solve[] solveArray) {

        TextView[] textViews = new TextView[solveArray.length];

        solveLayout1.removeAllViews();
        solveLayout2.removeAllViews();

        for (int i = 0; i < solveArray.length; i++) {
            textViews[i] = new TextView(this);
            textViews[i].setTextColor(Color.WHITE);
            textViews[i].setTextSize(18);
            textViews[i].setGravity(Gravity.RIGHT);

            if(solveArray[i].penalty == Penalty.DNF) {
                textViews[i].setText("DNF");
                continue;
            }

            textViews[i].setText(timeToString(solveArray[i].solvetime + (solveArray[i].penalty == Penalty.PLUS2 ? 2000 : 0)) + (solveArray[i].penalty == Penalty.PLUS2 ? "+" : ""));


        }

        for (int i = 0; i < Math.min(5,solveArray.length); i++) {
            solveLayout1.addView(textViews[i]);
            if (i + 5 < solveArray.length) {
                solveLayout2.addView(textViews[i + 5]);
            }
        }

    }

    public void updateSessionSpinner(int max){
        String[] sessionList = new String[max];

        for(int i=0;i<sessionList.length;i++){
            sessionList[i] = String.valueOf(max - i);
        }


        sessionArray = new ArrayList<>(Arrays.asList(sessionList));

        sessionAdapter = new ArrayAdapter<>(this,R.layout.spinnerhead,sessionArray);

        sessionAdapter.setDropDownViewResource(R.layout.spinnerstyle);
        sessionSpinner.setAdapter(sessionAdapter);
        sessionSpinner.setSelection(0);
        sessionSpinner.setOnItemSelectedListener(this);
    }

    Solve[] getSolves(){
        return database.getAllSolves(currentcat.name,session);
    }

    String timeToString(long time){
        secs = (int) (time / 1000);
        mins = secs / 60;
        secs = secs % 60;
        ms = (int) (time % 1000) / 10;
        return(mins > 0 ? (String.format("%d.%02d.%02d", mins, secs, ms)) : (String.format((secs > 9 ? "%02d." : "%01d.") + "%02d", secs, ms)));
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }



}