package com.example.bengi.alarmapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import java.util.Calendar;
import java.util.List;

public class AlarmManagerHelper extends BroadcastReceiver {
    //bütün alarmlar telefon kapatıldığı zaman iptal ediliyor yani aktif olanlar pasif oluyor.
    //o yüzden her açıldığında aktif olması için tekrar bir algılayıcı yazıyoruz(broadcast receiver) bunun için manifest dosyasına
    //yazılan komuttan sonra her açılışta tetiklenicek bu sayfa
    //sadece açılıp kapanınca değil normalde de kullanılabilir bir sayfa

    public static final String  ID = "id";
    public static final String  NAME ="name";
    public static final String  TIME_HOUR ="timeHour";
    public static final String  TIME_MINUTE = "timeMinute";
    public static final String  TONE = "alarmtone";

    @Override
    public void onReceive(Context context, Intent intent) {//burada intent  servisler ve yayın algılayıcılaın haberleşmesini sağlar
        setAlarms(context);//alarmları setet.
    }

    public static void setAlarms(Context context){

        cancelAlarms(context);
        Veritabani veritabani = new Veritabani(context);
        List<AlarmModel> alarms = veritabani.getAlarms();

        if (alarms != null) {
            for (AlarmModel alarm : alarms) {
                if (alarm.isEnabled) {

                    PendingIntent pIntent = createPendingIntent(context, alarm);

                    //şimdiki zaman değerlerini alıyorum
                    final int nowDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                    final int nowHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                    final int nowMinute = Calendar.getInstance().get(Calendar.MINUTE);
                    boolean alarmSet = false;

                    Calendar calendar = Calendar.getInstance();//takvim nesnesi oluşturuyorum
                    calendar.set(Calendar.HOUR_OF_DAY, alarm.timeHour);
                    calendar.set(Calendar.MINUTE, alarm.timeMinute);
                    calendar.set(Calendar.SECOND, 00);

                    //burada günler 1den başlıyor.
                    for (int dayOfWeek = Calendar.SUNDAY; dayOfWeek <= Calendar.SATURDAY; ++dayOfWeek) {
                        //alarm = alarmmodel sınıfından
                        //istenen şimdiki zamandan geride bir zamana alarm kurulmuş olmasın bunun için bütün if maddelerii yazılır
                        //o günün alarmı etkin mi ve haftanın günü bu günün(gerçek zaman) eşit veya büyük mü.....
                        if (alarm.getRepeatingDay(dayOfWeek - 1) && dayOfWeek >= nowDay &&
                                !(dayOfWeek == nowDay && alarm.timeHour < nowHour) &&
                                !(dayOfWeek == nowDay && alarm.timeHour == nowHour && alarm.timeMinute <= nowMinute)) {
                            calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);

                            setAlarm(context, calendar, pIntent);//artık alarm kurulabilir
                            alarmSet = true;//yani alarmset edildi anlasılsım diye
                            break;//ilk günümüze en yakın alarm kurulup break ile durduruyoruz;
                        }
                    }

                    //eğer geçmişte kalan alarm varsa
                    if (!alarmSet) {
                        for (int dayOfWeek = Calendar.SUNDAY; dayOfWeek <= Calendar.SATURDAY; ++dayOfWeek) {
                            //haftalıktekrar et  butonu etkinse o alarm için alarmı silme güncelle burada yazmak gerekli değil çünkü kurulumu yok kontrolu var
                            if (alarm.getRepeatingDay(dayOfWeek - 1) && dayOfWeek <= nowDay && alarm.haftaliktekrar) {
                                calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
                                calendar.add(Calendar.WEEK_OF_YEAR, 1);//takvime hafta ekliyoruz

                                setAlarm(context, calendar, pIntent);
                                alarmSet = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

           //alarmlar pil ömrü için her saniye çalısmazlar.
    private static void setAlarm(Context context, Calendar calendar, PendingIntent pIntent) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            alarmManager.setWindow(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),50000 ,pIntent);//setwindow=alarm enfazla 50snye ye kadar gecikebilir
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);//cihaz uyuyorsa uyandır
        }
    }

    private  static PendingIntent createPendingIntent(Context context , AlarmModel model){
        Intent intent =new Intent(context, AlarmService.class);//uuygulama kapalı olsa bile çalısır serviceler
        //alarm ekranında yzıcaklar
        intent.putExtra(ID, model.id);
        intent.putExtra(NAME, model.name);
        intent.putExtra(TIME_HOUR, model.timeHour);
        intent.putExtra(TIME_MINUTE, model.timeMinute);
        intent.putExtra(TONE, model.alarmTone.toString());
       // PendingIntent kullanılarak asenkron işlem başlatılımı yapılır.FLAG_UPDATE_CURRENT ile şunu söylüyoruz. Arka arkaya birden fazla notification gelirse eğer, sen en son geleni dikkate al sadece.
        // Diğerleri önemli değil. En son hariç diğer notification a tıkladığımızda en son gelen parametrelere göre işlem yap diyoruz.
        //pendingintent zaten varsa, onu saklar ancak eger aynı işlemi yapan varsa değiştirir.
       // bildirimleri ele almak için bir mantığa sahipsiniz, ancak ne zaman alacağınızı gerçekten bilmiyorsunuz. Böylece, bir
        // PendingIntent kullanarak ve bildirim tıklatıldığında hangi kodun yürütülmesi gerektiğini belirten bir hizmet başlatırsınız.
        PendingIntent pi = PendingIntent.getService(context, (int) model.id,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        return pi;
    }
    public static void cancelAlarms(Context context){
        Veritabani veritabani = new Veritabani(context);
        List<AlarmModel> alarms = veritabani.getAlarms();
        if(alarms != null){
            for(AlarmModel alarm : alarms){
                if(alarm.isEnabled){ //alarmın switch kısmı aktif se
                    PendingIntent pIntent = createPendingIntent(context, alarm);//ıntentın zamanlanmış halidir.
                    AlarmManager alarmManager   = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.cancel(pIntent);
                }
            }
        }


    }

}


