package com.example.bulgota;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;


public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        ImageView aniSplash = (ImageView)findViewById(R.id.ani_splash);


        Glide
                .with(this)
                .load(R.raw.anigif)
                .into(aniSplash);
        startLoading();
    }

    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), DeviceMapActivity.class);
                startActivity(intent);
                finish();
            }
        }, 5000);
    }

}
