package com.example.bulgota;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;

public class QRScanDialog extends Dialog implements View.OnClickListener {

    //init
    private Button btnRetry;
    private Context context;
    private IntentIntegrator qrScan;

    private QRScanDialogListener qrScanDialogListener;

    public QRScanDialog(Context context) {
        super(context);
        this.context = context;
    }

    //인터페이스 설정
    interface QRScanDialogListener {
        void onRetryClicked();
        void onCancleClicked();
    }

    //호출할 리스너 초기화
    public void setDialogListener(QRScanDialog.QRScanDialogListener qrScanDialogListener){
        this.qrScanDialogListener = qrScanDialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_qr_scan);

        btnRetry = (Button) findViewById(R.id.btn_retry);


        //버튼 클릭 리스너 등록
        btnRetry.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_retry) {
            qrScanDialogListener.onRetryClicked();
            dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        qrScanDialogListener.onCancleClicked();
    }
}


