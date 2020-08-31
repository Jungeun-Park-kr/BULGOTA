package com.example.bulgota;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;


public class LendSuccessDialog {
    //init
    private TextView tvLendModelNum;
    private TextView tvLendTime;
    private TextView tvPwdValue;
    private TextView tvPassword;
    private Context context;

    public LendSuccessDialog(Context context) {
        this.context = context;
    }

    protected void setLendSuccessDialog(final String modelNum, final String lendTime, final String password) {
        final Dialog lendSuccessDialog = new Dialog(context);

        lendSuccessDialog.setContentView(R.layout.dialog_success_lend);

        tvLendModelNum = lendSuccessDialog.findViewById(R.id.tv_lend_modelNum);
        tvLendTime = lendSuccessDialog.findViewById(R.id.tv_lend_time);
        tvPwdValue = lendSuccessDialog.findViewById(R.id.tv_pwd_value);
        tvPassword = lendSuccessDialog.findViewById(R.id.tv_password);

        tvPassword.setText(modelNum + " 모델의 자물쇠 비밀번호는");
        tvPwdValue.setText(password);
        tvLendModelNum.setText("모델명 : " +modelNum);
        tvLendTime.setText("이용시작시간 : " +lendTime);

        lendSuccessDialog.show();

    }
}
