package com.example.bulgota;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;


public class DetoxAnalysisActivity extends AppCompatActivity{

    //TODO  상민이 읽어보고 이해안되는 부분 있음 알려줘 ! 가장먼저 읽어볼 것 !
    //TODO  STEP1. 카운트다운은 내부 SQLITE(DB)에서 적용불가 하기 때문에 삭제함 TEXTVIEW로 대체
    //TODO  STEP2. DataSendSever에 int timer 클래스 변수가 유지해야함 -> 영현이 서버에 timer 받을 때 INT로 설정되있어서 분석 후 필요한 시간을 STRING 값이랑 INT값 둘 모두 필요함
    //TODO  STEP3.  해당 액티비에서 내부DB에 저장할 String 시간 값 받아와야함    ->  DB저장하는 .java 클래스 생성   ->   해당 시간 string 변수를 저장할 static scope의 변수에 저장 -> 일반 변수에서는 생명주기가 끝나면 소멸
    //TODO 이외 부분은 건드릴 필요 없들것 같음 STEP 3부터는 내가 해야되는 부분이니까 신경쓰지 않아도 됨

    private LineChart lineChart;
    ArrayList<Entry> entry_chart;
    //차트 객체
    private TextView countTime;

    //TODO 타이머 객체 사용 X
    //TODO 해당 CUSTOM 객체 삭제해야함 TEXTVIEW로 시간 찍어줄 것임       우선 혹시몰라 주석처리함
    //MyTimer myTimer;    //타이머객체

    LinearLayout llNoticeView;  //상단 전체 layout
    LinearLayout llState;   //주행가능 여부

    RelativeLayout rlAlarm; //주행가능시 알람받기 layout

    TextView tvAlcholLevel; //현재 알콜 농도
    TextView tvMystate; //주행 가능 or 불가 텍스트
    TextView tvtimer;   //주행가능까지 남은시간

    //혈중 알코올 농도
    double bac;
    //혈중 알코올 농도
    int timer;
    //TODO
    //TODO 아두이노에서 받아온 알콜농도에서 해독시간까지 걸리는 시간 값 저장 변수
    //TODO DataSendServer.java 파일에 timer 변수에 전달해야함
    //TODO 해당 java 파일에 전달하기 위해 int timer 매개변수 추가하여 값 저장
    //TODO DataSendServer.java의 timer 변수는 sendRegisterToserver 메서드안에 DataSendServer 객체 생성자에서 전달됨
    //TODO 이부분은 미리 수정해둠. flow 확인해주세요.

