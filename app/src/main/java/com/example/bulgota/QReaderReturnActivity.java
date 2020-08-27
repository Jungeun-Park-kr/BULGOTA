package com.example.bulgota;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;


public class QReaderReturnActivity extends AppCompatActivity implements DecoratedBarcodeView.TorchListener {
    private CaptureManager manager;
    private boolean isFlashOn = false;

    private Button btFlash;
    private Button btCode;
    private DecoratedBarcodeView barcodeView;

    private static final int SUCCESS = 0;
    private static final int ALREADY = 1;
    private static final int FAIL = 2;

    private String value = "";

    private boolean correct = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_qrcode);

        barcodeView = findViewById(R.id.qr_layout);

        manager = new CaptureManager(this, barcodeView);
        manager.initializeFromIntent(getIntent(), savedInstanceState);
        manager.decode();


        btFlash = findViewById(R.id.btn_flash);
        btFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFlashOn) {
                    barcodeView.setTorchOff();
                    onTorchOff();
                } else {
                    barcodeView.setTorchOn();
                    onTorchOn();
                }
            }
        });

        btCode = findViewById(R.id.btn_code);

        btCode.setOnClickListener(l -> {
            QRCodeReturnDialog qrCodeReturnDialog = new QRCodeReturnDialog(this);
            qrCodeReturnDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            qrCodeReturnDialog.setDialogListener(new QRCodeReturnDialog.CustomDialogListener() {
                @Override
                public void onPositiveClicked(String model, int data, int status) {
                    ReturnModelDialog returnModelDialog = new ReturnModelDialog(QReaderReturnActivity.this);
//                    //반납하기
                    switch(status) {
                        case 0://반납 성공
                            int usageTime = data;
                            returnModelDialog.setReturnModelDialog(status, model, usageTime);
                            break;
                        case 1://이미 반납된 모델
                            Log.e("status", String.valueOf(status));
                            returnModelDialog.setReturnModelDialog(status, model, 0);
                            break;
                        case 2://반납 오류
                            Log.e("status", String.valueOf(status));
                            returnModelDialog.setReturnModelDialog(status, model, -1);
                            break;
                    }
                }
                @Override
                public void onNegativeClicked() {
                }
            });
            qrCodeReturnDialog.show();
        });
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent intent = new Intent(CertCompletionActivity.this,DeviceMapActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        }, 3000);
    }

    @Override
    public void onTorchOn() {
        isFlashOn=true;
    }

    @Override
    public void onTorchOff() {
        isFlashOn = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        manager.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        manager.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        manager.onSaveInstanceState(outState);
    }
}