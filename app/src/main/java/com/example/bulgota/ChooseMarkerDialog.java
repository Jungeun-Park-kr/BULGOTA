package com.example.bulgota;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

public class ChooseMarkerDialog extends Dialog {

    //init
    private ImageView btnClose;
    private Context context;

    public ChooseMarkerDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_choose_marker);

        btnClose = (ImageView)findViewById(R.id.btn_close);

        //버튼 클릭 리스너 등록
        btnClose.setOnClickListener(l -> {
            dismiss();
        });
    }
}


