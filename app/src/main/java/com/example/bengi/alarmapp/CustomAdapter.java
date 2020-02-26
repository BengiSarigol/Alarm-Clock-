package com.example.bengi.alarmapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class CustomAdapter extends BaseAdapter {

    private Context context;
    private List<AlarmModel> modelList;

    //diğer sayfalardan gönderildiğinde context ve modellist olarak alıcak değer atılıcak
    public CustomAdapter(Context context, List<AlarmModel> modelList){
        this.context = context;
        this.modelList =modelList;
    }

    //mainactivity tarafından çağırılıyordu.Varolan alarmın güncellenmesi veya yeni alarm durumund çağırılır
    public void setAlarms(List<AlarmModel> alarms){
        modelList = alarms;
    }

    @Override
    public int getCount() {
        if(modelList != null){

            return modelList.size();//listviewdeki elemanların sayısı
        }
        return 0;
    }
    @Override
    public Object getItem(int position) {
        if(modelList != null){
            return  modelList.get(position);
        }
            return null;
    }

    @Override
    public long getItemId(int position) {
        if(modelList != null){
            //list viewe tıklanma olayında id bilgisi getıtemıd sayseinde istenen yer eiletiliyor
            return  modelList.get(position).id;
        }
            return 0;
    }

    //
    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        if(modelList != null){
            AlarmModel model = (AlarmModel) getItem(position);//her bir liste elemenını bilgisini getıtem ile alıp model de tutuyorum
            //ve bu sayede liste elemanlarım ile alarmmodeldeki fonksiyonlarımı kullanıcam
            LinearLayout container = (LinearLayout) ((Activity) context).getLayoutInflater().inflate(R.layout.custom_alarm_listesi, null);
              //her bir liste elemanı için linearlayout özelliğiyle tanımladığımız xml sayfasını kullanıcaz
            //hep set işlemi kullanıcaz çünkü ekrana verileri alıp set edicez

            TextView texttime = (TextView) container.findViewById(R.id.alarm_zamani);
            texttime.setText(String.format("%02d : %02d" , model.timeHour,model.timeMinute));
            //model adı zamanının listeden çekilip ekrana yazdırılması
            TextView textname = (TextView) container.findViewById(R.id.alarm_ismi);
            textname.setText(model.name);

            //switchlerin on ve off olma durumu renklendirilmesi
            updateTextColor((TextView) container.findViewById(R.id.alarm_pazar),model.getRepeatingDay(AlarmModel.SUNDAY));
            updateTextColor((TextView) container.findViewById(R.id.alarm_pazartesi),model.getRepeatingDay(AlarmModel.MONDAY));
            updateTextColor((TextView) container.findViewById(R.id.alarm_sali),model.getRepeatingDay(AlarmModel.TUESDAY));
            updateTextColor((TextView) container.findViewById(R.id.alarm_carsamba),model.getRepeatingDay(AlarmModel.WEDNESDAY));
            updateTextColor((TextView) container.findViewById(R.id.alarm_persembe),model.getRepeatingDay(AlarmModel.THURSDAY));
            updateTextColor((TextView) container.findViewById(R.id.alarm_cuma),model.getRepeatingDay(AlarmModel.FRIDAY));
            updateTextColor((TextView) container.findViewById(R.id.alarm_cumartesi),model.getRepeatingDay(AlarmModel.SATURDAY));
            //gtrepeating day true yada false döndürüyordu

            SwitchCompat alarmOnOff = (SwitchCompat) container.findViewById(R.id.alarmonoff);
            alarmOnOff.setChecked(model.isEnabled);
            alarmOnOff.setTag(Long.valueOf(model.id));//etiket ekledik.model id'yi long turune çevirip
            alarmOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {//etiketin true mu false mu kontrolu
                    ((MainActivity) context).setAlarmEnabled(((Long) buttonView.getTag()).longValue(), isChecked);//main activityde ki setalarmenableda gönderiiliyor.
                }
            });


            container.setTag(Long.valueOf(model.id));
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) context).startAlarmDetailsActivity(((Long) v.getTag()).longValue());

                    }
            });
            container.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ((MainActivity) context).deleteAlarm(((Long) v.getTag()).longValue());
                    return true;
                }
            });
            return container;
        }
            return null;
    }

    private void updateTextColor(TextView view, boolean isOn){
        if(isOn){ //açıksa switch
            view.setTextColor(context.getResources().getColor(R.color.colorAccent));
        }else{
            view.setTextColor(Color.GRAY);
        }
    }
}
