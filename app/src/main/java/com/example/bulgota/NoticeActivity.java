package com.example.bulgota;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.widget.LocationButtonView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NoticeActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PAGE_UP = 8;
    private static final int PAGE_LEFT = 4;
    private static final int PAGE_RIGHT = 6;
    private static final int PAGE_DOWN = 2;

    //상수로 페이지 넘버 설정
    private static final int PLAN_BACKGROUND = 0;
    private static final int LEGAL = 1;
    private static final int GUIDE = 2;

    private int tag=0;

    private ImageView btnHamberger;

    private View viewLayer;

    private ConstraintLayout clHamberger;
    private ConstraintLayout clToolbar;

    private Animation translateRightAim;
    private Animation translateLeftAim;

    private boolean isHambergerOpen = false;

    private int pageValue;

    private SlidingPageAnimationListener animationListener;

    //기획 배경 등 텍스트를 클릭하면 해당 항목의 공지사항으로 이동할 예정.
    private TextView tvPlanBackground;
    private TextView tvLegal;
    private TextView tvGuide;
    private TextView tvToolbarTitle;
    private TextView tvHambergerTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notice);

        //기획 배경 등 텍스트를 클릭하면 해당 항목의 공지사항으로 이동할 예정.
        tvPlanBackground = (TextView)findViewById(R.id.tv_tab_list_1);
        tvPlanBackground.setOnClickListener(this);
        tvLegal = (TextView)findViewById(R.id.tv_tab_list_2);
        tvLegal.setOnClickListener(this);
        tvGuide = (TextView)findViewById(R.id.tv_tab_list_3);
        tvGuide.setOnClickListener(this);
        tvToolbarTitle = (TextView)findViewById(R.id.tv_toolbar_title);
        tvToolbarTitle.setOnClickListener(this);
        tvHambergerTitle = (TextView)findViewById(R.id.tv_hamberger_title);
        tvHambergerTitle.setOnClickListener(this);

        Intent intent = getIntent();

        tag = intent.getIntExtra("tag", 0);

        setContent(tag);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        setHamberger();

    }

    @Override
    public void onBackPressed() {
        if(isHambergerOpen) {
            viewLayer.performClick();
            return;
        } else {
            super.onBackPressed();
        }
    }

    private void setHamberger() {
        clHamberger = findViewById(R.id.cl_hamberger);
        clToolbar = findViewById(R.id.cl_toolbar);
        btnHamberger = findViewById(R.id.btn_hamberger);
        viewLayer = findViewById(R.id.view_layer);

        btnHamberger.setOnClickListener(l -> {
            //애니메이션 준비
            translateRightAim = AnimationUtils.loadAnimation(this, R.anim.translate_right);
            translateRightAim.setAnimationListener(animationListener);

            pageValue = PAGE_RIGHT;
            clHamberger.setAnimation(translateRightAim);
            clHamberger.setVisibility(View.VISIBLE);
            viewLayer.setVisibility(View.VISIBLE);

        });

        viewLayer.setOnClickListener((l -> {
            translateLeftAim = AnimationUtils.loadAnimation(this, R.anim.translate_left);
            translateLeftAim.setAnimationListener(animationListener);

            pageValue = PAGE_LEFT;
            clHamberger.startAnimation(translateLeftAim);
            clHamberger.setVisibility(View.GONE);
            viewLayer.setVisibility(View.GONE);
        }));
    }

    private class SlidingPageAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {
            switch (pageValue) {
                case PAGE_DOWN : {
                }
                case PAGE_UP : {
                    break;
                }
                case PAGE_LEFT : {
                    clHamberger.setVisibility(View.GONE);
                    viewLayer.setVisibility(View.GONE);
                    isHambergerOpen = false;
                    break;
                }
                case PAGE_RIGHT : {
                    viewLayer.setVisibility(View.VISIBLE);
                    isHambergerOpen = true;
                }
            }
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            switch (pageValue) {
                case PAGE_DOWN : {
                }
                case PAGE_UP : {
                    break;
                }
                case PAGE_LEFT : {
                    clHamberger.setVisibility(View.GONE);
                    //viewLayer.setVisibility(View.GONE);
                    break;
                }
                case PAGE_RIGHT : {
                    clHamberger.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    @Override
    public void onClick(View v) {
        if(v == tvPlanBackground){
            if(tag != PLAN_BACKGROUND) {
                tag = PLAN_BACKGROUND;
                viewLayer.performClick();
                setContent(tag);
            }
        } else if(v == tvLegal){
            if(tag != LEGAL) {
                tag = LEGAL;
                viewLayer.performClick();
                setContent(tag);
            }
        } else if(v == tvGuide){
            if(tag != GUIDE) {
                tag = GUIDE;
                viewLayer.performClick();
                setContent(tag);
            }
        } else if(v == tvToolbarTitle){
            onBackPressed();
            onBackPressed();
        } else if(v == tvHambergerTitle){
            onBackPressed();
        }

    }

    public void setContent(int tag) {
        tvPlanBackground.setTextColor(0xFF000000);
        tvLegal.setTextColor(0xFF000000);
        tvGuide.setTextColor(0xFF000000);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if(tag == PLAN_BACKGROUND){
            tvPlanBackground.setTextColor(0xFF4EBFDE);
            PlanBackgroundFragment fr = new PlanBackgroundFragment();
            transaction.replace(R.id.fl_content, fr);
            transaction.commit();
        } else if(tag == LEGAL){
            tvLegal.setTextColor(0xFF4EBFDE);
            LegalFragment fr = new LegalFragment();
            transaction.replace(R.id.fl_content, fr);
            transaction.commit();
        } else if(tag == GUIDE){
            tvGuide.setTextColor(0xFF4EBFDE);
            GuideFragment fr = new GuideFragment();
            transaction.replace(R.id.fl_content, fr);
            transaction.commit();
        }

        if(isHambergerOpen == true) {
            viewLayer.performClick();
            isHambergerOpen = false;
        }
    }
}
