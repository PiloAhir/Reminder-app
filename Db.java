package com.maroreminder.app;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import java.util.*;

public class Db extends SQLiteOpenHelper {
    public Db(Context c){super(c,"reminders.db",null,1);}
    public void onCreate(SQLiteDatabase d){
        d.execSQL("CREATE TABLE reminders(id INTEGER PRIMARY KEY AUTOINCREMENT,title TEXT NOT NULL,note TEXT,time INTEGER NOT NULL)");
    }
    public void onUpgrade(SQLiteDatabase d,int o,int n){}
    public long add(String title,String note,long time){
        ContentValues v=new ContentValues();v.put("title",title);v.put("note",note);v.put("time",time);
        return getWritableDatabase().insert("reminders",null,v);
    }
    public void update(long id,String title,String note,long time){
        ContentValues v=new ContentValues();v.put("title",title);v.put("note",note);v.put("time",time);
        getWritableDatabase().update("reminders",v,"id=?",new String[]{String.valueOf(id)});
    }
    public void delete(long id){getWritableDatabase().delete("reminders","id=?",new String[]{String.valueOf(id)});}
    public ArrayList<Reminder> all(){
        ArrayList<Reminder>a=new ArrayList<>();
        try(Cursor c=getReadableDatabase().rawQuery("SELECT id,title,note,time FROM reminders ORDER BY time",null)){
            while(c.moveToNext())a.add(new Reminder(c.getLong(0),c.getString(1),c.getString(2),c.getLong(3)));
        } return a;
    }
    public void replaceAll(ArrayList<Reminder> rows){
        SQLiteDatabase d=getWritableDatabase();d.beginTransaction();
        try{d.delete("reminders",null,null);for(Reminder r:rows){ContentValues v=new ContentValues();v.put("title",r.title);v.put("note",r.note);v.put("time",r.time);d.insert("reminders",null,v);}d.setTransactionSuccessful();}
        finally{d.endTransaction();}
    }
}
