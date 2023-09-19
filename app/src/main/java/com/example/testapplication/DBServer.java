package com.example.testapplication;

import android.database.sqlite.SQLiteDatabase;

public class DBServer {
    public static SQLiteDatabase db;

    static {

        db=SQLiteDatabase.openOrCreateDatabase("data/data/com.example.testapplication/Note.db", null);
        //建表语句
        String CREATE_NOTE = "create table Note ("
                + "id integer primary key autoincrement, "
                + "title text, "
                + "content text, "
                + "time text)";

        try{
            db.rawQuery("select count(1) from Note",null);
        }catch(Exception e){
            db.execSQL(CREATE_NOTE);
        }
    }

}
