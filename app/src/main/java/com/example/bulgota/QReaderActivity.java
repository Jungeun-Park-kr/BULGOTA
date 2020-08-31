package com.example.bulgota;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;


public class QReaderActivity extends AppCompatActivity implements DecoratedBarcodeView.TorchListener {
    private CaptureManager manager;
    private boolean isFlashOn = false;

    private Button btFlash;
    private Button btCode;
    private DecoratedBarcodeView barcodeView;

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
            QRCodeDialog dialog = new QRCodeDialog(this);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            dialog.setDialogListener(new QRCodeDialog.CustomDialogListener() {
                @Override
                public void onPositiveClicked(String model) {
                    //대여하기
                    Intent intent = new Intent(QReaderActivity.this, CertCompletionActivity.class);
                    intent.putExtra("modelName", model);
                    startActivity(intent);
                    finish();
                }
                @Override
                public void onNegativeClicked() {
                }
            });
            dialog.show();
        });
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