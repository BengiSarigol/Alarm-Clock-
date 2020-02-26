package com.example.bengi.alarmapp;

import android.content.Intent;
import android.os.IBinder;
import android.app.Service;
import android.content.Context;
import android.os.Handler;

import com.example.bengi.alarmapp.AlarmManagerHelper;


public class AlarmService extends Service {
    public static String TAG = AlarmService.class.getSimpleName();

    @Override
    public IBinder onBind(Intent intent) {//farklı uygulamalarla bilgi paylaşımı yapılıcağı zaman doldurulur biz ondan null döndürdük.
        return null;
    }


    //alarm managerhelper dan gönderilen intentlerimiz vardı.onlar gelicek
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent alarmIntent = new Intent(getBaseContext(), AlarmScreen.class);
        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//zaten alarm varsa yeni alarm açmaz flag
        alarmIntent.putExtras(intent);
        getApplication().startActivity(alarmIntent);

        final Context context=this;
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {//60 saniye sonra yeni alarmlar kurulsun
                AlarmManagerHelper.setAlarms(context);
            }
        },60000);


        return super.onStartCommand(intent, flags, startId);
    }

}


