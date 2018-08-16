package com.example.user.uvit_2;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.user.uvit_2.R;
import android.widget.Button;
import android.widget.GridLayout;

/*************그래프관련******************/
import android.graphics.Color;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

/**********************DB 관련************************/
import com.example.user.uvit_2.MySQLiteOpenHelper;

import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static android.support.v7.widget.StaggeredGridLayoutManager.TAG;


public class FragmentTrends extends Fragment {
    private static final String TAG = "FragmentTrends";
    private View view = null;
    GridLayout layout;
    MySQLiteOpenHelper db;
    int count;
    String Type = "DAY";
    Button btn_year, btn_month, btn_week, btn_day, btn_behind, btn_forward;
    String className;
    TextView selectDate;
    String year, month, day;

    int[] xV;
    float[] yV;
    String[] xVText; //X축에 같이 넣을 텍스트 0시,2시....
    String[] day_of_week = new String[7]; //요일 넣을 배열

    private static GraphicalView chartView;

    //Date관련
    Date Date;
    Calendar cal;
    Calendar cal2;
    SimpleDateFormat format;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) view = inflater.inflate(R.layout.fragment_trends, container, false);

        btn_year = (Button) view.findViewById(R.id.btn_year);
        btn_month = (Button) view.findViewById(R.id.btn_month);
        btn_week = (Button) view.findViewById(R.id.btn_week);
        btn_day = (Button) view.findViewById(R.id.btn_day);
        selectDate = (TextView) view.findViewById(R.id.date);
        btn_behind = (Button) view.findViewById(R.id.btn_1);
        btn_forward = (Button) view.findViewById(R.id.btn_2);


        db = new MySQLiteOpenHelper(view.getContext());


        btn_year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        /****하단에 날짜부분 (2016-09-01 ~ 2016-09-01)****/
        //http://d4emon.tistory.com/60

        format = new SimpleDateFormat("yyyy-MM-dd");


        btn_behind.setOnClickListener(new View.OnClickListener() { // 날짜를 뒤로
            public void onClick(View v) {


                if (Type.equals("DAY")) { //하루 전
                    cal.add(Calendar.DATE, -1);
                    cal2.add(Calendar.DATE, -1);
                } //day일땐 날짜 그대로

                else if (Type.equals("WEEK")) { //일주일 전
                    cal.add(Calendar.DATE, -6);
                    cal2.add(Calendar.DATE, -6);

                } else if (Type.equals("MONTH")) {  //한 달 전
                    cal.add(Calendar.MONTH, -1);
                    cal2.add(Calendar.MONTH, -1);
                } else if (Type.equals("YEAR")) { //일 년 전
                    cal.add(Calendar.YEAR, -1);
                    cal2.add(Calendar.YEAR, -1);
                }


                putDateText(cal, cal2);
                chartType(Type);

            }
        });

        btn_forward.setOnClickListener(new View.OnClickListener() { // 날짜를 앞으로
            public void onClick(View v) {
                if (Type.equals("DAY")) { //하루 전
                    cal.add(Calendar.DATE, 1);
                    cal2.add(Calendar.DATE, 1);
                } //day일땐 날짜 그대로

                else if (Type.equals("WEEK")) { //일주일 전
                    cal.add(Calendar.DATE, 6);
                    cal2.add(Calendar.DATE, 6);

                } else if (Type.equals("MONTH")) {  //한 달 전
                    cal.add(Calendar.MONTH, 1);
                    cal2.add(Calendar.MONTH, 1);
                } else if (Type.equals("YEAR")) { //일 년 전
                    cal.add(Calendar.YEAR, 1);
                    cal2.add(Calendar.YEAR, 1);
                }


                putDateText(cal, cal2);
                chartType(Type);

            }
        });
        /*************/


        defaultChart(); //default는 day

        btn_day.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { //일
                Type = "DAY";
                chartType(Type);

                Date = new Date();

                cal = Calendar.getInstance();
                cal2 = Calendar.getInstance();

                cal.setTime(Date);
                cal2.setTime(Date);

                putDateText(cal, cal2);
            }
        });

        btn_week.setOnClickListener(new View.OnClickListener() { //주
            public void onClick(View v) { //주
                Type = "WEEK";

                Date = new Date();


                Calendar[] calArr = new Calendar[7];
                for(int i=0; i<calArr.length; i++){
                    calArr[i] = Calendar.getInstance();
                    calArr[i].setTime(Date);
                }

                for(int i = 0; i<calArr.length; i++){
                    calArr[i].add(Calendar.DATE, -(6-i)); //일주일 데이터를 모두 셋팅해줌
                }

                setWeek(calArr); //calendar값을 통해 요일 얻어오는 메소드
                chartType(Type);

                putDateText(calArr[0], calArr[6]); //첫값,끝값으로 셋팅팅
            }
        });

        btn_month.setOnClickListener(new View.OnClickListener() { //월
            public void onClick(View v) { //월
                Type = "MONTH";
                chartType(Type);

                Date = new Date();

                cal = Calendar.getInstance();
                cal2 = Calendar.getInstance();

                cal.setTime(Date);
                cal2.setTime(Date);

                int year, month;
                int firstDay = 1, lastDay;

                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH); //9월 이면 month = 8

                //0 : 1월
                //1 : 2월 .........
                if (month == 1) lastDay = 28;
                else if (month == 3 || month == 5 || month == 8 || month == 10) { //4,6,9,11월
                    lastDay = 30;
                } else lastDay = 31;

                cal.set(year, month, firstDay); //(2015,7,31) 로 넣으면 2015년 8월 31일로 들어감
                cal2.set(year, month, lastDay);


                putDateText(cal, cal2);
            }
        });

        btn_year.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { //년
                Type = "YEAR";
                chartType(Type);

                Date = new Date();
                Date = new Date();

                cal = Calendar.getInstance();
                cal2 = Calendar.getInstance();

                cal.setTime(Date);
                cal2.setTime(Date);


                int year = cal2.get(Calendar.YEAR);

                int firstMonth = 0, lastMonth = 11;
                int firstDay = 1, lastDay = 31;


                cal.set(year, firstMonth, firstDay);
                cal2.set(year, lastMonth, lastDay);

                putDateText(cal, cal2);
            }
        });

        return view;
    }// onCreateView 끝


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void defaultChart() {
        //default
        chartType("DAY");

        Date = new Date();
        cal = Calendar.getInstance();
        cal2 = Calendar.getInstance();

        cal.setTime(Date);
        cal2.setTime(Date);

        putDateText(cal, cal2);
    }


    private void putDateText(Calendar cal, Calendar cal2) {
        String sDate = format.format(cal.getTime());
        Log.e("sDate:", sDate);
        String eDate = format.format(cal2.getTime());
        Log.e("eDate:", eDate);

        //    Log.e("cal2,eDate", eDate);

        String[] dateResult = eDate.split("-");
        /*  dateResult[0] : YEAR
            dateResult[1] : MONTH
            dateResult[2] : DAY  */

        year = dateResult[0];
        month = dateResult[1];
        day = dateResult[2];


        selectDate.setText(sDate + " ~ " + eDate);
    }


    public void chartType(String type) {

        //    putOriginalDate(type);
        setXV(type);
        choose(type);
    }

    private void choose(String type) {

        db = new MySQLiteOpenHelper(view.getContext());

        /*********** uvi (막대 그래프) 시작 ************/
        count = getXNum(type);

        float[] uvi_arr = {(float) 0.0};
        float[] vitamin_arr = {(float) 0.0};

//        uvi_arr = db.getTrendData("UVI", type, db.getYear(), db.getMonth(), db.getDay() );
//        vitamin_arr = db.getTrendData("VITAMIN", type, db.getYear(), db.getMonth(), db.getDay());

        uvi_arr = db.getTrendData("UVI", type, year, month, day);
        vitamin_arr = db.getTrendData("VITAMIN", type, year, month, day);

        GridLayout layout_uvi = (GridLayout) view.findViewById(R.id.uvi_Chart);
        layout_uvi.removeAllViews();

        //그래프 객체 생성
        final GraphicalView uvi_chart = ChartFactory.getBarChartView(view.getContext(), uvi_getDataset(xV, uvi_arr), uvi_getRenderer(), BarChart.Type.STACKED);
        layout_uvi.addView(uvi_chart);

        GridLayout layout_vitamind = (GridLayout) view.findViewById(R.id.Vitamin_Chart);
        layout_vitamind.removeAllViews();

        //그래프 생성
        final GraphicalView vitamin_chart = ChartFactory.getLineChartView(view.getContext(), vitamin_getDataset(xV, vitamin_arr), vitamin_getRenderer());
        layout_vitamind.addView(vitamin_chart);

        /*********** 비타민D (꺾은선 그래프) 끝 ************/
    }



    /*********
     * 막대 그래프 - UVI 차트 시작
     ************/
    //데이터들
    private XYMultipleSeriesDataset uvi_getDataset(int[] xV, float[] yV) {

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        XYSeries series = new XYSeries("Vitamin D");

        int seriesLength = xV.length;

        for (int k = 0; k < seriesLength; k++) {
            series.add(xV[k], yV[k]);
            //     Log.d("data", String.valueOf(xV[k] + yV[k]));
        }

        dataset.addSeries(series);

        return dataset;
    }

    //선 그리기
    private XYMultipleSeriesRenderer uvi_getRenderer() {
        XYSeriesRenderer renderer = new XYSeriesRenderer();     // one renderer for one series
        renderer.setColor(Color.BLACK);
        renderer.setDisplayChartValues(true);
        renderer.setChartValuesSpacing((float) 5.5d);
        renderer.setLineWidth((float) 10.5d);

        XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();   // collection multiple values for one renderer or series
        mRenderer.addSeriesRenderer(renderer);


        uvi_setChartSettings(mRenderer);

        return mRenderer;
    }

    private void uvi_setChartSettings(XYMultipleSeriesRenderer Renderer) {

        // renderer.setShowGrid(true);//격자 보이기: 표시 여부, true; false: 보이기.
        //customization of the chart

//        mRenderer.setChartTitle("UVIndex Chart");
        Renderer.setXTitle(Type);
        Renderer.setYTitle("UVI");

        Renderer.setZoomButtonsVisible(true);
        Renderer.setShowLegend(true);
        Renderer.setShowGridX(true);      // this will show the grid in  graph
        Renderer.setShowGridY(true);
//        mRenderer.setAntialiasing(true);

        //글자크기
//        mRenderer.setChartTitleTextSize(30);
        Renderer.setAxisTitleTextSize(30);          //x,y축 title
        Renderer.setLabelsTextSize(20);             //x,y축 수치

        Renderer.setBarSpacing(.5);   // adding spacing between the line or stacks
        Renderer.setApplyBackgroundColor(true);
        Renderer.setBackgroundColor(Color.WHITE);
        Renderer.setMargins(new int[]{30, 50, 10, 20}); //상 좌 하 우 ( '하' 의 경우 setFitLegend(true)일 때에만 가능 )

        //x,y축 표시 간격 ( 각 축의 범위에 따라 나눌 수 있는 최소치가 제한 됨 )
        Renderer.setXLabels(5);
        Renderer.setYLabels(5);

        //x축 최대 최소(화면에 보여질)
        Renderer.setXAxisMin(0);
        Renderer.setXAxisMax(count);

        //y축 최대 최소(화면에 보여질)
        Renderer.setYAxisMin(0);
        Renderer.setYAxisMax(14);

        //줌 기능 가능 여부
        Renderer.setZoomEnabled(false, false);

        //색
        Renderer.setAxesColor(Color.RED);       //x,y축 선 색
        Renderer.setLabelsColor(Color.BLACK);    //x,y축 글자색

        Renderer.setBackgroundColor(Color.WHITE);    //그래프 부분 색
        Renderer.setMarginsColor(Color.WHITE);       //그래프 바깥 부분 색(margin)

        Renderer.setPanEnabled(true, true);    // will fix the chart position
        Renderer.setGridColor(0xFF212121);//격자 색상 설정, 짙은 회색으로


        //X축에 text추가하기
        if (Type.equals("DAY") || Type.equals("MONTH") || Type.equals("YEAR") || Type.equals("WEEK")) {
            for (int i = 0; i < xVText.length; i++) {
                Renderer.addXTextLabel(i, xVText[i]);
            }
        }
        Renderer.setXLabels(0); // x축 숫자 안보이게 하기
    }

    /*********
     * 막대 그래프 - uvi 차트 끝
     ************/



    /*********
     * 꺾은선 그래프 - 비타민D 차트 시작
     ************/

    //그래프 설정 모음
    // http://www.programkr.com/blog/MQDN0ADMwYT3.html ( 그래프 설정 속성 한글로 써져있는 사이트 )

    //X축
    //일 : 0시 ~ 23시
    //주 : 월요일 ~ 일요일
    //월 : 1일 ~ 31일
    //년 : 1월 ~ 12월
    private void vitaminSetChartSettings(XYMultipleSeriesRenderer renderer) {
        //타이틀, x,y축 글자
        renderer.setXTitle(Type);
        renderer.setYTitle("Vitamin D");
        renderer.setChartTitleTextSize(20);

        //  renderer.setRange(new double[]{0, 6, -70, 40})

        //background
        renderer.setApplyBackgroundColor(true);      //변경 가능여부
        renderer.setBackgroundColor(Color.WHITE);    //그래프 부분 색
        renderer.setMarginsColor(Color.WHITE);       //그래프 바깥 부분 색(margin)

        //글자크기
//        mRenderer.setChartTitleTextSize(30);
        renderer.setAxisTitleTextSize(30);          //xy축 제목 글꼴 크기
        renderer.setLabelsTextSize(20);            //xy 축 위에서 수치 크기 설정

        renderer.setMargins(new int[]{30, 50, 10, 20}); //상 좌 하 우 ( '하' 의 경우 setFitLegend(true)일 때에만 가능 )

        //색
        renderer.setAxesColor(Color.RED);       //x,y축 선 색
        renderer.setLabelsColor(Color.BLACK);    //x,y축 글자색
        // renderer.setXLabelsColor(0x660000FF);//x축 수치 글꼴 색상

        //x,y축 표시 간격 ( 각 축의 범위에 따라 나눌 수 있는 최소치가 제한 됨 )
        renderer.setXLabels(5);
        renderer.setYLabels(10);

        //x축 최대 최소(화면에 보여질)
        renderer.setXAxisMin(0);
        renderer.setXAxisMax(count);//수정!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1

        //y축 최대 최소(화면에 보여질)
        renderer.setYAxisMin(0);
        renderer.setYAxisMax(600);

        //클릭 가능 여부
        renderer.setClickEnabled(false);
        //줌 기능 가능 여부
        renderer.setZoomEnabled(false, false);
        //X,Y축 스크롤
        renderer.setPanEnabled(true, false);                // 가능 여부
        renderer.setPanLimits(new double[]{-2, 24, 20, 40});   // 가능한 범위

        //지정된 크기에 맞게 그래프를 키움
        renderer.setFitLegend(true);
        //간격에 격자 보이기
        renderer.setShowGrid(true);
        renderer.setGridColor(0xFF212121);//격자 색상 설정, 짙은 회색으로

        // http://www.masterqna.com/android/43152/achartengine-x-%EC%B6%95%EC%97%90-value-%EA%B0%92-%EB%A7%90%EA%B3%A0-text-%EB%8D%B0%EC%9D%B4%ED%84%B0%EB%A1%9C-%EB%8C%80%EC%B2%B4%ED%95%98%EB%8A%94-%EB%B2%95%EC%A2%80-%EC%95%8C%EB%A0%A4%EC%A3%BC%EC%84%B8%EC%9A%94

        //꺾은선 그래프 포인트(동그라미) 사이즈 조절
        renderer.setPointSize(5);


        //X축에 text추가하기
        if (Type.equals("DAY") || Type.equals("MONTH") || Type.equals("YEAR") || Type.equals("WEEK") ) {
            Log.e("xVText lenght", xVText.length+"");
            for (int i = 0; i < xVText.length; i++) {
                renderer.addXTextLabel(i, xVText[i]);
                Log.e("xVText"+i, xVText[i]);
            }
        } else if (Type.equals("WEEEK")) {
            getWeek();
            xVText = new String[xVText.length];
            for(int i = 0; i < xVText.length; i++) {
                renderer.addXTextLabel(i,xVText[i]);
                Log.e("xVText"+i, xVText[i]);
            }
        }

        renderer.setXLabels(0);  // x축 숫자 안보이게 하기
    }

    //선 그리기
    private XYMultipleSeriesRenderer vitamin_getRenderer() {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

        //---그려지는 점과 선 설정----
        XYSeriesRenderer r = new XYSeriesRenderer();
        r.setColor(Color.BLACK);            //색
        r.setPointStyle(PointStyle.DIAMOND);//점의 모양
        r.setFillPoints(false);             //점 체우기 여부
        renderer.addSeriesRenderer(r);
        //----------------------------

        /*
        * 다른 그래프를 추가하고 싶으면
        * XYSeriesRenderer 추가로 생성한 후
        *  renderer.addSeriesRenderer(r) 해준다 (Data도 있어야함)
        *
        */


        vitaminSetChartSettings(renderer);
        return renderer;
    }

    //데이터들
    private XYMultipleSeriesDataset vitamin_getDataset(int[] xV, float[] yV) {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

        XYSeries series = new XYSeries("");

        int seriesLength = xV.length;

        for (int k = 0; k < seriesLength; k++) {
            series.add(xV[k], yV[k]);
        }

        dataset.addSeries(series);


        return dataset;
    }
    /********* 꺾은선 그래프 - 비타민D 차트 끝 ************/

    //일,주,월,년 에 따라 x측의 max값도 달라진다.
    private int getXNum(String type) {
        if (Type == "DAY") {
            return 23;
        } else if (Type == "WEEK") {
            return 7;
        } else if (Type == "MONTH") {
            return 31;
        } else {
            return 12;
        }
    }

    private void setXV(String type) {
        if (type.equals("DAY")) { //24
            xV = new int[24];
            xVText = new String[24];
            for (int i = 0; i < 24; i++) {
                xV[i] = i;
                if(i > 12){
                    xVText[i] = (i-12)+"시";
                }
                else{
                    xVText[i] = i+"시";
                }

            }

        } else if (type.equals("WEEK")) { //7
            xV = new int[7];
            xVText = new String[7];
            for (int i = 0; i < 7; i++) {
                xV[i] = i;

                //요일 셋팅
                String[] week = new String[day_of_week.length];
                week = getWeek();
                xVText[i] = week[i];
            }

            //xVTextLog
            for(int i=0; i<7; i++){
                Log.e("xVText"+i, xVText[i]);
            }


        } else if (type.equals("MONTH")) { //31
            xV = new int[31];
            xVText = new String[31];
            for (int i = 0; i < 31; i++) {
                xV[i] = i + 1;
            //    xVText[i] = (i+1) + "일";
                xVText[i] = (i+1) + "";

            }
        } else if (type.equals("YEAR")) { //12
            xV = new int[12];
            xVText = new String[12];
            for (int i = 0; i < 12; i++) {
                xV[i] = i + 1;
                xVText[i] = (i+1) + "월";
            }
        }
    }

    private void setWeek(Calendar[] cal) { //무슨 요일인지를 알아내기 위한 메소드
        for (int i = 0; i < cal.length; i++) {

            switch (cal[i].get(Calendar.DAY_OF_WEEK)) {
                case 1:
                    day_of_week[i] = "일";
                    break;
                case 2:
                    day_of_week[i] = "월";
                    break;
                case 3:
                    day_of_week[i] = "화";
                    break;
                case 4:
                    day_of_week[i] = "수";
                    break;
                case 5:
                    day_of_week[i] = "목";
                    break;
                case 6:
                    day_of_week[i] = "금";
                    break;
                case 7:
                    day_of_week[i] = "토";
                    break;
            }
        }
    }

    private String[] getWeek() {
        return day_of_week;
    }

    @Override
    public void onStop() {
        Log.w(TAG, "App stopped");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.w(TAG, "App destroyed");
        super.onDestroy();
    }
}