package gr.androidapp.stekionair;

import java.util.Calendar;

public class GetTime {

    static String cDay ="";


    public static String getTime(){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        if (minutes<10)
            return String.valueOf(hour)+"0"+String.valueOf(minutes);
        return String.valueOf(hour)+String.valueOf(minutes);
    }

    public static void getDay(){
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        switch (day) {
            case Calendar.SUNDAY:
                cDay ="Sunday";
                break;
            case Calendar.MONDAY:
                cDay ="Monday";
                break;
            case Calendar.TUESDAY:
                cDay ="Tuesday";
                break;
            case Calendar.WEDNESDAY:
                cDay ="Wednesday";
                break;
            case Calendar.THURSDAY:
                cDay ="Thursday";
                break;
            case Calendar.FRIDAY:
                cDay ="Friday";
                break;
            case Calendar.SATURDAY:
                cDay ="Saturday";
                break;
        }
    }
}
