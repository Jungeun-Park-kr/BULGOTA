package com.example.bulgota;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bulgota.api.BullgoTAService;
import com.example.bulgota.api.ResponseLendModel;
import com.example.bulgota.api.ResponseSelectModel;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CertCompletionActivity extends AppCompatActivity {
    CheckBox checkSafety;
    ImageView imgCheckLine;
    Button btnUse;

    private String modelName;
    private String lendTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cert_completion);

        checkSafety = findViewById(R.id.check_safety);
        btnUse = findViewById(R.id.btn_use);
        imgCheckLine = findViewById(R.id.img_check_line);

        //Realm 객체 선언
        Realm.init(this);
        Realm mRealm = Realm.getDefaultInstance();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) { //인증 화면으로 넘어온 경우, 이전에 있던 남은 해독시간 데이터 삭제
                mRealm.delete(TimeVO.class); //모든 데이터 삭제
            }
        });

//        Log.d("modelName", getIntent().getStringExtra("modelName")); //모델이름 확인용
//        modelName = getIntent().getStringExtra("modelName"); //모델네임 저장
        modelName = "BGT_004";

        btnUse.setOnClickListener(l -> {
            if(!checkSafety.isChecked()) {
                imgCheckLine.setVisibility(View.VISIBLE);
            } else {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BullgoTAService.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                BullgoTAService bullgoTAService = retrofit.create(BullgoTAService.class);
                bullgoTAService.lendModel(modelName).enqueue(new Callback<ResponseLendModel>() {

                    @Override
                    public void onResponse(Call<ResponseLendModel> call, Response<ResponseLendModel> response) {
                        if (response.body().getSuccess()) {
                            //대여 성공

                            Object data = response.body().getData();
                            lendTime = data.toString();

                            LendSuccessDialog lendSuccessDialog = new LendSuccessDialog(CertCompletionActivity.this);
                            lendSuccessDialog.setLendSuccessDialog(modelName, lendTime);
                        } else {
                            //대여 실패
                            LendFailDialog lendFailDialog = new LendFailDialog(CertCompletionActivity.this);
                            lendFailDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                            lendFailDialog.show();
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseLendModel> call, Throwable t) {
                    }

                });
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(CertCompletionActivity.this,DeviceMapActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, 3000);

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}