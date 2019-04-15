package com.xp.note.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class NoteDBOpenHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "note";
    public static final int VERSION = 1;
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String TIME = "time";
    public static final String PRIORITY = "priority";
    public static final String ID = "_id";

    public NoteDBOpenHelper(Context context) {
        super(context, TABLE_NAME, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + TABLE_NAME + " ("
                + ID + " integer primary key autoincrement ,"
                + CONTENT + " TEXT NOT NULL,"
                + TITLE + " TEXT NOT NULL,"
                + TIME + " TEXT NOT NULL,"
                + PRIORITY + " TEXT NOT NULL)");

    }



    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("drop table if exists " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }


}
