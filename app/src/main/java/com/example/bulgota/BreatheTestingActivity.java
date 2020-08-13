package com.example.bulgota;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

public class BreatheTestingActivity extends AppCompatActivity {

    public static LottieAnimationView lottieBreathTesting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breathe_testing);

//        TextView tvTestingTitle = findViewById(R.id.tv_testing_title);
//        TextView tvTestingInfo = findViewById(R.id.tv_breathe_testing_info);
        LottieAnimationView lottieTesting = findViewById(R.id.lottie_breathe_testing);
        setUpAnimation(lottieTesting);

//        Typeface typeface_regular=Typeface.createFromAsset(getAssets(), "nanum_square_round_regular.ttf");
//        Typeface typeface_bold=Typeface.createFromAsset(getAssets(), "nanum_square_round_bold.ttf");
//        tvTestingTitle.setTypeface(typeface_bold);
//        tvTestingInfo.setTypeface(typeface_regular);
    }

    private void setUpAnimation(LottieAnimationView animview) {
        //재생할 애니메이션
        animview.setAnimation("lottie_breathe_testing.json");
        //반복횟수 지정 : 무한
        animview.setRepeatCount(LottieDrawable.INFINITE); //아니면 횟수 지정
        //시작
        animview.playAnimation();

    }
}