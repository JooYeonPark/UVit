package com.example.user.uvit_2;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by user on 2016-10-06.
 */
public class ValueObject{
    private String TAG = "vo";

        private String ctime;
        //모니터링 변수
        String time;         double temp, hum, uvi, uvb;
        String action;       double vitamind;
        String syncheck;

        // 위치 GPS 변수
        double lat, lon;     String address;

        // 기상 RSS 변수
        String rsstime, day, hour;     double rsstemp;
        String sky, pty;      double pop, r12, s12, ws;
        String wdKor;          double reh;

        // 개인 프로파일 변수
        String name, gender;
        int age, skintype, exposure_upper, exposure_lower, targets_vitamind_sufficient,targets_vitamind_upperlimit;

        // 알람 변수
        int sd_temp, sd_hum, sd_uvi, sd_vitamind, rss_temp, rss_hum;
        boolean rss_switch;

        //트렌드 변수
        String trend_year, trend_month, trend_day, trend_hour;

        public ValueObject(double temp, double hum, double uvi, double uvb, String action, double vitamind) {

            // 측정된 time
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ctime = format.format(new Date());

            this.time = ctime;
            this.temp = temp;
            this.hum = hum;
            this.uvi = uvi;
            this.uvb = uvb;
            this.action = action;
            this.vitamind = vitamind;

            String arr[] = ctime.split("-"); //년, 월, 일+시간
            String arr2[] = arr[2].split(" "); //일, 시간
            String arr3[] = arr2[1].split(":"); //시, 분, 초

//        for (int i = 0; i < arr.length; i++)            Log.d(TAG, arr[i]);        Log.d(TAG, "------------------------");
//        for (int i = 0; i < arr2.length; i++)            Log.d(TAG, arr2[i]);       Log.d(TAG, "------------------------");
//        for (int i = 0; i < arr3.length; i++)            Log.d(TAG, arr3[i]);        Log.d(TAG, "------------------------");

            int hour = Integer.parseInt(arr3[0]);
            int minute = Integer.parseInt(arr3[1]);
            int second = Integer.parseInt(arr3[2]);
//        if (hour == 23 && minute >= 50 && second >= 50) {vitamind=0; Log.d(TAG, hour + ":" + minute + ":" + second+" 비타민 D RESET");}
            if (hour == 23 && minute >= 50 && second >= 50) { this.vitamind=0; Log.d(TAG, hour + ":" + minute + ":" + second+" 비타민 D RESET");}

            //Trend_table을 위해 필요한 변수들 세팅
            // 측정된 time
            format = new SimpleDateFormat("yyyy-MM-dd");
            ctime = format.format(new Date());


            String[] dateResult = ctime.split("-");
        /*  dateResult[0] : YEAR
            dateResult[1] : MONTH
            dateResult[2] : DAY  */

            trend_year = dateResult[0];
            trend_month = dateResult[1];
            trend_day = dateResult[2];

            format =  new SimpleDateFormat("HH:mm:ss");
            ctime = format.format(new Date());

            String[] hourResult = ctime.split(":");
        /*  dateResult[0] : 5시
            dateResult[1] : 20분
            dateResult[2] : 30초  */

            trend_hour = dateResult[0];
        }

        public ValueObject(String push){
            this.syncheck = push;
        }

        public ValueObject(double lat, double lon, String address){
            this.lat = lat;
            this.lon = lon;
            this.address = address;
        }

        public ValueObject(String day, String hour, double rsstemp, String sky, String pty, double pop, double r12, double s12, double ws, String wdKor, double reh){

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ctime = format.format(new Date());

            this.rsstime = ctime;
            this.day = day;        this.hour = hour;
            this.rsstemp = rsstemp;        this.sky = sky;
            this.pty = pty;        this.pop = pop;
            this.r12 = r12;        this.s12 = s12;
            this.ws =   ws;        this.wdKor = wdKor;
            this.reh = reh;
        }

        public ValueObject(String name, int age, String gender, int skintype, int ex_upper, int ex_lower, int targets_vitamind_sufficient, int targets_vitamind_upperlimit){
            this.name = name;
            this.age = age;
            this.gender = gender;
            this.skintype = skintype;
            this.exposure_upper = ex_upper;
            this.exposure_lower = ex_lower;
            this.targets_vitamind_sufficient = targets_vitamind_sufficient;
            this.targets_vitamind_upperlimit = targets_vitamind_upperlimit;
        }

        public ValueObject(int sd_temp, int sd_hum, int sd_uvi,int sd_vitamind, int rss_temp, int rss_hum, boolean rss_switch){
            this.sd_temp = sd_temp;
            this.sd_hum = sd_hum;
            this.sd_uvi = sd_uvi;
            this.sd_vitamind = sd_vitamind;
            this.rss_temp = rss_temp;
            this.rss_hum = rss_hum;
            this.rss_switch = rss_switch;
        }

        public String getTime() {return time;}
        public double getTemp() {return temp;}
        public double getHum() {return hum;}
        public double getUvi() {return uvi;}
        public double getUvb() {return uvb;}
        public String getAction() {return action;}
        public double getVitamind() {return vitamind;}

        public String getSyncheck() {return syncheck;}

        public double getLat () { return lat;}
        public double getLon () { return lon;}
        public String getAddress () { return address;}

        public String getDate() {return rsstime;}
        public String getDay() {return day;}
        public String getHour() {return hour;}
        public double getRsstemp() {return rsstemp;}
        public String getSky() {return sky;}
        public String getPty() {return pty;}
        public double getPop() {return pop;}
        public double getR12() {return r12;}
        public double getS12() {return s12;}
        public double getWs() {return ws;}
        public String getWdKor() {return wdKor;}
        public double getReh() {return reh;}

        public String getName() {return name;}
        public int getAge() {return age;}
        public String getGender() {return gender;}
        public int getSkintype() {return skintype;}
        public int getExposure_upper() {return exposure_upper;}
        public int getExposure_lower() {return exposure_lower;}
        public int getTargets_vitamind_sufficient() {return  targets_vitamind_sufficient;}
        public int getTargets_vitamind_upperlimit() {return  targets_vitamind_upperlimit;}

        public int getSd_temp() {return sd_temp;}
        public int getSd_hum() {return sd_hum;}
        public int getSd_uvi() {return sd_uvi;}
        public int getSd_vitamind() {return sd_vitamind;}
        public int getRss_temp() {return rss_temp;}
        public int getRss_hum() {return rss_hum;}
        public boolean getRss_switch() {return rss_switch;}

        public String getTrendYear() {
            return trend_year;
        }

        public String getTrendMonth() {
            return trend_month;
        }

        public String getTrendDay() {
            return trend_day;
        }

        public String getTrendHour(){
            return trend_hour;
        }
}

