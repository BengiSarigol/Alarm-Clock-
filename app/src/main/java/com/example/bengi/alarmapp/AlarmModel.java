package com.example.bengi.alarmapp;

import android.net.Uri;

public class AlarmModel {

    public static final int SUNDAY = 0; //takvim sınıfındaki sayısal değerlerine uygun sabitler verildi
    public static final int MONDAY = 1;
    public static final int TUESDAY = 2;
    public static final int WEDNESDAY = 3;
    public static final int THURSDAY = 4;
    public static final int FRIDAY = 5;
    public static final int SATURDAY = 6;

    public long id= -1;
    public int timeHour;
    public int timeMinute;
    private boolean repeatingDays[];//alarmın hangi günler tekrar edildiği
    public boolean haftaliktekrar;
    public Uri alarmTone;
    public String name; //alarm ismi
    public boolean isEnabled;

    //get set metodları::
    //kullanılıcak değerleri alırken yada iletirken değişiklik yapmak istersek kullanırız.örneğin b değerini iletmeden 10 ile çarp dersek
    //ama eger değişikliğe gerek yoksa get ve set metodlarınada gerek yoktur.

    public AlarmModel(){ //const.
        repeatingDays = new boolean[7];
    }

    //Alarmın hangi günler tekrarlanıcağını belirlemek için set ve get metodu oluşturuyorum.
    public boolean setRepeatingDay(int dayofWeek, boolean value){
        return  repeatingDays[dayofWeek] = value;
    }


    public boolean getRepeatingDay(int dayofWeek){
        return  repeatingDays[dayofWeek] ;
    }


}

