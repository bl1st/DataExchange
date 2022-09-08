package com.example.dataexchange;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.sql.Date;
import java.util.ArrayList;

public class DB extends SQLiteOpenHelper {
    public DB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE messages (IP TEXT,PORT TEXT,NAME TEXT,MESSAGE TEXT, DATE INTEGER);";
        db.execSQL(sql);
    }
    public void SaveMessage(Message m) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "INSERT INTO messages VALUES ('" + m.IP + "','" + m.Port + "','" + m.Name + "','" + m.Text + "'," + m.DateTime.getTime() + ");";
        db.execSQL(sql);
    }
   public void LoadHistory(ArrayList<Message> lst){
       SQLiteDatabase db = getReadableDatabase();
       String sql = "SELECT * FROM messages";
       Cursor cur = db.rawQuery(sql, null);
       if (cur.moveToFirst() == true) {
           do {
               Message m = new Message();
               m.IP = cur.getString(0);
               m.Port = cur.getString(1);
               m.Name = cur.getString(2);
               m.Text = cur.getString(3);
               m.DateTime = new Date(Long.parseLong(cur.getString(4)));
               lst.add(m);

           }while (cur.moveToNext() == true);
       }
   }
    public void CLEARALLTABLE(){
        SQLiteDatabase db = getWritableDatabase();
        String sql = "DELETE FROM messages";
        db.execSQL(sql);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
