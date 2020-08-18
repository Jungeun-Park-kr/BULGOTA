package com.example.bulgota;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class CertCompletionActivity extends AppCompatActivity {
    CheckBox checkSafety;
    TextView tvCheckWarning;
    Button btnUse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cert_completion);

        checkSafety = findViewById(R.id.check_safety);
        tvCheckWarning = findViewById(R.id.tv_check_warning);
        btnUse = findViewById(R.id.btn_use);

        btnUse.setOnClickListener(l -> {
            if(!checkSafety.isChecked()) {
                tvCheckWarning.setTextColor(Color.RED);
            } else {

            }
        });

    }
}