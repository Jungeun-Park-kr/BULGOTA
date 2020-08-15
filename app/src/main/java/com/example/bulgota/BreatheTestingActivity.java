package com.example.bulgota;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class BreatheTestingActivity extends AppCompatActivity {

    private BluetoothSPP btSpp; //블루투스 통신
    Button btnMeasureActivity;//측정하기 화면 이동 버튼

    public static LottieAnimationView lottieBreathTesting; //(로딩모양)측정중 로띠띠
   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breathe_testing);

        LottieAnimationView lottieTesting = findViewById(R.id.lottie_breathe_testing);
        setUpAnimation(lottieTesting); //애니메이션 등록

    /////여기서부터 블루투스 ///////
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
               int iValue = Integer.parseInt(msg);
               tvValue.setText("측정값:" + iValue);
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
                finish();
            }
        }
    }

    private void setUpAnimation(LottieAnimationView animview) { //로띠 애니메이션 설정
        //재생할 애니메이션
        animview.setAnimation("lottie_breathe_testing.json");
        //반복횟수 지정 : 무한
        animview.setRepeatCount(LottieDrawable.INFINITE); //아니면 횟수 지정
        //시작
        animview.playAnimation();

    }
}