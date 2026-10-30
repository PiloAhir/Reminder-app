package com.maroreminder.app;

public class Reminder {
    public long id, time;
    public String title, note;
    public Reminder(long id, String title, String note, long time) {
        this.id=id; this.title=title; this.note=note; this.time=time;
    }
}
