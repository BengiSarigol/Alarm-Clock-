package com.example.bengi.alarmapp;

import android.app.AlarmManager;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

public class AlarmDetay extends AppCompatActivity {

    private TimePicker timepicker;
    private SwitchCompat haftaliktekrar,pazartesi,sali,carsamba,persembe,cuma,cumartesi,pazar;
    private EditText editname;
    private TextView secilenmelodi;
    private AlarmModel alarmmodel;
    private Veritabani veritabani;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_detay);


        timepicker = (TimePicker) findViewById(R.id.timepicker);
        haftaliktekrar= (SwitchCompat) findViewById(R.id.alarm_tekrar);
        pazartesi= (SwitchCompat) findViewById(R.id.alarm_tekrar_pazartesi);
        sali = (SwitchCompat) findViewById(R.id.alarm_tekrar_sali);
        carsamba = (SwitchCompat) findViewById(R.id.alarm_tekrar_carsamba);
        persembe = (SwitchCompat) findViewById(R.id.alarm_tekrar_perşembe);
        cuma = (SwitchCompat) findViewById(R.id.alarm_tekrar_cuma);
        cumartesi = (SwitchCompat) findViewById(R.id.alarm_tekrar_cumartesi);
        pazar = (SwitchCompat) findViewById(R.id.alarm_tekrar_pazar);
        editname = (EditText)findViewById(R.id.alarm_edittext);
        secilenmelodi = (TextView)findViewById(R.id.secilen_melodi);


        timepicker.setIs24HourView(true);//saati 24 saatlik dilime uygun gösterir.
        veritabani = new Veritabani(this);

        long id =getIntent().getExtras().getLong("id");

        if(id == -1){
            //eğer yeni alarm oluşturulacaksa
            alarmmodel = new AlarmModel();
            alarmmodel.alarmTone = Settings.System.DEFAULT_ALARM_ALERT_URI;
            secilenmelodi.setText(RingtoneManager.getRingtone(this, alarmmodel.alarmTone).getTitle(this));//isim gseçilince gözüksün set edildi
        }else{
            //alarm güncellenecekse
            alarmmodel = veritabani.getAlarm(id);//id'ye sahip alarm ile ilgili veriler alarmmodelde tutulur


            timepicker.setCurrentHour(alarmmodel.timeHour);
            timepicker.setCurrentMinute(alarmmodel.timeMinute);

            editname.setText(alarmmodel.name);

            //ilk sayfada belirttiğim özellikler doğrultusunda kayıt ekranındakiler set ediliyor.
            // Bunuda veritabanından aldığımız id  sayesinde doğru alarmda değişiklikler yapıyor
            //her bilgi tek tek check ediliyor.
            haftaliktekrar.setChecked(alarmmodel.haftaliktekrar);
            pazar.setChecked(alarmmodel.getRepeatingDay(alarmmodel.SUNDAY));
            pazartesi.setChecked(alarmmodel.getRepeatingDay(alarmmodel.MONDAY));
            sali.setChecked(alarmmodel.getRepeatingDay(alarmmodel.TUESDAY));
            carsamba.setChecked((alarmmodel.getRepeatingDay(alarmmodel.WEDNESDAY)));
            persembe.setChecked(alarmmodel.getRepeatingDay(alarmmodel.THURSDAY));
            cuma.setChecked(alarmmodel.getRepeatingDay(alarmmodel.FRIDAY));
            cumartesi.setChecked(alarmmodel.getRepeatingDay(alarmmodel.SATURDAY));

            setextringtone ların birinde hata var
            secilenmelodi.setText(RingtoneManager.getRingtone(this,alarmmodel.alarmTone).getTitle(this));
            }

        LinearLayout ringTone = (LinearLayout) findViewById(R.id.alarm_ringtone_container);
        ringTone.setOnClickListener(new  View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT,false);//hiçbir şey seçilmediyse varsayılan olsun dedik
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI, Settings.System.DEFAULT_ALARM_ALERT_URI);//varsayılan melodiyi seçtik
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,RingtoneManager.TYPE_ALL);//bütün alarm müzikleri ve müzikler olabilir dedik
                startActivityForResult(intent,1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode== RESULT_OK){
            switch (requestCode){
                case 1:
                    //alarmtone uri tipindeydi alarm model safasından
                    //seçilen modelin set edilmesi uri olarak
                    alarmmodel.alarmTone=data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    secilenmelodi.setText(RingtoneManager.getRingtone(this,alarmmodel.alarmTone));
                    break;

                    default:
                        break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.alarmdetay, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){

            case R.id.kaydet:{

                updateModelFromLayout();
                AlarmManagerHelper.cancelAlarms(this);//hangi alarm iptal edildi hangisi tekrar kuruldu arka planda zor odugu için önce hepsini durduuyorum
                // bütün alarmları önce durduyoruz

                if(alarmmodel.id<0){
                    veritabani.createAlarm(alarmmodel);//yeni alarm oluştur
                }else{
                    veritabani.updateAlarm(alarmmodel);//zaten alarm var güncelle
                }
                AlarmManagerHelper.setAlarms(this);
                setResult(RESULT_OK);//işlem bitti başarılı bir şekilde devam et
                finish();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    //değişiklerin güncellenmesi
    //repeatinday[0veya1 veya...] = true yada false//
    private void updateModelFromLayout(){
        alarmmodel.timeHour = timepicker.getCurrentHour().intValue();
        alarmmodel.timeMinute = timepicker.getCurrentMinute().intValue();
        alarmmodel.name = editname.getText().toString();
        alarmmodel.haftaliktekrar = haftaliktekrar.isChecked();
        alarmmodel.setRepeatingDay(AlarmModel.SUNDAY,pazar.isChecked());
        alarmmodel.setRepeatingDay(AlarmModel.MONDAY,pazartesi.isChecked());
        alarmmodel.setRepeatingDay(AlarmModel.TUESDAY,sali.isChecked());
        alarmmodel.setRepeatingDay(AlarmModel.WEDNESDAY,carsamba.isChecked());
        alarmmodel.setRepeatingDay(AlarmModel.THURSDAY,persembe.isChecked());
        alarmmodel.setRepeatingDay(AlarmModel.FRIDAY,cuma.isChecked());
        alarmmodel.setRepeatingDay(AlarmModel.SATURDAY,cumartesi.isChecked());
        alarmmodel.isEnabled = true;
    }
}
