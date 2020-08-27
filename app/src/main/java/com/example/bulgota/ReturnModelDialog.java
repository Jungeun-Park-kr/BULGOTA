package com.example.bulgota;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import java.util.Timer;


public class ReturnModelDialog {
    //init
    private TextView tvReturnModelNum;
    private TextView tvUsageTime;
    private TextView tvUsageFee;
    private Context context;

    private int usageFee;

    public ReturnModelDialog(Context context) {
        this.context = context;
    }

    protected void setReturnModelDialog(final int returnStatus, final String modelNum, final int usageTime) {
        final Dialog returnModelDialog = new Dialog(context);

        switch(returnStatus) {
            case 0:
                returnModelDialog.setContentView(R.layout.dialog_success_lend);

                tvReturnModelNum = returnModelDialog.findViewById(R.id.tv_return_modelNum);
                tvUsageTime = returnModelDialog.findViewById(R.id.tv_usage_time);
                tvUsageFee = returnModelDialog.findViewById(R.id.tv_usage_fee);

                tvReturnModelNum.setText("모델명 : " +modelNum);
                tvUsageTime.setText("이용시간 : " +usageTime);

                if(usageTime <= 10) {
                    usageFee = 1000;
                } else {
                    usageFee = 1000 + (usageTime-10)*100;
                }

                tvUsageFee.setText(("이용요금 : "+usageFee+"원"));
                break;
            case 1:
                returnModelDialog.setContentView(R.layout.dialog_already_return);
                break;
            case 2:
                returnModelDialog.setContentView(R.layout.dialog_fail_return);
        }
        returnModelDialog.show();
    }
}
