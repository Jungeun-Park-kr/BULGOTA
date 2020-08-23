package com.example.bulgota;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

public class ChooseMarkerDialog extends Dialog {

    //init
    private TextView tvOK;
    private Context context;

    public ChooseMarkerDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_choose_marker);

        tvOK = (TextView) findViewById(R.id.tv_ok);

        //버튼 클릭 리스너 등록
        tvOK.setOnClickListener(l -> {
            dismiss();
        });
    }
}


