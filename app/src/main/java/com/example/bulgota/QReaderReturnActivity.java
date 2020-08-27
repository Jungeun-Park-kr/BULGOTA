package com.example.bulgota;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.example.bulgota.api.BullgoTAService;
import com.example.bulgota.api.ResponseReturnModel;
import com.example.bulgota.api.ResponseSelectModel;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


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
            QRCodeDialog dialog = new QRCodeDialog(this);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            dialog.setDialogListener(new QRCodeDialog.CustomDialogListener() {
                @Override
                public void onPositiveClicked(String model) {
//                    //반납하기
//                    Intent intent = new Intent(QReaderReturnActivity.this, DeviceMapActivity.class);
//                    intent.putExtra("modelNum", model);
//                    intent.putExtra("lendStatus", lendStatus);
//                    startActivity(intent);
//                    finish();
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