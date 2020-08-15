package com.example.bulgota;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;


public class DetoxAnalysisActivity extends AppCompatActivity {

    private LineChart lineChart;
    ArrayList<Entry> entry_chart = new ArrayList<>();
    //차트

    private TextView countTime;
    MyTimer myTimer;    //타이머객체
    //타이머

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detox_analysis);

        countTime = findViewById(R.id.tv_timer);
        myTimer = new MyTimer(60000, 1000);
        myTimer.start();
        // 그래프선언
        lineChart = findViewById(R.id.chart); //layout id
        LineData chartData = new LineData();

        Entry data1 = new Entry(15, 100);
        Entry data2 = new Entry(16, 90);
        Entry data3 = new Entry(17, 65);
        Entry data4 = new Entry(18, 30);
        Entry data5 = new Entry(19, 1);

        entry_chart.add(data1);   //x y 좌표
        entry_chart.add(data2);   //x y 좌표
        entry_chart.add(data3);   //x y 좌표
        entry_chart.add(data4);   //x y 좌표
        entry_chart.add(data5);   //x y 좌표

        LineDataSet lineDataSet = new LineDataSet(entry_chart, "나");
        chartData.addDataSet(lineDataSet);

        lineDataSet.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)); //LineChart에서 Line Color 설정
        lineDataSet.setCircleColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)); // LineChart에서 Line Circle Color 설정
        lineDataSet.setCircleHoleColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)); // LineChart에서 Line Hole Circle Color 설정

//        LineData lineData = new LineData(); //LineDataSet을 담는 그릇 여러개의 라인 데이터가 들어갈 수 있습니다.
//        lineData.addDataSet(lineDataSet);
//
//        lineData.setValueTextColor(ContextCompat.getColor(getContext(), R.color.textColor)); //라인 데이터의 텍스트 컬러 설정
//        lineData.setValueTextSize(9);
//
//        XAxis xAxis = lineChart.getXAxis(); // x 축 설정
//        xAxis.setPosition(XAxis.XAxisPosition.TOP); //x 축 표시에 대한 위치 설정
//        xAxis.setValueFormatter(new ChartXValueFormatter()); //X축의 데이터를 제 가공함. new ChartXValueFormatter은 Custom한 소스
//        xAxis.setLabelCount(5, true); //X축의 데이터를 최대 몇개 까지 나타낼지에 대한 설정 5개 force가 true 이면 반드시 보여줌
//        xAxis.setTextColor(ContextCompat.getColor(getContext(), R.color.textColor)); // X축 텍스트컬러설정
//        xAxis.setGridColor(ContextCompat.getColor(getContext(), R.color.textColor)); // X축 줄의 컬러 설정
//
//        YAxis yAxisLeft = lineChart.getAxisLeft(); //Y축의 왼쪽면 설정
//        yAxisLeft.setTextColor(ContextCompat.getColor(getContext(), R.color.textColor)); //Y축 텍스트 컬러 설정
//        yAxisLeft.setGridColor(ContextCompat.getColor(getContext(), R.color.textColor)); // Y축 줄의 컬러 설정
//
//        YAxis yAxisRight = lineChart.getAxisRight(); //Y축의 오른쪽면 설정
//        yAxisRight.setDrawLabels(false);
//        yAxisRight.setDrawAxisLine(false);
//        yAxisRight.setDrawGridLines(false);
//        //y축의 활성화를 제거함
//
//        lineChart.setVisibleXRangeMinimum(60 * 60 * 24 * 1000 * 5); //라인차트에서 최대로 보여질 X축의 데이터 설정
//        lineChart.setDescription(null); //차트에서 Description 설정 저는 따로 안했습니다.
//
//        Legend legend = lineChart.getLegend(); //레전드 설정 (차트 밑에 색과 라벨을 나타내는 설정)
//        legend.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);//하단 왼쪽에 설정
//        legend.setTextColor(ContextCompat.getColor(getContext(), R.color.textColor)); // 레전드 컬러 설정
        // lineChart.setData(lineData);

        lineChart.setData(chartData);
        lineChart.invalidate();
    }

    class MyTimer extends CountDownTimer
    {
        public MyTimer(long millisInFuture, long countDownInterval)
        {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            countTime.setText(millisUntilFinished/1000 + " 초");
        }

        @Override
        public void onFinish() {
            countTime.setText("0 초");
        }
    }
}