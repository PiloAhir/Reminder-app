package com.maroreminder.app;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.*;
import android.widget.*;
import java.text.*;
import java.util.*;
import org.json.*;

public class MainActivity extends Activity {
    Db db; LinearLayout root,list; boolean dark=false;
    int bg,fg,accent;
    static final int EXPORT=10,IMPORT=11;
    @Override public void onCreate(Bundle b){super.onCreate(b);db=new Db(this);getWindow().setStatusBarColor(Color.rgb(22,125,141));getWindow().setNavigationBarColor(Color.rgb(22,125,141));if(Build.VERSION.SDK_INT>=33&&checkSelfPermission("android.permission.POST_NOTIFICATIONS")!=android.content.pm.PackageManager.PERMISSION_GRANTED)requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"},4);show();}
    void theme(){bg=dark?Color.rgb(24,29,34):Color.rgb(244,248,250);fg=dark?Color.WHITE:Color.rgb(25,42,52);accent=Color.rgb(22,125,141);}
    void show(){
        theme();root=new LinearLayout(this);root.setOrientation(1);root.setPadding(dp(16),dp(12),dp(16),dp(8));root.setBackgroundColor(bg);
        LinearLayout head=new LinearLayout(this);head.setGravity(Gravity.CENTER_VERTICAL);
        TextView title=txt("મારો રિમાઇન્ડર",24,true);head.addView(title,new LinearLayout.LayoutParams(0,-2,1));
        Button theme=button(dark?"☀":"☾");head.addView(theme,new LinearLayout.LayoutParams(dp(48),dp(44)));theme.setOnClickListener(v->{dark=!dark;show();});
        Button backup=button("⋮");head.addView(backup,new LinearLayout.LayoutParams(dp(48),dp(44)));backup.setOnClickListener(v->menu());
        root.addView(head);
        TextView sub=txt("તમારા કામ, તમારી યાદ — સંપૂર્ણ ઓફલાઇન",13,false);root.addView(sub);
        Button add=button("+ નવો રિમાઇન્ડર");add.setOnClickListener(v->editor(null));LinearLayout.LayoutParams ap=new LinearLayout.LayoutParams(-1,dp(50));ap.topMargin=dp(14);root.addView(add,ap);
        ScrollView sc=new ScrollView(this);list=new LinearLayout(this);list.setOrientation(1);sc.addView(list);root.addView(sc,new LinearLayout.LayoutParams(-1,0,1));
        ArrayList<Reminder> rows=db.all();
        if(rows.isEmpty()){TextView empty=txt("હજુ કોઈ રિમાઇન્ડર નથી.\nઉપરનું બટન દબાવી પહેલું રિમાઇન્ડર બનાવો.",15,false);empty.setGravity(Gravity.CENTER);list.addView(empty,new LinearLayout.LayoutParams(-1,dp(180)));}
        SimpleDateFormat fmt=new SimpleDateFormat("dd MMM yyyy, hh:mm a",Locale.getDefault());
        for(Reminder r:rows){
            LinearLayout card=new LinearLayout(this);card.setOrientation(1);card.setPadding(dp(13),dp(10),dp(13),dp(10));card.setBackground(round(dark?Color.rgb(39,47,54):Color.WHITE));
            TextView t=txt(r.title,17,true);card.addView(t);card.addView(txt(fmt.format(new Date(r.time)),13,false));
            if(r.note!=null&&!r.note.isEmpty())card.addView(txt(r.note,13,false));
            LinearLayout actions=new LinearLayout(this);Button edit=button("ફેરફાર"),del=button("ડિલીટ");actions.addView(edit,new LinearLayout.LayoutParams(0,dp(40),1));actions.addView(del,new LinearLayout.LayoutParams(0,dp(40),1));card.addView(actions);
            edit.setOnClickListener(v->editor(r));del.setOnClickListener(v->new AlertDialog.Builder(this).setMessage("આ રિમાઇન્ડર ડિલીટ કરવું છે?").setNegativeButton("ના",null).setPositiveButton("હા",(d,w)->{cancel(this,r.id);db.delete(r.id);show();}).show());
            LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,-2);cp.topMargin=dp(10);list.addView(card,cp);
        }
        setContentView(root);
    }
    void editor(Reminder old){
        EditText name=new EditText(this);name.setSingleLine(true);name.setHint("રિમાઇન્ડરનું નામ");if(old!=null)name.setText(old.title);
        EditText note=new EditText(this);note.setHint("વિગત (વૈકલ્પિક)");if(old!=null)note.setText(old.note);
        Calendar cal=Calendar.getInstance();if(old!=null)cal.setTimeInMillis(old.time);
        DatePickerDialog date=new DatePickerDialog(this,(v,y,m,d)->{
            TimePickerDialog time=new TimePickerDialog(this,(tv,h,min)->{
                cal.set(y,m,d,h,min,0);cal.set(Calendar.MILLISECOND,0);
                String n=name.getText().toString().trim();if(n.isEmpty()){msg("રિમાઇન્ડરનું નામ લખો.");return;}
                long id=old==null?db.add(n,note.getText().toString().trim(),cal.getTimeInMillis()):old.id;
                if(old!=null)db.update(id,n,note.getText().toString().trim(),cal.getTimeInMillis());
                if(cal.getTimeInMillis()<=System.currentTimeMillis()){msg("ભૂતકાળનો સમય પસંદ થયો છે. રિમાઇન્ડર સેવ થયું, પરંતુ સૂચના નહીં આવે.");}
                else schedule(this,new Reminder(id,n,note.getText().toString().trim(),cal.getTimeInMillis()));
                show();
            },cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE),false);time.show();
        },cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
        LinearLayout box=new LinearLayout(this);box.setOrientation(1);box.setPadding(dp(8),0,dp(8),0);box.addView(name);box.addView(note);
        new AlertDialog.Builder(this).setTitle(old==null?"નવો રિમાઇન્ડર":"રિમાઇન્ડર ફેરફાર").setView(box).setPositiveButton("તારીખ અને સમય પસંદ કરો",(d,w)->date.show()).setNegativeButton("રદ કરો",null).show();
    }
    static void schedule(Context c,Reminder r){
        Intent i=new Intent(c,ReminderReceiver.class);i.putExtra("title",r.title);i.putExtra("note",r.note);i.putExtra("id",(int)r.id);
        PendingIntent p=PendingIntent.getBroadcast(c,(int)r.id,i,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        AlarmManager a=(AlarmManager)c.getSystemService(ALARM_SERVICE);a.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,r.time,p);
    }
    static void cancel(Context c,long id){Intent i=new Intent(c,ReminderReceiver.class);PendingIntent p=PendingIntent.getBroadcast(c,(int)id,i,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);((AlarmManager)c.getSystemService(ALARM_SERVICE)).cancel(p);}
    void menu(){String[] a={"બેકઅપ Export","બેકઅપ Import","બધા રિમાઇન્ડર ડિલીટ"};new AlertDialog.Builder(this).setTitle("વધુ વિકલ્પો").setItems(a,(d,n)->{if(n==0){Intent i=new Intent(Intent.ACTION_CREATE_DOCUMENT);i.setType("application/json");i.putExtra(Intent.EXTRA_TITLE,"maro-reminder-backup.json");startActivityForResult(i,EXPORT);}else if(n==1){Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.setType("application/json");i.addCategory(Intent.CATEGORY_OPENABLE);startActivityForResult(i,IMPORT);}else new AlertDialog.Builder(this).setMessage("બધા રિમાઇન્ડર ડિલીટ કરવા?").setNegativeButton("ના",null).setPositiveButton("હા",(x,y)->{for(Reminder r:db.all())cancel(this,r.id);db.replaceAll(new ArrayList<>());show();}).show();}).show();}
    @Override protected void onActivityResult(int req,int res,Intent data){super.onActivityResult(req,res,data);if(res!=RESULT_OK||data==null)return;try{Uri u=data.getData();if(req==EXPORT){JSONArray a=new JSONArray();for(Reminder r:db.all()){JSONObject o=new JSONObject();o.put("title",r.title);o.put("note",r.note);o.put("time",r.time);a.put(o);}try(java.io.OutputStream out=getContentResolver().openOutputStream(u)){out.write(a.toString(2).getBytes("UTF-8"));}msg("બેકઅપ સેવ થયું.");}else if(req==IMPORT){java.io.InputStream in=getContentResolver().openInputStream(u);java.io.ByteArrayOutputStream out=new java.io.ByteArrayOutputStream();byte[] b=new byte[4096];int n;while((n=in.read(b))>0)out.write(b,0,n);in.close();JSONArray a=new JSONArray(out.toString("UTF-8"));ArrayList<Reminder> rows=new ArrayList<>();for(int k=0;k<a.length();k++){JSONObject o=a.getJSONObject(k);rows.add(new Reminder(0,o.getString("title"),o.optString("note",""),o.getLong("time")));}for(Reminder r:db.all())cancel(this,r.id);db.replaceAll(rows);for(Reminder r:db.all())if(r.time>System.currentTimeMillis())schedule(this,r);show();msg("બેકઅપ Import થયું.");}}catch(Exception e){msg("ફાઇલ પ્રક્રિયામાં ભૂલ: "+e.getMessage());}}
    TextView txt(String s,int size,boolean bold){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(fg);t.setPadding(dp(3),dp(5),dp(3),dp(5));if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return t;}
    Button button(String s){Button b=new Button(this);b.setText(s);b.setAllCaps(false);b.setTextColor(Color.rgb(22,125,141));b.setBackground(round(dark?Color.rgb(39,47,54):Color.WHITE));return b;}
    android.graphics.drawable.GradientDrawable round(int c){android.graphics.drawable.GradientDrawable d=new android.graphics.drawable.GradientDrawable();d.setColor(c);d.setCornerRadius(dp(14));d.setStroke(dp(1),dark?Color.rgb(65,77,84):Color.rgb(220,231,235));return d;}
    int dp(int n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}
    void msg(String s){new AlertDialog.Builder(this).setTitle("મારો રિમાઇન્ડર").setMessage(s).setPositiveButton("બરાબર",null).show();}
}
