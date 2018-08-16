package com.example.user.uvit_2;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.LoggingPermission;

import static java.lang.Math.*;
/**
 * Created by user on 2016-10-06.
 */
public class RssService {
    String TAG = "RssService";

    private Activity mActivity;
    MySQLiteOpenHelper dbHelper;

    double RE = 6371.00877; // 지구 반경(km)
    double GRID = 5.0; // 격자 간격(km)
    double SLAT1 = 30.0; // 투영 위도1(degree)
    double SLAT2 = 60.0; // 투영 위도2(degree)
    double OLON = 126.0; // 기준점 경도(degree)
    double OLAT = 38.0; // 기준점 위도(degree)
    double XO = 43; // 기준점 X좌표(GRID)
    double YO = 136; // 기1준점 Y좌표(GRID)
    private static String urlStr;


    public RssService(Activity ac) {
        mActivity = ac; //앱티비티 저장
    }

    public String getGrid(double lat, double lon) {
        double DEGRAD = PI / 180.0;        // double RADDEG = 180.0 / Math.PI;

        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olon = OLON * DEGRAD;
        double olat = OLAT * DEGRAD;

        double sn = tan(PI * 0.25 + slat2 * 0.5) / tan(PI * 0.25 + slat1 * 0.5);
        sn = log(cos(slat1) / cos(slat2)) / log(sn);
        double sf = tan(PI * 0.25 + slat1 * 0.5);
        sf = pow(sf, sn) * cos(slat1) / sn;
        double ro = tan(PI * 0.25 + olat * 0.5);
        ro = re * sf / pow(ro, sn);
        Map<String, Object> map = new HashMap<String, Object>();
        double ra = tan(PI * 0.25 + (lat) * DEGRAD * 0.5);
        ra = re * sf / pow(ra, sn);
        double theta = lon * DEGRAD - olon;
        if (theta > PI)
            theta -= 2.0 * PI;
        if (theta < -PI)
            theta += 2.0 * PI;
        theta *= sn;

        int gridx = (int) floor(ra * sin(theta) + XO + 0.5);
        int gridy = (int) floor(ro - ra * cos(theta) + YO + 0.5);
        String text = "http://www.kma.go.kr/wid/queryDFS.jsp?gridx=" + gridx + "&gridy=" + gridy;
        //기상청 x,y를 사용하여 xml주소 만들기
        Log.d(TAG, text);
        parseRss(text);//xml주소의 내용 파싱하기
        return text;

    }

