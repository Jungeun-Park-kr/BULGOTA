package com.example.bulgota;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.Timer;


public class ReturnModelDialog extends Dialog {
    //init
    private Context context;

    private int usageFee;

    public ReturnModelDialog(Context context) {
        super(context);
        this.context = context;
    }


    protected void setReturnModelDialog(final int returnStatus, final String modelNum, final int usageTime) {

        switch(returnStatus) {
            case 0:
                setContentView(R.layout.dialog_success_return);

                TextView tvReturnModelNum = (TextView) findViewById(R.id.tv_return_modelNum);
                TextView tvUsageTime = findViewById(R.id.tv_usage_time);
                TextView tvUsageFee = findViewById(R.id.tv_usage_fee);

                //setView(modelNum);
                tvReturnModelNum.setText("모델명 : "+modelNum);
                tvUsageTime.setText("이용시간 : " +usageTime);

                if(usageTime <= 10) {
                    usageFee = 1000;
                } else {
                    usageFee = 1000 + (usageTime-10)*100;
                }

                tvUsageFee.setText(("이용요금 : "+usageFee+"원"));
                break;
            case 1:
                setContentView(R.layout.dialog_already_return);
                break;
            case 2:
                setContentView(R.layout.dialog_fail_return);
        }
        show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(context,DeviceMapActivity.class);
                context.startActivity(intent);
                dismiss();
            }
        }, 3000);
    }
}
