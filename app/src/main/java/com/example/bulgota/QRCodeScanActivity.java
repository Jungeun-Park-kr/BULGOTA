package com.example.bulgota;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bulgota.api.BullgoTAService;
import com.example.bulgota.api.Marker_list;
import com.example.bulgota.api.ResponseSelectModel;
import com.example.bulgota.api.ResponseWithMarkerData;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

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
                qrScan.setPrompt("킥보드에 부착된 QR코드를 스캔해주세요.");
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
                Toast.makeText(QRCodeScanActivity.this, "취소!", Toast.LENGTH_SHORT).show();
            } else {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BullgoTAService.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                BullgoTAService bullgoTAService = retrofit.create(BullgoTAService.class);
                bullgoTAService.checkModel(result.getContents()).enqueue(new Callback<ResponseSelectModel>() {
                    @Override
                    public void onResponse(Call<ResponseSelectModel> call, Response<ResponseSelectModel> response) {
                        if (response.body().getSuccess()) {
                            //유효한 모델이면
                            Toast.makeText(QRCodeScanActivity.this, "스캔완료!", Toast.LENGTH_SHORT).show();
                            textView.setText(result.getContents());
                        } else {
                            textView.setText("존재하지 않는 모델명입니다.");
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseSelectModel> call, Throwable t) {
                    }

                });
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data); }
    }
}