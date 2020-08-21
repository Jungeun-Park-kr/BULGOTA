package com.example.bulgota;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;

public class BreatheTestingActivity extends AppCompatActivity implements View.OnClickListener{
    private BluetoothSPP btSpp; //블루투스 통신
    Button btnMeasureActivity;//측정하기 화면 이동 버튼
    Button btnTesting; //진짜 측정화면


    public static Context context_main;
    public boolean result = true; //Testing Activity에 보낼 측정 결과값을 저장
    public boolean isTesting; //측정중인지 확인(Testing Activity에서 가져옴)

    double dValue; ////측정값 가져오기 (mg/L) , 혈중알코올 농도:mg/100mL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breathe_testing);

        context_main = this;

        btnTesting = findViewById(R.id.btn_testing);
        btnTesting.setOnClickListener(this);
        btnMeasureActivity = findViewById(R.id.btn_testing_activity);
        btnMeasureActivity.setOnClickListener(this);

        btSpp = new BluetoothSPP(this); //초기화

        if (!btSpp.isBluetoothAvailable()) { //블루투스 사용 불가능할 경우
            Toast.makeText(getApplicationContext()
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }

        btSpp.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //데이터 수신하기
            /* 아두이노에서 넘어온 데이터 수신 : 아두이노는 1byte씩 데이터 수신됨.
             *   따라서 아두이노에서 온 데이터를 넣어 바이트를 모두 합친 후 msg를 통해 return됨
             *   즉 우리가 사용할 것 : msg!! // 아래는 msg를 토스트로 띄운 예시
             */
            TextView tvValue = findViewById(R.id.tv_value);


            @Override
            public void onDataReceived(byte[] data, String msg) { //데이터 수신 받을 때
                //int iValue = Integer.parseInt(msg);

                dValue = Double.parseDouble(msg);
                tvValue.setText("측정값:" + dValue);
                if (dValue > 300) { //현재 측정중이고 300 넘은 경우 - 운전 불가
                    result = false;
                }
            }
        });

        btSpp.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() { //연결됐을 때
            @Override
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext()
                        , "Connected to " + name + "\n" + address
                        , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeviceDisconnected() { //연결 해제
                Toast.makeText(getApplicationContext()
                        , "Connection lost", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeviceConnectionFailed() { //연결 실패
                Toast.makeText(getApplicationContext()
                        , "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        });

        ////////////////////////////////////////////////////////////////연결 시도
        Button btnConnect = findViewById(R.id.btn_measure); //측정하기 버튼
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btSpp.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    btSpp.disconnect();
                } else {
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }
            }
        });
    }

    public void onDestroy() {
        super.onDestroy();
        btSpp.stopService(); //블루투스 중지
    }

    public void onStart() {
        super.onStart();
        if (!btSpp.isBluetoothEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!btSpp.isServiceAvailable()) {
                btSpp.setupService();
                btSpp.startService(BluetoothState.DEVICE_OTHER); //아두이노와 연결 / DEVICE_ANDROID는 안드로이드 기기 끼리

            }
        }
    }


    public void measure() {

        Button btnMeasure = findViewById(R.id.btn_measure); //
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                btSpp.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK) {
                btSpp.setupService();
                btSpp.startService(BluetoothState.DEVICE_OTHER);
                // setup();
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                //finish();
            }
        }
    }


    @Override
    public void onClick(View v) {
//        if(v == btnMeasureActivity) {
//            Intent intent = new Intent(this,BreathTestingActivity.class);
//            startActivity(intent);
//        }
//        else if (v== btnTesting) {
//            Intent intent = new Intent(this,TestingActivity.class);
//            startActivity(intent);
//        }

    }
}