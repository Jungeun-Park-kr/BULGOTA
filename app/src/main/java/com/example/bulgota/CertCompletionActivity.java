package com.example.bulgota;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class CertCompletionActivity extends AppCompatActivity {
    CheckBox checkSafety;
    ImageView imgCheckLine;
    Button btnUse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cert_completion);

        checkSafety = findViewById(R.id.check_safety);
        btnUse = findViewById(R.id.btn_use);
        imgCheckLine = findViewById(R.id.img_check_line);

        btnUse.setOnClickListener(l -> {
            if(!checkSafety.isChecked()) {
                imgCheckLine.setVisibility(View.VISIBLE);
            } else {

            }
        });

    }
}