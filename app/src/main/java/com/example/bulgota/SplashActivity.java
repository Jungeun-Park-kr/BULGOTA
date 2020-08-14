package com.example.bulgota;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

public class SplashActivity extends AppCompatActivity {
    LottieAnimationView aniSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Lottie Animation
        aniSplash = (LottieAnimationView)findViewById(R.id.ani_splash);
        aniSplash.setAnimation("splashani.json");
        aniSplash.setRepeatCount(LottieDrawable.INFINITE);
        //Lottie Animation start
        aniSplash.playAnimation();
    } //view 객체 획득
}
