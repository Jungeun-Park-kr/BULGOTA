package com.example.bulgota;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

public class LendFailDialog extends Dialog {
    //init
    private TextView tvLendModelNum;
    private TextView tvLendTime;
    private Context context;

    public LendFailDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_fail_lend);
    }
}
