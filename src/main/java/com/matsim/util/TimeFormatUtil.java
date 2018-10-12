package com.matsim.util;

import java.util.regex.Pattern;

/**
 * Created by MingLU on 2018/7/3,
 * Life is short, so get your fat ass moving and chase your damn dream.
 */
public class TimeFormatUtil {


    public static boolean isHHMMSSformat(String time){
//        Pattern p = Pattern.compile("^(?:[01]\\d|2[0-3])(?::[0-5]\\d){2}$");
        Pattern p = Pattern.compile("((((0?[0-9])|([1][0-9])|([2][0-4]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
        return p.matcher(time).matches();
    }

    public static double hhmmss2Seconds(String hhmmss) {
        // if there is am pm in it
        String[] hms;
        double seconds = 0;
        if (hhmmss.contains( "pm" ) || hhmmss.contains( "PM" )) {
            hhmmss = hhmmss.replace( "pm", "" ).replace( " ", "" ).replace( "PM", "" );
            hms = hhmmss.split( ":" );
            double hour = Double.parseDouble( hms[0] );
            if (hour < 12) {
                seconds = 3600 * (Double.parseDouble( hms[0] ) + 12) + 60 * Double.parseDouble( hms[1] ) + Double.parseDouble( hms[2] );
            } else {
                seconds = 3600 * Double.parseDouble( hms[0] ) + 60 * Double.parseDouble( hms[1] ) + Double.parseDouble( hms[2] );
            }
//            System.out.println( hms[0] + "," + hms[1] + "," + hms[2] );
        } else  {
            hhmmss = hhmmss.replace( "am", "" ).replace( " ", "" ).replace( "AM", "" );
            hms = hhmmss.split( ":" );
            seconds = 3600 * Double.parseDouble( hms[0] ) + 60 * Double.parseDouble( hms[1] ) + Double.parseDouble( hms[2] );
        }
//        System.out.println( seconds );
        return seconds;
    }
}
