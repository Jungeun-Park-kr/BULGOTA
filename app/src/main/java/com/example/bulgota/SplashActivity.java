package com.example.bulgota;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        LottieAnimationView aniSplash = findViewById(R.id.ani_splash);
        setUpAnimation(aniSplash);

//        Typeface typeface_regular=Typeface.createFromAsset(getAssets(), "nanum_square_round_regular.ttf");
//        Typeface typeface_bold=Typeface.createFromAsset(getAssets(), "nanum_square_round_bold.ttf");
//        tvTestingTitle.setTypeface(typeface_bold);
//        tvTestingInfo.setTypeface(typeface_regular);
    }

    private void setUpAnimation(LottieAnimationView animview) {
        //재생할 애니메이션
        animview.setAnimation("splashani3.json");
        //반복횟수 지정 : 무한
        animview.setRepeatCount(LottieDrawable.RESTART); //아니면 횟수 지정
        //시작
        animview.playAnimation();

    }
}
