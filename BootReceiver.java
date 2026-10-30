package com.maroreminder.app;

import android.content.*;
import java.util.*;

public class BootReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context c, Intent i){
        if(Intent.ACTION_BOOT_COMPLETED.equals(i.getAction())){
            for(Reminder r:new Db(c).all()) MainActivity.schedule(c,r);
        }
    }
}
