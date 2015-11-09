package com.shantanu.cubetimer;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
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

    public static final int DATABASE_VERSION = 11;
    public static final String DATABASE_NAME = "Solves.db";

    public String[] catList;


    public static final String ID = "_id";
    public static final String TIME = "solvetime";


    public Cursor c ;

    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version,String[] catList) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        this.catList = catList;
    }



    @Override
    public void onCreate(SQLiteDatabase db) {

        for(int i=0;i<catList.length;i++){
            Log.e("te",catList[i]);
            db.execSQL("CREATE TABLE " + catList[i] + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TIME + " LONG );");


        }


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        for(int i=0;i<catList.length;i++){
            db.execSQL("DROP TABLE IF EXISTS "+catList[i] +";");
        }
        onCreate(db);
    }

    void addSolve(Solve solve,String table){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        String TABLE_NAME = table;

        values.put(TIME, solve.solvetime);

        db.insert(TABLE_NAME, null, values);

    }

    Solve[] getAllSolves(String table){

        String TABLE_NAME = table;

        String query = "SELECT * FROM " + TABLE_NAME +" ORDER BY "+ID +" DESC;";

        SQLiteDatabase db = this.getReadableDatabase();
        c = db.rawQuery(query, null);

        Solve[] solveList = new Solve[c.getCount()];
        int i = 0;

        if(c.moveToFirst()){
            do{
                solveList[i]= new Solve();
                solveList[i].solvetime = c.getLong(c.getColumnIndex(TIME));
                solveList[i]._id = c.getInt(c.getColumnIndex(ID));
                i++;
            }while(c.moveToNext());
        }

        return solveList;
    }

    public void deletePrevious(String table){
        String query = "SELECT MAX("+ID+") FROM "+table+";";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(query,null);

        if(c.moveToFirst()) {
            int maxid = c.getInt(0);
            db.delete(table, ID + " = " + maxid, null);
        }
    }

    void deleteAllSolves(String table){
        getWritableDatabase().delete(table,null,null);
    }
}













