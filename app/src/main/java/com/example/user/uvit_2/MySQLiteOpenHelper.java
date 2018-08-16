package com.example.user.uvit_2;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.nfc.Tag;
import android.system.ErrnoException;
import android.util.Log;

//파일입출력관련
import java.io.*;
import java.io.FileInputStream;
import java.util.ArrayList;

import android.view.View;

/**
 * Created by user on 2016-08-17.
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    Activity ac;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "wiset2";
    private static final String TABLE_NAME = "records";
    private static final String RSS_TABLE_NAME = "rss_records";
    private static final String PROFILE_TABLE_NAME = "profile";
    private static final String VITAMIND_TABLE_NAME = "BASE_VITAMIND";
    private static final String SKINTYPE_TABLE_NAME = "BASE_SKINTYPE";
    private static final String TREND_TABLE_NAME = "trend_table"; //trend 그래프 관련 table

    int[][] skinTomed = {{1, 200}, {2, 250}, {3, 300}, {4, 450}, {5, 600}, {6, 1000}};
    int[][] ageTovitamind = {{1, 2, 200, 1200}, {3, 5, 200, 1500}, //>=,<
            {6, 8, 200, 1600}, {9, 11, 200, 2400},
            {12, 64, 400, 4000}, {65, 100, 600, 4000}};

    String TAG = "SQLite";

    ArrayList<String> smslist = new ArrayList<String>();


    public MySQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                "DATE BLOB, " +
                "TEMP REAL, " +
                "HUM REAL," +
                "UVI REAL," +
                "UVB REAL, " +
                "ACTION TEXT," +
                "VITAMIND REAL);";
        db.execSQL(sql);
//        1. NULL
//        2. INTEGER : 1, 2, 3, 4, 6, 8 bytes의 정수 값
//        3. REAL : 8bytes의 부동 소수점 값
//        4. TEXT : UTF-8, UTF-16BE, UTF-16LE 인코딩의 문자열
//        5. BLOB : 입력 된 그대로 저장


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    //밴드 데이터 INSERT
    public void addData(ValueObject vo) {
        ContentValues values = new ContentValues();
        values.put("DATE", vo.getTime());
        values.put("TEMP", vo.getTemp());
        values.put("HUM", vo.getHum());
        values.put("UVI", vo.getUvi());
        values.put("UVB", vo.getUvb());
        values.put("ACTION", vo.getAction());
        values.put("VITAMIND", vo.getVitamind());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_NAME, null, values);
//        Log.d(TAG, "insert 성공");
        db.close();
    }

    //기상 RSS 데이터 INSERT
    public void addRSSData(ValueObject vo) {
        ContentValues values = new ContentValues();

        values.put("DATE", vo.getDate());
        values.put("DAY", vo.getDay());
        values.put("HOUR", vo.getHour());
        values.put("RSSTEMP", vo.getRsstemp());
        values.put("SKY", vo.getSky());
        values.put("PTY", vo.getPty());
        values.put("POP", vo.getPop());
        values.put("R12", vo.getR12());
        values.put("S12", vo.getS12());
        values.put("WS", vo.getWs());
        values.put("WDKOR", vo.getWdKor());
        values.put("REH", vo.getReh());

        SQLiteDatabase db = getWritableDatabase();

        db.insert(RSS_TABLE_NAME, null, values);
        Log.i(TAG, "rss insert 성공");
        db.close();
    }

    //개인 프로파일 INSERT
    public void addProfileData(ValueObject vo) {
        ContentValues values = new ContentValues();
        values.put("NAME", vo.getName());
        values.put("AGE", vo.getAge());
        values.put("GENDER", vo.getGender());
        values.put("SKINTYPE", vo.getSkintype());
        values.put("EXPOSURE_UPPER", vo.getExposure_upper());
        values.put("EXPOSURE_LOWER", vo.getExposure_lower());
        values.put("TARGETS_VITAMIND_SUFFICIENT", vo.getTargets_vitamind_sufficient());
        values.put("TARGETS_VITAMIND_UPPERLIMIT", vo.getTargets_vitamind_upperlimit());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(PROFILE_TABLE_NAME, null, values);
        Log.i(TAG, "profile insert 성공");
    }

    public String[] getData() {
//        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE DATE=(SELECT MAX(DATE) FROM " + TABLE_NAME + ")";
        String sql = "SELECT * FROM " + TABLE_NAME + " LIMIT 1 OFFSET (SELECT COUNT(*) FROM " + TABLE_NAME + ")-1";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        String[] arr = new String[cursor.getColumnCount()]; //7
        arr[0] = cursor.getString(0);
        arr[1] = String.valueOf(cursor.getFloat(1));
        arr[2] = String.valueOf(cursor.getFloat(2));
        arr[3] = String.valueOf(cursor.getFloat(3));
        arr[4] = String.valueOf(cursor.getFloat(4));
        arr[5] = cursor.getString(5);
        arr[6] = String.valueOf(cursor.getFloat(6));
        // for (int i = 0; i < arr.length; i++) {          Log.d(TAG, i+" : "+arr[i]);        }
        db.close();
        return arr;
    }

    public String[] getProfileData() {
        String sql = "SELECT * FROM " + PROFILE_TABLE_NAME + " LIMIT 1 OFFSET (SELECT COUNT(*) FROM " + PROFILE_TABLE_NAME + ")-1";
        SQLiteDatabase db = getReadableDatabase();
        String[] arr = new String[8];
        try {

            Log.d(TAG, sql);
            Cursor cursor = db.rawQuery(sql, null);
            cursor.moveToFirst();
            arr[0] = cursor.getString(0);
            arr[1] = String.valueOf(cursor.getInt(1));
            arr[2] = cursor.getString(2);
            arr[3] = String.valueOf(cursor.getInt(3));
            arr[4] = String.valueOf(cursor.getInt(4));
            arr[5] = String.valueOf(cursor.getInt(5));
            arr[6] = String.valueOf(cursor.getInt(6));
            arr[7] = String.valueOf(cursor.getInt(7));
            for (int i = 0; i < arr.length; i++) {
                Log.d(TAG, "profile " + i + ":" + arr[i]);
            }
        } catch (Exception e) {
        } finally {
            db.close();
        }

        return arr;
    }

    public String[] getVitamindData(String ageText) {
        String sql = "select * from " + VITAMIND_TABLE_NAME + " WHERE " + ageText + ">=start_age and " + ageText + "<=end_age;";
        Log.d(TAG, sql);
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        String[] arr = new String[cursor.getColumnCount()]; //4
        arr[0] = String.valueOf(cursor.getInt(0));
        arr[1] = String.valueOf(cursor.getInt(1));
        arr[2] = String.valueOf(cursor.getInt(2));
        arr[3] = String.valueOf(cursor.getInt(3));
        for (int i = 0; i < arr.length; i++) {
            Log.d(TAG, arr[i]);
        }
        db.close();
        return arr;
    }

    public String getSkintypeData() {
        String[] arrProfile = getProfileData();
        String sql = "select * from " + SKINTYPE_TABLE_NAME + " where skin=" + arrProfile[3] + ";";
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        String[] arr = new String[cursor.getColumnCount()]; //2
        arr[0] = String.valueOf(cursor.getInt(0));
        arr[1] = String.valueOf(cursor.getInt(1));
        for (int i = 0; i < arr.length; i++) {
            Log.d(TAG, arr[i]);
        }
        db.close();
        return arr[1];
    }

    public String[] getRssData() {
        String sql = "SELECT * FROM  " + RSS_TABLE_NAME + " LIMIT 1 OFFSET (SELECT COUNT(*) FROM " + RSS_TABLE_NAME + ")-1";
        SQLiteDatabase db = getWritableDatabase();
        Log.d(TAG, sql);
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        String[] arr = new String[cursor.getColumnCount()]; //11
//        arr[0] = String.valueOf(cursor.getInt(3));
//        arr[1] = String.valueOf(cursor.getString(5));
//        arr[2] = String.valueOf(cursor.getInt(11));
        db.close();
        return arr;
    }


    public void basicTable() {
        SQLiteDatabase db = getWritableDatabase();

        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                "DATE BLOB PRIMARY KEY, " +
                "TEMP REAL, " +
                "HUM REAL," +
                "UVI REAL," +
                "UVB REAL, " +
                "ACTION TEXT," +
                "VITAMIND REAL);";
        db.execSQL(sql);

        String rss_sql = "CREATE TABLE IF NOT EXISTS " + RSS_TABLE_NAME + "(" +
                "DATE BLOB PRIMARY KEY, " +
                "DAY TEXT, " +
                "HOUR TEXT, " +
                "RSSTEMP REAL, " +
                "SKY TEXT, " +
                "PTY TEXT, " +
                "POP REAL, " +
                "R12 REAL, " +
                "S12 REAL, " +
                "WS REAL, " +
                "WDKOR TEXT, " +
                "REH REAL);";
        db.execSQL(rss_sql);

        String profile_sql = "CREATE TABLE IF NOT EXISTS " + PROFILE_TABLE_NAME + "(" +
                "NAME TEXT, " +
                "AGE INTEGER, " +
                "GENDER TEXT, " +
                "SKINTYPE INTEGER, " +
                "EXPOSURE_UPPER INTEGER," +
                "EXPOSURE_LOWER INTEGER," +
                "TARGETS_VITAMIND_SUFFICIENT INTEGER," +
                "TARGETS_VITAMIND_UPPERLIMIT INTEGER);";
        db.execSQL(profile_sql);

        String basic_sql = "CREATE TABLE IF NOT EXISTS " + VITAMIND_TABLE_NAME + "(" +
                "START_AGE INTEGER, " +
                "END_AGE INTEGER," +
                "VITAMIND_SUFFICIENT INTEGER, " + // 비타민 D 충분섭춰량
                "VITAMIND_UPPERLIMIT INTEGER);"; // 비타민 D 상한섭취량
        db.execSQL(basic_sql);

        String basic_sql2 = "CREATE TABLE IF NOT EXISTS " + SKINTYPE_TABLE_NAME + "(" +
                "SKIN INTEGER, " +
                "MED INTEGER);";
        db.execSQL(basic_sql2);

        String trend_sql = "CREATE TABLE IF NOT EXISTS " + TREND_TABLE_NAME + "(" +
                "YEAR TEXT NOT NULL, " + //YEAR,MONTH,DAY 문자타입
                "MONTH TEXT NOT NULL," +
                "DAY TEXT NOT NULL," +
                "HOUR TEXT NOT NULL," +
                "UVI REAL NOT NULL," + //UVI, VITAMIN float형
                "VITAMIN REAL NOT NULL);";
        db.execSQL(trend_sql);


        addBasicData();
    }

    public void addBasicData() {
        addRecods(TABLE_NAME);
        addProfile(PROFILE_TABLE_NAME);
        addVitamind(VITAMIND_TABLE_NAME);
        addSkintype(SKINTYPE_TABLE_NAME);
        addRss(RSS_TABLE_NAME);
    }

    private void addRecods(String tableName) {
        ContentValues values = new ContentValues();
        values.put("DATE", "동기화 버튼을 누르세요");
        values.put("TEMP", 0);
        values.put("HUM", 0);
        values.put("UVI", 0);
        values.put("UVB", 0);
        values.put("ACTION", "낮음");
        values.put("VITAMIND", 0);
        SQLiteDatabase db = getWritableDatabase();

        db.insert(tableName, null, values);
        Log.i(TAG, tableName + " 성공");
        db.close();

    }

    private void addProfile(String profileTableName) {
        ContentValues values = new ContentValues();
        values.put("NAME", "이름을 입력하세요.");
        values.put("AGE", 20);
        values.put("GENDER", "남");
        values.put("SKINTYPE", 3);
        values.put("EXPOSURE_UPPER", 25);
        values.put("EXPOSURE_LOWER", 25);
        values.put("TARGETS_VITAMIND_SUFFICIENT", 400);
        values.put("TARGETS_VITAMIND_UPPERLIMIT", 4000);
        SQLiteDatabase db = getWritableDatabase();
        Log.i(TAG, String.valueOf(values));

        db.insert(profileTableName, null, values);
        Log.i(TAG, profileTableName + " 성공");
        db.close();

        String[] profile = getProfileData();
        for (int i = 0; i < profile.length; i++) {
            Log.d(TAG, profile[i]);
        }
    }

    private void addVitamind(String vitamindTableName) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        for (int i = 0; i < ageTovitamind.length; i++) {
            values.put("START_AGE", ageTovitamind[i][0]);
            values.put("END_AGE", ageTovitamind[i][1]);
            values.put("VITAMIND_SUFFICIENT", ageTovitamind[i][2]);
            values.put("VITAMIND_UPPERLIMIT", ageTovitamind[i][3]);
            db.insert(vitamindTableName, null, values);
        }
        Log.i(TAG, vitamindTableName + " 성공");
        db.close();
    }

    private void addSkintype(String skintypeTableName) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        for (int i = 0; i < skinTomed.length; i++) {
            values.put("SKIN", skinTomed[i][0]);
            values.put("MED", skinTomed[i][1]);
            db.insert(skintypeTableName, null, values);
        }
        Log.i(TAG, skintypeTableName + " 성공");
        db.close();
    }

    private void addRss(String RSS_TABLE_NAME) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
//        for (int i = 0; i < skinTomed.length; i++) {
//            values.put("SKIN", skinTomed[i][0]);
//            values.put("MED", skinTomed[i][1]);
//            db.insert(RSS_TABLE_NAME, null, values);
//        }
        Log.i(TAG, RSS_TABLE_NAME + " 성공");
        db.close();
    }

    /******************
     * Trend
     ***********************/
    public void addTrendData(ValueObject vo) {
        ContentValues values = new ContentValues();
        values.put("YEAR", vo.getTrendYear());
        values.put("MONTH", vo.getTrendMonth());
        values.put("DAY", vo.getTrendDay());
        values.put("HOUR", vo.getTrendHour());
        values.put("UVI", vo.getUvi());
        values.put("VITAMIND", vo.getVitamind());


        SQLiteDatabase db = getWritableDatabase();
        db.insert(TREND_TABLE_NAME, null, values);


        db.close();
    }

    public float[] getTrendData(String chartType, String type, String year, String month, String day) {
        float max = 0.0f, maxTmp = 0.0f, tmp = 0.0f;
        String sql = ""; //selectQuery문

        //무조건 TEXT 비교해 주려면 'ABC' 이런형태여야 함
        String Nyear = "'" + year + "'";
        String Nmonth = "'" + month + "'";
        String Nday = "'" + day + "'";

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor;

        float[] arr = new float[getDayNum(type)];
        for (int i = 0; i < getDayNum(type); i++) { //초기화
            arr[i] = 0.0f;
        }


        for (int i = 0; i < getDayNum(type); i++) {
            sql = ""; //sql 초기화


            if (type.equals("DAY")) { //default값 day
                //나중에 달력 옮길려면 getDay()이부분 바꾸면 됨
                String sql2 = "(SELECT * FROM  " + TREND_TABLE_NAME + " where (YEAR = " + Nyear + ") AND (MONTH = " + Nmonth + "))";
                sql = "SELECT * FROM  " + sql2 + " where (DAY = " + Nday + ") AND (HOUR = " + "'" + i + "'" + ");";
            } else if (type.equals("WEEK")) {
                int num = Integer.parseInt(day) - 6 + i;
                String sql2 = "(SELECT * FROM  " + TREND_TABLE_NAME + " where (DAY = " + "'" + num + "'" + ") AND (MONTH = " + Nmonth + "))";
                sql = "SELECT * FROM  " + sql2 + " where (YEAR = " + Nyear + ");";
            } else if (type.equals("MONTH")) {
                String sql2 = "(SELECT * FROM  " + TREND_TABLE_NAME + " where (DAY = " + "'" + i + "'" + ") AND (MONTH = " + Nmonth + " ))";
                sql = "SELECT * FROM  " + sql2 + " where (YEAR = " + Nyear + ");";
            } else if (type.equals("YEAR")) { //year
                int num = Integer.parseInt(month);
                sql = "SELECT * FROM  " + TREND_TABLE_NAME + " where MONTH = " + "'" + i + "'" + " AND YEAR = " + Nyear + ";";

                if (num == 2) {
                    if (i == 28) break;
                } //2월
                if (num == 4 || num == 6 || num == 9 || num == 11) { //4,6,9,11월
                    if (i == 30) break;
                }
            }


            cursor = db.rawQuery(sql, null);


            if (cursor != null && !cursor.isClosed()) {
                if (cursor.moveToFirst()) {
                    do {

                        if (chartType.equals("UVI")) maxTmp = cursor.getFloat(4); //uvi은 인덱스 4
                        else if (chartType.equals("VITAMIN"))
                            maxTmp = cursor.getFloat(5); //vitamin은 인덱스 4

                        if (maxTmp > max) {
                            tmp = max;
                            max = maxTmp;
                            maxTmp = tmp;

                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }

            arr[i] = max;
        }

        return arr;
    }

//    private void trendUnit() { //trendTable 초기화
//
//        ContentValues values = new ContentValues();
//        SQLiteDatabase db = getWritableDatabase();
//
//        db.execSQL( "INSERT INTO trend_table VALUES ('0','0', '0', '0', 0.0, 0.0 );" );
//    }

    //현재까지 수집된 데이터가 없어 insert 해주는 메소드
    public void inputIndex() {
//        ContentValues values = new ContentValues();
//        SQLiteDatabase db = getWritableDatabase();
//
//
//        //  트랜잭션을 이용해 빠르게 삽입하는 방법
//        db.beginTransaction();
//        try {
//            for (int i = 0; i < smslist.size(); i++) {
//                db.execSQL(smslist.get(i));
//            }
//            db.execSQL("INSERT INTO trend_table VALUES ('2016','10', '22', '0', 13.0, 100.0 );");
//
//        } catch (Exception e) {
//            Log.i("InputData:", e.toString());
//        } finally {
//        }

    }

    public void readFromFile(MainActivity mainActivity) {
//        String[] arrProfile = getProfileData();
////        String sql = "select * from " + SKINTYPE_TABLE_NAME + " where skin=" + arrProfile[3] + ";";
//        String  sql="select * from trend_table;";
//        SQLiteDatabase db = getWritableDatabase();
//        Cursor cursor = db.rawQuery(sql, null);
//        Log.d(TAG, String.valueOf(cursor.getColumnCount()));
//        cursor.moveToFirst();
//        String[] arr = new String[cursor.getColumnCount()]; //2
//        arr[0] = String.valueOf(cursor.getString(0));
//        arr[1] = String.valueOf(cursor.getString(1));
//        arr[2] = String.valueOf(cursor.getString(2));
//        arr[3] = String.valueOf(cursor.getString(3));
//        arr[4] = String.valueOf(cursor.getString(4));
//        arr[5] = String.valueOf(cursor.getString(5));
//        for (int i = 0; i < arr.length; i++) {
//            Log.d(TAG, arr[i]);
//        }
//        db.close();


//            db.execSQL("delete from trend_table where year="+2016+" or year="+2015+" ;");
//
        try {
            Resources r = mainActivity.getResources();
            InputStream is = r.openRawResource(R.raw.query);
            Log.d(TAG, String.valueOf(is));
            InputStreamReader r2 = new InputStreamReader(is);
            StringBuilder str = new StringBuilder();
            Log.d(TAG, String.valueOf(r2));

            BufferedInputStream bt = new BufferedInputStream(is);

            while (true) {
                int i = r2.read();
                if (i == -1) {
                    break;
                } else {
                    char c = (char) i;
                    str.append(c);
                }
            }
            String[] strs = str.toString().split("\n");
            SQLiteDatabase db = getWritableDatabase();

            for (int i = 0 ; i < strs.length; i++) {
                Log.i(TAG, strs[i]);
                String sql=strs[i];
                db.execSQL(sql);

            }

        } catch (IOException io) {
            io.printStackTrace();
        }
    }


    public int getDayNum(String type) {
        String dayType = "";
        if (type.equals("DAY")) return 24;  //day는 시간별로 max값 보여주어야함
        else if (type.equals("WEEK")) return 7; //week은 일별로 즉 일주일에는 7일이니까 값이 7
        else if (type.equals("MONTH"))
            return 31;// month는 일별로 즉 한달에 30일 또는 31일이니 31. (최대 31일이니까 31로 지정함)
        else return 12; //1년에 12달이 있어서 12
    }

}
