package com.example.bulgota;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //퍼미션 리스트
    private static String[] permission_list = {
            Manifest.permission.INTERNET,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA
    };

    Button deviceMapBtn;
    Button breathTestingBtn;
    Button detoxAnalysisBtn;
    Button QRCodeScanBtn;
    Button certCompletionBtn;
    Button splashActivityBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();

        deviceMapBtn = findViewById(R.id.btn_device_map);
        breathTestingBtn = findViewById(R.id.btn_breath_testing);
        detoxAnalysisBtn = findViewById(R.id.btn_detox_analysis);
        QRCodeScanBtn = findViewById(R.id.btn_QRcode_scan);
        certCompletionBtn = findViewById(R.id.btn_cert_completion);
        splashActivityBtn = findViewById(R.id.btn_splash);

        deviceMapBtn.setOnClickListener(this);
        breathTestingBtn.setOnClickListener(this);
        detoxAnalysisBtn.setOnClickListener(this);
        QRCodeScanBtn.setOnClickListener(this);
        certCompletionBtn.setOnClickListener(this);
        splashActivityBtn.setOnClickListener(this);


        Intent intent = getIntent();
        if(intent != null) {//푸시알림을 선택해서 실행한것이 아닌경우 예외처리
            String notificationData = intent.getStringExtra("test");
            if(notificationData != null)
                Log.d("FCM_TEST", notificationData);

        }

    } //view 객체 획득

    @Override
    public void onClick(View v) {

        if(v == deviceMapBtn) {
            Intent intent = new Intent(this,DeviceMapActivity.class);
            startActivity(intent);
        }  else if (v == breathTestingBtn) {
            Intent intent = new Intent(this, BreatheTestingActivity.class);
            startActivity(intent);
        } else if (v == detoxAnalysisBtn) {
            Intent intent = new Intent(this,DetoxAnalysisActivity.class);
            startActivity(intent);
        } else if (v == QRCodeScanBtn) {
            Intent intent = new Intent(this,QRCodeScanActivity.class);
            startActivity(intent);
        } else if (v == certCompletionBtn) {
            Intent intent = new Intent(this,CertCompletionActivity.class);
            startActivity(intent);
        } else if (v == splashActivityBtn) {
            Intent intent = new Intent(this,SplashActivity.class);
            startActivity(intent);
        }

    }

    public void checkPermission(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) //안드로이드 버전 6.0 미만일시 리턴
            return;

        for (String permission : permission_list) { //권한 허용 여부 확인
            int check = checkCallingOrSelfPermission(permission);

            if (check == PackageManager.PERMISSION_DENIED) //권한 허용여부 확인 창 띄우기
                requestPermissions(permission_list,0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 0) { //퍼미션 리스트 하나씩 확인
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) //허용 되었다면
                    ; //허용 되었음 동작할 메소드
                else { //허용 되지 않은 경우 앱종료
                    Toast.makeText(this, "불고타 서비스 이용을 위해 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                    //showRequestAgainDialog();
                }
            }
        }
    }

}