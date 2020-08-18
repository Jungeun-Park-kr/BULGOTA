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