    String sTimer;
    //TODO
    //TODO 아두이노에서 받아온 알콜농도에서 해독시간까지 걸리는 시간을 string 값으로 저장하는 변수
    //TODO 소스코드 하단에 makeStringTimer 메서드 사용해서 리턴값만 받아오면 됨
    //TODO 해당 메서드 매개변수는 해독시간까지 걸리는 timer변수값 넣으면 됨

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detox_analysis);

        countTime = findViewById(R.id.tv_timer);    //타이머
        lineChart = findViewById(R.id.chart);   //그래프

        llNoticeView = findViewById(R.id.ll_notice_view);
        llState = findViewById(R.id.ll_state);

        rlAlarm = findViewById(R.id.rl_alarm);

        tvAlcholLevel = findViewById(R.id.tv_alchol_level);
        tvMystate = findViewById(R.id.tv_my_state);
        tvtimer = findViewById(R.id.tv_timer);

        //intent 추가
        Intent intent = getIntent();
        bac = intent.getDoubleExtra("bac",0.03);

        tvAlcholLevel.setText(String.format("%.2f", bac));

        timer = (int)(bac * 60 * 60 / 0.015); // 초단위로 바꿈
        //intent 추가

        rlAlarm.setOnClickListener(new RelativeLayout.OnClickListener(){
            @Override
            public void onClick(View view) {

                //서버로부터 예상 해독시간 및 사용자 토큰id 전달

                FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            //토큰 값 전달 실패 시
                            if(task.isSuccessful()==false){
                                Log.e("토큰 id전달 실패","send token error",task.getException());
                                return;
                            }

                            String token = task.getResult().getToken();

                            //서버에서 json값 수신
                            //getRegistrationToServer(token);

                            //서버에서 json값 전송
                            //TODO 해당 메서드 호출 전에 timer 변수 값 변경필요...
                            // 여기서 서버에 전달할 변수들이 저장되기 때문에 Onclick 메서드 호출 전에 해독시간 int값 저장 알고리즘 사용 후 저장 필요
                            sendRegistrationToServer(token);
                        }
                    });
            }
        });


        //TODO 타이머 객체 사용 X
        //TODO 해당 CUSTOM 객체 삭제해야함 TEXTVIEW로 시간 찍어줄 것임       우선 혹시몰라 주석처리함
      /*  myTimer = new MyTimer(600000, 1000);
        myTimer.start();*/

        LineData chartData = new LineData();
        // 그래프선언
        entry_chart = new ArrayList<>();

        graphDataAdd(entry_chart);
        //그래프에 들어갈 ArrayList 자료구조 데이터 추가 메서드

        LineDataSet lineDataSet = new LineDataSet(entry_chart, "나");

        chartSetting(lineChart,lineDataSet,chartData);
        //차트 설정값 세팅 메서드

        //TODO  알콜농도에서 구한 timer값 string 변수로 변경하는 메서드
        sTimer =  makeStringTimer(timer);

    }

    void sendRegistrationToServer(String token) {
        String serverUrl = "https://bullgota.ml/notification/push";
         new DataSendServer(serverUrl,token,timer).execute();
    }

    void getRegistrationToServer(String token){
        String serverUrl = "https://bullgota.ml/list/marker";
        new DataGetServer(serverUrl).execute();

    }

    void graphDataAdd(ArrayList<Entry> entry_chart)
    {
        int xTime=0;
        double yBac=bac;
        while(yBac>=0.0){
            Entry data = new Entry(xTime++, (float)(yBac));
            entry_chart.add(data);   //x y 좌표
            yBac-=0.015;
        }
        if(yBac > -0.015){
            yBac += 0.015;
            Entry data = new Entry(xTime+(float)(yBac/0.015), 0.0f);
            entry_chart.add(data);
        }
    }

    void chartSetting(LineChart lineChart,LineDataSet lineDataSet, LineData chartData)
    {
        lineDataSet.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)); //LineChart에서 Line Color 설정
        lineDataSet.setCircleColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)); // LineChart에서 Line Circle Color 설정
        lineDataSet.setCircleHoleColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)); // LineChart에서 Line Hole Circle Color 설정

        chartData.addDataSet(lineDataSet);

        chartData.setValueTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent)); //라인 데이터의 텍스트 컬러 설정
        chartData.setValueTextSize(11);

        XAxis xAxis = lineChart.getXAxis(); // x 축 설정
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); //x 축 표시에 대한 위치 설정
        xAxis.setLabelCount(5, true); //X축의 데이터를 최대 몇개 까지 나타낼지에 대한 설정 5개 force가 true 이면 반드시 보여줌
        xAxis.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent)); // X축 텍스트컬러설정
        xAxis.setGridColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent)); // X축 줄의 컬러 설정


        YAxis yAxisLeft = lineChart.getAxisLeft(); //Y축의 왼쪽면 설정
        yAxisLeft.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent)); //Y축 텍스트 컬러 설정
        yAxisLeft.setGridColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent)); // Y축 줄의 컬러 설정

        YAxis yAxisRight = lineChart.getAxisRight(); //Y축의 오른쪽면 설정
        yAxisRight.setDrawLabels(false);
        yAxisRight.setDrawAxisLine(false);
        yAxisRight.setDrawGridLines(false);
        //y축의 활성화를 제거함

        Description description = new Description();
        description.setTextSize(12);
        description.setText("predicted by Withmark.");
        lineChart.setDescription(description); //오른쪽 하단에 description 설정

        lineChart.setData(chartData);
        lineChart.invalidate();

        Legend legend = lineChart.getLegend(); //레전드 설정 (차트 밑에 색과 라벨을 나타내는 설정)
        legend.setDirection(Legend.LegendDirection.RIGHT_TO_LEFT);//색과 라벨 위치설정
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL); // 라벨 그래프와 수평
        legend.setFormSize(10); //차트 색 크기
        legend.setTextSize(9); //라벨 text 크기
        legend.setForm(Legend.LegendForm.CIRCLE);   //차트 형태
        legend.setYOffset(20);  //라벨과 그래프 offset
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);   //라벨 위치
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);    //라벨 위치
    }


    //TODO 아두이노에서 받아온 알콜농도에서 해독시간까지 걸리는 시간을 string 값으로 변경하는 메서드
    private String makeStringTimer(int timer) {
        int sec;
        int min;
        int  hour;
        String sTimer;

        sec = timer%60;
        min = timer/60;
        hour = min/60;

        sTimer = hour+"시"+min+"분"+sec+"초";

        return sTimer;
    }

    //TODO 타이머 객체 사용 X
    //TODO 해당 CUSTOM 객체 삭제해야함 TEXTVIEW로 시간 찍어줄 것임       우선 혹시몰라 주석처리함
/*    class MyTimer extends CountDownTimer
    {
        public MyTimer(long millisInFuture, long countDownInterval)
        {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            countTime.setText(millisUntilFinished /3600000 + " 시간 "+millisUntilFinished % 3600000 / 60000 +" 분 " + millisUntilFinished % 60 / 1000 + " 초");
        }

        @Override
        public void onFinish() {
            countTime.setText("0 초");
        }
    }*/
}