package com.example.bulgota;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bulgota.api.BullgoTAService;
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

public class QRCodeScanActivity extends AppCompatActivity {
    //View Objects
    private Button button;
    private TextView textView;

    //qr code scanner object

    private CaptureManager manager;
    private boolean isFlashOn = false;

    private Button btFlash;
    private DecoratedBarcodeView barcodeView;

    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_code_scan);

        //View Objects
        button = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textView);

        //initializing scan object
        qrScan = new IntentIntegrator(this);

        //button onClick
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //scan option
                qrScan.setBeepEnabled(false);
                qrScan.setCaptureActivity(QReaderActivity.class);
                qrScan.setOrientationLocked(false);
                qrScan.initiateScan();
            }
        });
    }

    //Getting the scan results

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            //qrcode가 없으면
            if (result.getContents() == null) {

            } else {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BullgoTAService.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                BullgoTAService bullgoTAService = retrofit.create(BullgoTAService.class);
                Log.e("result.getContent2", result.getContents());
                bullgoTAService.checkModel(result.getContents()).enqueue(new Callback<ResponseSelectModel>() {
                //bulgotaservice return mode
                    //responseReturnModel
                    //95 getMessage()변경
                    //반납가능시 0 이미반납된 모델이면 1 반납실패 2
                    @Override
                    public void onResponse(Call<ResponseSelectModel> call, Response<ResponseSelectModel> response) {
                        Log.e("getSuccess", String.valueOf(response.body().getSuccess()));

                        if (response.body().getSuccess()) {
                            //유효한 모델이면
                            Intent intent = new Intent(QRCodeScanActivity.this, CertCompletionActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            QRScanDialog qrScanDialog = new QRScanDialog(QRCodeScanActivity.this);
                            qrScanDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                            qrScanDialog.setDialogListener(new QRScanDialog.QRScanDialogListener() {
                                @Override
                                public void onRetryClicked() {
                                    button.performClick();
                                }

                                @Override
                                public void onCancleClicked() {
                                    qrScanDialog.dismiss();
                                }
                            });
                            qrScanDialog.show();
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseSelectModel> call, Throwable t) {
                        Log.e("fail", "fail");
                    }

                });
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data); }
    }
}