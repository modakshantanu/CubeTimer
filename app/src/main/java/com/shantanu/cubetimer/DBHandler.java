package com.shantanu.cubetimer;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;


public class DBHandler extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 21;
    public static final String DATABASE_NAME = "Solves.db";

    public String[] catList;
    public int[] puzzleIds;

    public static final String PENALTY = "_penalty";
    public static final String ID = "_id";
    public static final String TIME = "solvetime";
    public static final String SESSION = "session";

    public static final String CAT_TABLE = "_cats";
    public static final String CAT_NAME = "_name";
    public static final String CAT_PUZZLE = "_puzzle";
    public static final String CAT_INSPECTION = "_inspection";
    public static final String CAT_BASETIME = "_basetime";

    public Cursor c ;

    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        Resources r = context.getResources();
        puzzleIds = r.getIntArray(R.array.PuzzleIDs);
        catList = r.getStringArray(R.array.CatNames);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        ContentValues c = new ContentValues();

        db.execSQL("CREATE TABLE "+CAT_TABLE+" (" +
                CAT_NAME +" VARCHAR(50) PRIMARY KEY ," +
                CAT_PUZZLE +" INTEGER," +
                CAT_BASETIME +" INTEGER DEFAULT -1," +
                CAT_INSPECTION + " INTEGER DEFAULT 0);");

        for(int i=0;i<catList.length;i++){

            db.execSQL("CREATE TABLE " + catList[i] + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SESSION + " INTEGER DEFAULT 1, " +
                    PENALTY + " INTEGER DEFAULT 0,"+
                    TIME + " LONG );");


            c.put(CAT_NAME, catList[i]);
            c.put(CAT_PUZZLE, puzzleIds[i]);
            c.put(CAT_BASETIME, -1);
            c.put(CAT_INSPECTION,0);

            db.insert(CAT_TABLE,null,c);

        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        ContentValues c;
        db.execSQL("DROP TABLE IF EXISTS "+CAT_TABLE+";");

        db.execSQL("CREATE TABLE IF NOT EXISTS "+ CAT_TABLE +"(" +
                CAT_NAME +" VARCHAR(50) PRIMARY KEY ," +
                CAT_PUZZLE +" INTEGER," +
                CAT_BASETIME +" INTEGER DEFAULT -1," +
                CAT_INSPECTION + " INTEGER DEFAULT 0);");



        for(int i=0;i<catList.length;i++) {


            db.execSQL("CREATE TABLE IF NOT EXISTS " + catList[i] + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SESSION + " INTEGER DEFAULT 1, " +
                    PENALTY + " INTEGER DEFAULT 0, " +
                    TIME + " LONG );");




            c = new ContentValues();
            c.put(CAT_NAME, catList[i]);
            c.put(CAT_PUZZLE, puzzleIds[i]);
            c.put(CAT_BASETIME, -1);
            c.put(CAT_INSPECTION, 0);
            db.insert(CAT_TABLE, null, c);


            try {

               db.execSQL("ALTER TABLE " + catList[i] + " ADD "+ PENALTY + " INTEGER DEFAULT 0;");



            }catch (Exception e){
                 Log.e("gh", e.toString());
            }
            try{
                db.execSQL("ALTER TABLE " + catList[i] + " ADD " + SESSION + " INTEGER DEFAULT 1;");
            }catch (Exception e){
                Log.e("gh", e.toString());
            }

            db.execSQL("Update " + catList[i] + " SET SESSION = 1;");
            db.execSQL("Update " + catList[i] + " SET PENALTY = 0;");




        }


    }

    void addSolve(Solve solve,String table,int session){

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TIME, solve.solvetime);
        values.put(PENALTY, solve.penalty.id);
        values.put(SESSION, session);
        db.insert(table, null, values);

    }

    Solve[] getAllSolves(String table,int session){

        String TABLE_NAME = table;

        String query = "SELECT * FROM " + TABLE_NAME +" where session ="+String.valueOf(session)+" ORDER BY "+ID +" DESC;";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        Solve[] solveList = new Solve[c.getCount()];
        int i = 0;

        if(c.moveToFirst()){
            do{

                solveList[i]= new Solve();
                solveList[i].solvetime = c.getLong(c.getColumnIndex(TIME));
                solveList[i]._id = c.getInt(c.getColumnIndex(ID));
                solveList[i].penalty=Penalty.getById(c.getInt(c.getColumnIndex(PENALTY)));

                i++;
            }while(c.moveToNext());
        }
        c.close();
        return solveList;
    }

    public void deletePrevious(String table,int session){
        String query = "SELECT MAX("+ID+") FROM "+table+" where session ="+String.valueOf(session)+";";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(query, null);

        if(c.moveToFirst()) {
            int maxid = c.getInt(0);
            db.delete(table, ID + " = " + maxid, null);
        }
        c.close();
    }

    void deleteAllSolves(String table,int session){
        getWritableDatabase().delete(table, SESSION + " = " + String.valueOf(session), null);
    }

    String[] getCatList(){

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM "+ CAT_TABLE +" ;";

        Cursor c = db.rawQuery(query,null);

        int i=0;
        Log.e("count",String.valueOf(c.getCount()));

        String[] returnArray = new String[c.getCount()];
        if(c.moveToFirst()){
            do{
                returnArray[i] = c.getString(c.getColumnIndex(CAT_NAME));
                i++;
            }while(c.moveToNext());


        }else
            Log.e("count","did not do");

        c.close();
        return returnArray;

    }
    int[] getPuzzleIds(){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM "+ CAT_TABLE +" ;";

        Cursor c = db.rawQuery(query,null);

        int i=0;

        int[] returnArray = new int[c.getCount()];
        if(c.moveToFirst()){
            do{
                returnArray[i] = c.getInt(c.getColumnIndex(CAT_PUZZLE));
                i++;
            }while(c.moveToNext());


        }

        c.close();
        return returnArray;

    }
    int getCatCount(){
        return catList.length;
    }

    int getMaxSession(String table){
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("select max("+SESSION+") from "+table+";",null);
        if(c.moveToFirst()) {
            return c.getInt(0)!=0?c.getInt(0):1;
        }
        else
            return 1;

    }

    int getSolveCount(String table,int session){
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("select count(*) from "+table+" where "+ SESSION +" = "+String.valueOf(session) + ";",null);
        if(c.moveToFirst()) {
            return c.getInt(0);
        }
        else
            return 0;
    }

    public void addPenalty(String table,int session,Penalty p) {

        String query = "SELECT MAX("+ID+") FROM "+table+" where session ="+String.valueOf(session)+";";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(query, null);


        if(c.moveToFirst()) {
            int maxid = c.getInt(0);
            db.execSQL("UPDATE "+table +" SET "+ PENALTY+" = "+ String.valueOf(p.id)+" WHERE "+ID + " = "+ String.valueOf(maxid)+";");
        }
        c.close();

    }
}













