package com.example.bengi.alarmapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private ListView liste;
    private Veritabani veritabani = new Veritabani(this);
    private CustomAdapter adapter;
    public static final int ALARM_SET = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        liste = (ListView) findViewById(R.id.liste);

        //kayıt olan alarmların ekranda gözükmesi
        adapter = new CustomAdapter(this,veritabani.getAlarms());//alarmların olduğu listeyi çekip custoadaptera yolluyoruz
        liste.setAdapter(adapter);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAlarmDetailsActivity(-1);

            }
        });

        //fab gizlenmesi için çünkü eger kayıtlar artarsa fab kaymalı
        liste.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                  //firstVisibleItem yukardan görünen ilk item indeksi,  visibleItemCount: görünen item sayısı,totalItemCount: toplam ıtem sayısı

                if(firstVisibleItem> 0){//görünen item 0 dan büyükse yani ilk sıradaki(0.) scrolla kaydırrsam altta kalıcagı için o zaman fab kaybolsun
                    fab.hide();
                }else{
                    fab.show();
                }
            }
        });
    }

    //HEMDE custom adapterdan çağırılıcak id ona göre değişebilir alarma göre
    public void startAlarmDetailsActivity(long id) {
        Intent intent = new Intent(this,AlarmDetay.class);
        intent.putExtra("id",id);
        startActivityForResult(intent, ALARM_SET);//ALARM_SETİ 0 ALDIM YUKARDA
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode==ALARM_SET){
            adapter.setAlarms(veritabani.getAlarms());//veritabanından alarm listesi alınsın tekrar ve adaptera set edilsin
            adapter.notifyDataSetChanged();//adapterı günceleme
        }
    }

    //public yaptım çünkü adapter sayfamdan çağırıcam
    public void deleteAlarm(long id){
        final long alarmId = id;

        new AlertDialog.Builder(MainActivity.this)
                .setIcon(android.R.drawable.ic_delete)
                .setTitle("Sil").setMessage("Alarm silinecek, onaylıyor musunuz?")
                .setNegativeButton("Hayır",null)
                .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        AlarmManagerHelper.cancelAlarms(MainActivity.this);//bütün alarmları önce durduruyorum
                        veritabani.deleteAlarm(alarmId);//veritabanından alarm silinsin
                        adapter.setAlarms(veritabani.getAlarms());//geriye kalan veritabanındaki alarmları tekrar set ediyorum
                        adapter.notifyDataSetChanged();
                        AlarmManagerHelper.setAlarms(MainActivity.this);
                    }
                }).show();


    }

    //kayıt edilmiş alarmların switch kısmı aktif yada pasif yapılınca çalışcak fonksiyon
    public void setAlarmEnabled(long id, boolean isEnabled){

        AlarmManagerHelper.cancelAlarms(this);//durumları güncelleemeden önce alarmları durduyorum
        AlarmModel model = veritabani.getAlarm(id);
        model.isEnabled = isEnabled; //veritabanından id ile çekilen modelin pasif mi aktif mi durumu tutuluyor
        veritabani.updateAlarm(model);//bu modelın durumu güncelleniyor
        AlarmManagerHelper.setAlarms(MainActivity.this);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.alarmdetay,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