    public String[][] parseRss(String xml) {

        String[][] rssArr = new String[18][11];
        Integer i = -1;

        try {
            URL url = new URL(xml);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();//url 열기
            if (urlConnection == null) {
            }
            urlConnection.setConnectTimeout(10000);//최대 10초 대기
            urlConnection.setUseCaches(false);//매번 서버에서 읽어오기

            String[] dayArr = new String[]{"오늘", "내일", "모레"};
            String[] skyArr = new String[]{"맑음", "구름조금", "구름많음", "흐림"};
            String[] ptyArr = new String[]{"맑음", "비", "비/눈", "눈"};
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) { //url코드가 HTTP기 맞으면
                InputStream inputStream = urlConnection.getInputStream(); //
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance(); //
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();

                String str = inputStream.toString();
                Log.d(TAG, str);
                xpp.setInput(inputStream, "utf-8");//웹서버에서 utf-8로 문자처리
                int eventType = xpp.getEventType();

                boolean hourTag = false;//hour 태그 처리 여부
                List<String> fieldList = new ArrayList<String>();
                fieldList.add("day");       //0  // 오늘, 내일, 모레
                fieldList.add("hour");      //1 // 동네예보 3시간 단위
                fieldList.add("temp");      //2 // 현재시간온도
                fieldList.add("sky");       //3 // 하늘상태코드 맑음=1. 구름조금=2, 구름많음=3, 흐림=4
                fieldList.add("pty");       //4 // 강수상태코드 맑음=0, 비=1, 비/눈=2, 눈=3
                fieldList.add("pop");       //5 // 강수확율 %
                fieldList.add("r12");       //6 // 12시간 예상 강수량
                fieldList.add("s12");       //7 // 12시간 예상 적설량
                fieldList.add("ws");        //8 // 풍속(m/s)
                fieldList.add("wdKor");     //9 // 풍향 한국어
                fieldList.add("reh");       //10 // 습도
                int fieldIndex = -1;//태그의 순서.START_TAG에서 검색,저장.
                //TEXT

//                START_DOCUMENT 이벤트는 문서의 시작을
//                END_DOCUMENT 은 문서의 끝을
//                START_TAG는 태그의 시작을 (예 : <data> )
//                END_TAG는 태그의 끝을 (예 : </data> )
//                TEXT는 태그의 시작과 끝 사이에서 나타난다. (예 : <data>여기서 TEXT 이벤트 발생</data> )

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_DOCUMENT) {
                        //System.out.println("Start document");
                    } else if (eventType == XmlPullParser.START_TAG) {
                        String stag = xpp.getName();
                        fieldIndex = fieldList.indexOf(stag);//미등록 태그는 -1로 처리

                    } else if (eventType == XmlPullParser.END_TAG) {
                    } else {
                        if (eventType == XmlPullParser.TEXT) {
                            String text = xpp.getText();
                            switch (fieldIndex) {
                                case 0://day태그
                                    int dayIndex = Integer.parseInt(text);
                                    rssArr[i][0] = dayArr[dayIndex];
                                    break;
                                case 1://hour태그
                                    i++;
                                    int hourIndex = Integer.parseInt(text);
                                    rssArr[i][1] = String.format("%d시부터%d시", hourIndex - 3, hourIndex);
                                    break;
                                case 2://temp태그
                                    rssArr[i][2] = text;
                                    break;
                                case 3://sky태그
                                    int skyIndex = Integer.parseInt(text);
                                    rssArr[i][3] = skyArr[skyIndex - 1];
                                    break;
                                case 4://pty태그
                                    int ptyIndex = Integer.parseInt(text);
                                    rssArr[i][4] = ptyArr[ptyIndex];
                                    break;
                                case 5://pop태그
                                    rssArr[i][5] = text;
                                    break;
                                case 6://r12태그
                                    rssArr[i][6] = text;
                                    break;
                                case 7://s12태그
                                    rssArr[i][7] = text;
                                    break;
                                case 8://ws태그
                                    rssArr[i][8] = text.substring(0, 3);
                                    break;
                                case 9://wdKor태그
                                    rssArr[i][9] = text;
                                    break;
                                case 10://reh태그
                                    rssArr[i][10] = text;
                                    break;
                            }//end switch
                            fieldIndex = -1;
                            hourTag = false;
                        }
                    }
                    eventType = xpp.next();
                }//while의 끝
                inputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rssArr;
    }

    public Date nextTime() {
        GregorianCalendar cal = new GregorianCalendar();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DATE);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        switch (hour) {
            case 1:            case 2:            case 3:                hour = 4;                break;
            case 4:            case 5:            case 6:                hour = 7;                break;
            case 7:            case 8:            case 9:                hour = 10;                break;
            case 10:            case 11:            case 12:                hour = 13;                break;
            case 13:            case 14:            case 15:                hour = 16;                break;
            case 16:            case 17:            case 18:                hour = 19;                break;
            case 19:            case 20:            case 21:                hour = 22;                break;
            case 22:            case 23:                hour = 1;                day++;                break;
            case 0:                hour = 1;                break;
        }
        cal.set(year, month, day, hour, 0, 0);
        return cal.getTime();
    }

    public void addRSSData(String[][] rssArr) {
//        mActivity.registerReceiver(myReceiver, new IntentFilter("SEND_DATA"));

        dbHelper = new MySQLiteOpenHelper(mActivity);

        String day = rssArr[0][0];
        String hour = rssArr[0][1];
        double rsstemp = Double.valueOf(rssArr[0][2]);
        String sky = rssArr[0][3];
        String pty = rssArr[0][4];
        double pop = Double.valueOf(rssArr[0][5]);
        double r12 = Double.valueOf(rssArr[0][6]);
        double s12 = Double.valueOf(rssArr[0][7]);
        double ws = Double.valueOf(rssArr[0][8]);
        String wdKor = rssArr[0][9];
        double reh = Double.valueOf(rssArr[0][10]);

        ValueObject vo = new ValueObject(day, hour, rsstemp, sky, pty, pop, r12, s12, ws, wdKor, reh);

        Intent intent = new Intent("SEND_RSS");
        intent.putExtra("DATE", vo.getDate());
        intent.putExtra("DAY", vo.getDay());
        intent.putExtra("HOUR", vo.getHour());
        intent.putExtra("RSSTEMP", vo.getRsstemp());
        intent.putExtra("SKY", vo.getSky());
        intent.putExtra("PTY", vo.getPty());
        intent.putExtra("POP", vo.getPop());
        intent.putExtra("R12", vo.getR12());
        intent.putExtra("S12", vo.getS12());
        intent.putExtra("WS", vo.getWs());
        intent.putExtra("WDKOR", vo.getWdKor());
        intent.putExtra("REH", vo.getReh());
        mActivity.sendBroadcast(intent);

        Log.d(TAG, vo.getDate() + ", "
                + vo.getDay() + ", "
                + vo.getHour() + ", "
                + vo.getRsstemp() + ", "
                + vo.getSky() + ", "
                + vo.getPty() + ", "
                + vo.getPop() + ", "
                + vo.getR12() + ", "
                + vo.getS12() + ", "
                + vo.getS12() + ", "
                + vo.getWs() + ", "
                + vo.getWdKor() + ", "
                + vo.getReh());
        dbHelper.addRSSData(vo);
    }

    public StringBuffer json(String item) {
        StringBuffer sb = new StringBuffer();
        String[][] topArr = null;
        String xml = null;

        if (item.equals("top")) xml = "http://www.kma.go.kr/DFSROOT/POINT/DATA/top.json.txt";
        else xml = "http://www.kma.go.kr/DFSROOT/POINT/DATA/mdl." + item + ".json.txt";

        try {
            URL url = new URL(xml);
            Log.d(TAG,xml);
            int byteRead = 0;
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            InputStream input = conn.getInputStream();

            byte[] buffer = new byte[input.available()];

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            while ((byteRead = input.read(buffer)) > 0) {
                baos.write(buffer, 0, byteRead);
            }
            Log.d(TAG, String.valueOf(baos.toString()));

            JSONArray jarray = new JSONArray(baos.toString());

            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jObject = jarray.getJSONObject(i);  // JSONObject 추출
                String code = jObject.getString("code");
                String value = jObject.getString("value");

                sb.append(code + "," + value + ",");
            }
            Log.d(TAG, sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb;
    }

    public StringBuffer json_leaf(String item) {
        StringBuffer sb = new StringBuffer();
        String[][] topArr = null;
        String xml = null;

        xml = "http://www.kma.go.kr/DFSROOT/POINT/DATA/leaf." + item + ".json.txt";

        try {
            URL url = new URL(xml);
            int byteRead = 0;
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            InputStream input = conn.getInputStream();
            byte[] buffer = new byte[input.available()];

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            while ((byteRead = input.read(buffer)) > 0) {
                baos.write(buffer, 0, byteRead);
            }
            Log.d(TAG, String.valueOf(baos.toString()));

            JSONArray jarray = new JSONArray(baos.toString());

            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jObject = jarray.getJSONObject(i);  // JSONObject 추출
                String code = jObject.getString("code");
                String value = jObject.getString("value");
                String x = jObject.getString("x");
                String y = jObject.getString("y");

                sb.append(code + "," + value + "," + x + "," +  y+",");
            }
            Log.d(TAG, sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb;
    }

    public StringBuilder getRssData(int item, String xml) {

        String[] column = new String[11];
        column[0] = "";
        column[1] = "";
        column[2] = "온         도";
        column[3] = "하 늘 상 태";
        column[4] = "강 수 상 태";
        column[5] = "강 수 확 율";
        column[6] = "예상강수량";
        column[7] = "예상적설량";
        column[8] = "풍         속";
        column[9] = "풍         향";
        column[10] = "습         도";

        String[] unit = new String[11];
        unit[0] = "";
        unit[1] = "";
        unit[2] = "℃";
        unit[3] = "";
        unit[4] = "";
        unit[5] = "%";
        unit[6] = "";
        unit[7] = "";
        unit[8] = "m/s";
        unit[9] = "쪽";
        unit[10] = "%";

        StringBuilder sb = new StringBuilder();
        String[][] rssArr = new String[18][11];
        rssArr = parseRss(xml);
        String str = "";

        sb.append("**  " + rssArr[item][0] + " " + rssArr[item][1] + "날씨  **\n\n");


        for (int i = 2; i < rssArr[0].length; i++) {
            sb.append(column[i] + "\t\t\t\t\t\t" + rssArr[item][i] + " " + unit[i] + "\n");
        }
        return sb;
    }

}
