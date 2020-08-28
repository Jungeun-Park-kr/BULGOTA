package com.example.bulgota;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.Animator;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.bulgota.api.BullgoTAService;
import com.example.bulgota.api.ResponseSelectModel;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class BreatheTestingActivity extends AppCompatActivity implements View.OnClickListener{
    private String modelName;

    boolean isPageSlide = false; //페이지가 넘어갔는지 확인하는 변수
    boolean isTooFar = false; //측정기와의 거리를 확인하는 변수 (10cm 이상일시 true)
    Animation translateDownAnim; //화면 슬라이드 애니메이션
    Animation translateUpAnim;
    ConstraintLayout testPage; //측정 레이아웃
    ConstraintLayout connectPage; //연결 레이아웃
    //페이지 슬라이딩 이벤트 발생시, 애니메이션이 시작 됐는지 종료됐는지 감지하는 리스너
    private BreatheTestingActivity.SlidingPageAnimationListener animationListener;

    private BluetoothSPP btSpp; //블루투스 통신
    MyTimer myTimer;    //타이머객체
    public static LottieAnimationView lottieBreathTesting; //(로딩모양)측정중 로띠
    public static LottieAnimationView lottieAnalyzing; //(점모양)분석중 로띠
    Button btnTesting; //측정시작 버튼
    Button btnConnect; //연결하기 버튼
    TextView tvBigInfo; //큰 안내문(아이콘 아래)
    TextView tvSmallInfoM; //작은 안내문 (버튼 위 - 연결화면)
    TextView tvSmallInfoC; //작은 안내문 (버튼 위 - 측정화면)



    private boolean isTesting = false; //현재 사용자가 측정중인지 확인하는 변수
    private boolean result = true; //음주 측정 결과값 저장
    double dValue; ////측정값 가져오기 (mg/L) , 혈중알코올 농도:mg/100mL

    private IntentIntegrator qrScan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breathe_testing);


        lottieBreathTesting = findViewById(R.id.lottie_breathing); //측정중 로띠
        lottieAnalyzing = findViewById(R.id.lottie_analyzing); //분석중 로띠
        btnTesting = findViewById(R.id.btn_measure_start); //측정시작 버튼
        btnConnect = findViewById(R.id.btn_connect_start); //연결하기 버튼
        tvBigInfo = findViewById(R.id.tv_testing_info); //아이콘 아래 큰 안내문
        tvSmallInfoC = findViewById(R.id.tv_testing_small_info); //버튼 위 작은 안내문 (연결화면)
        tvSmallInfoM = findViewById(R.id.tv_testing_small_info2); //버튼 위 작은 안내문 (측정화면)
        btnTesting.setOnClickListener(this);

        //슬라이드할 뷰
        testPage = findViewById(R.id.cl_test_view);  //측정 뷰
        connectPage = findViewById(R.id.cl_connect_view); //연결 뷰

        //페이지 슬라이딩 이벤트 발생시, 애니메이션이 시작 됐는지 종료됐는지 감지하는 리스너
        translateDownAnim = AnimationUtils.loadAnimation(this, R.anim.move_down);
        //translateUpAnim = AnimationUtils.loadAnimation(this, R.anim.move_up);
  

        Log.d("modelName", getIntent().getStringExtra("modelName"));//대여하기 눌렀을 때 넘어오는 모델명
        modelName = getIntent().getStringExtra("modelName"); //모델네임 저장

        //기기명과 함께 연결하기 안내 메시지
        tvSmallInfoC.setText("아래의 연결하기 버튼을 눌러\n"+modelName+"와 연결해주세요.");


        btnTesting.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnTesting.setEnabled(false); //버튼 다시 못누르게 false

                tvSmallInfoM.setText("3초 후에 측정을 시작합니다.");
                myTimer = new MyTimer(3000, 1000);
                myTimer.start();

            }
        });

        btSpp = new BluetoothSPP(this); //초기화

        if (!btSpp.isBluetoothAvailable()) { //블루투스 사용 불가능할 경우
            Toast.makeText(getApplicationContext()
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }

        btSpp.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //데이터 수신하기
            /* 아두이노에서 넘어온 데이터 수신 : 아두이노는 1byte씩 데이터 수신됨.
             *   따라서 아두이노에서 온 데이터를 넣어 바이트를 모두 합친 후 msg를 통해 return됨
             *   즉 우리가 사용할 것 : msg!! // 아래는 msg를 토스트로 띄운 예시
             */

            @Override
            public void onDataReceived(byte[] data, String msg) { //데이터 수신 받을 때
                dValue = Double.parseDouble(msg); //수신받은 음주측정 값 저장
                //tvValue.setText("측정값:" + dValue);

                if (isTesting) {
//                    Toast.makeText(getApplicationContext()
//                            , "넘어오는값 :"+dValue
//                            , Toast.LENGTH_SHORT).show();
                    if (dValue < -100.00) { //현재 측정 중이고, 10cm 보다 멀리 있는 경우
                        isTooFar = true;
                    }
                    else
                        isTooFar = false;
                    if(dValue >= 0.03) { //현재 측정중이고 BAC가 0.03이상인 경우
                        result = false;
                    }
                }

            }
        });

        btSpp.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() { //연결됐을 때
            @Override
            public void onDeviceConnected(String name, String address) {
                //연결한 블루투스 이름이 사용자가 대여하기 누른 기기이름과 동일한지 확인
                //DeviceMapActivity에 있는 모델 변수 이름) markerDataList.get(finalI).getModel()
                //String modelName = getIntent().getStringExtra("modelName");
                if(!name.equals(modelName)) { //맵에서 선택한 모델명과 선택한 블루투스 이름이 다르면 연결 해제
                    Toast.makeText(getApplicationContext()
                            , "(연결실패) 선택한 킥보드 ("+modelName+")와 같은 기기를 다시 선택해주세요."
                            , Toast.LENGTH_SHORT).show();
                    btSpp.disconnect();
                }
                else
                    { //이름 같으면 마저 연결 진행
                    Toast.makeText(getApplicationContext()
                            , "Connected to " + name + "\n" + address
                            , Toast.LENGTH_SHORT).show(); //연결 확인 토스트 띄우기

                    //연결완료시 화면 전환
                    if (!isPageSlide) { //화면 전환이 안된 경우
                        //애니메이션 사용할 준비
                        translateDownAnim.setAnimationListener(animationListener);
                        //translateUpAnim.setAnimationListener(animationListener);
                        testPage.setVisibility(View.VISIBLE); //테스트 페이지 보이게 하기
                        testPage.startAnimation(translateDownAnim); //페이지 슬라이딩 애니메이션 시작
                        //connectPage.startAnimation(translateUpAnim); //연결 페이지 사라지기 애니메이션 시작
                    }
                }

            }

            @Override
            public void onDeviceDisconnected() { //연결 해제
                Toast.makeText(getApplicationContext()
                        , "Connection lost", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeviceConnectionFailed() { //연결 실패
                Toast.makeText(getApplicationContext()
                        , "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        });

        ////////////////////////////////////////////////////////////////연결 시도

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btSpp.getServiceState() == BluetoothState.STATE_CONNECTED) { //이미 연결되어있으면 해제
                    btSpp.disconnect();
                } else {
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }
            }
        });
    }

    private class SlidingPageAnimationListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) { //화면 전환 된 경우
            isPageSlide = true;
            connectPage.setVisibility(View.GONE); //연결 완료시 연결 레이아웃 없애기


        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }


    public void onDestroy() {
        super.onDestroy();
        btSpp.stopService(); //블루투스 중지
    }

    public void onStart() {
        super.onStart();
        if (!btSpp.isBluetoothEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!btSpp.isServiceAvailable()) {
                btSpp.setupService();
                btSpp.startService(BluetoothState.DEVICE_OTHER); //아두이노와 연결 / DEVICE_ANDROID는 안드로이드 기기 끼리

            }
        }
    }


    public void measure() {

        Button btnMeasure = findViewById(R.id.btn_measure_start); //
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("qrcode data", String.valueOf(data));
        Log.e("qrcode requestCode", String.valueOf(requestCode));
        Log.e("qrcode resultCode", String.valueOf(resultCode));
        if(requestCode == 49374) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if(result != null) {
                //qrcode가 없으면
                if (result.getContents() == null) {

                } else {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(BullgoTAService.BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    BullgoTAService bullgoTAService = retrofit.create(BullgoTAService.class);
                    Log.e("result.getContent2", result.getContents());
                    bullgoTAService.checkModel(result.getContents()).enqueue(new Callback<ResponseSelectModel>() {
                        //bulgotaservice return mode
                        //responseReturnModel
                        //95 getMessage()변경
                        //반납가능시 0 이미반납된 모델이면 1 반납실패 2
                        @Override
                        public void onResponse(Call<ResponseSelectModel> call, Response<ResponseSelectModel> response) {
                            Log.e("getSuccess", String.valueOf(response.body().getSuccess()));

                            if (response.body().getSuccess()) {
                                //유효한 모델이면
                                Intent intent = new Intent(BreatheTestingActivity.this, CertCompletionActivity.class);
                                intent.putExtra("modelName", result.getContents());
                                startActivity(intent);
                                finish();
                            } else {
                                QRScanDialog qrScanDialog = new QRScanDialog(BreatheTestingActivity.this);
                                qrScanDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                                qrScanDialog.setDialogListener(new QRScanDialog.QRScanDialogListener() {
                                    @Override
                                    public void onRetryClicked() {
                                        scanQRcode();
                                    }

                                    @Override
                                    public void onCancleClicked() {
                                        qrScanDialog.dismiss();
                                    }
                                });
                                qrScanDialog.show();
                            }
                        }
                        @Override
                        public void onFailure(Call<ResponseSelectModel> call, Throwable t) {
                            Log.e("fail", "fail");
                        }

                    });
                }
            }
        }

        else {
            if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
                if (resultCode == Activity.RESULT_OK)
                    btSpp.connect(data);
            } else if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
                if (resultCode == Activity.RESULT_OK) {
                    btSpp.setupService();
                    btSpp.startService(BluetoothState.DEVICE_OTHER);
                    // setup();
                } else {
                    Toast.makeText(getApplicationContext()
                            , "Bluetooth was not enabled."
                            , Toast.LENGTH_SHORT).show();
                    //finish();
                }
            }
        }
    }

    private void setUpAnimationMeasuring(final LottieAnimationView animview) { //측정중 로띠
        //재생할 애니메이션
        animview.setAnimation("lottie_breath_testing.json");
        //반복횟수 지정
        animview.setRepeatCount(1); //횟수 지정
        //시작
        animview.playAnimation();

        animview.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                isTesting = true; //측정 시작

            }

            @Override
            public void onAnimationEnd(Animator animator) { //끝난 경우
                isTesting = false; //측정 종료
//                Toast.makeText(getApplicationContext()
//                        , "isTooFar : "+isTooFar+"\ndValue : "+dValue
//                        , Toast.LENGTH_SHORT).show();
                if(isTooFar) { //측정 거리가 너무 멀 경우
//                    Toast.makeText(getApplicationContext()
//                            , "(측정 실패) 측정기와의 거리를 10cm 이내로 해주세요."
//                            , Toast.LENGTH_SHORT).show();

                    tvSmallInfoM.setText("측정기와의 거리를 10cm 이내로 해주세요.");

                    btnTesting.setEnabled(true); //버튼 다시 누를 수 있게 하기
                    btnTesting.setText("측 정 재 시 작");
                    if (btnTesting.getVisibility() == INVISIBLE) //측정중 버튼 다시 보이게 하기
                        btnTesting.setVisibility(View.VISIBLE);

                    if (lottieBreathTesting.getVisibility() == VISIBLE) { //측정중 로띠 다시 없애기
                        lottieBreathTesting.setVisibility(INVISIBLE);
                    }

                }
                else {
                    animview.setVisibility(INVISIBLE);
                    lottieAnalyzing.setVisibility(VISIBLE);
                    tvSmallInfoM.setText("측정 결과를 분석중입니다.");
                    setUpAnimationAnalyzing(lottieAnalyzing);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                animview.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    private void setUpAnimationAnalyzing(final LottieAnimationView animview) { //분석중 로띠
        //재생할 애니메이션
        animview.setAnimation("lottie_dots.json");
        animview.setSpeed((float)0.8);
        //반복횟수 지정
        animview.setRepeatCount(1); //횟수 지정
        //시작
        animview.playAnimation();

        animview.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) { //끝난 경우

                animview.setVisibility(INVISIBLE);

                if(!isTooFar)
                    analysisResult(); //측정 결과에 따라 화면 이동하는 메소드
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                animview.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
                animview.removeAllLottieOnCompositionLoadedListener();
            }
        });
    }

    private void analysisResult() { //측정 결과 확인후 처리하는 메소드
        //큐알코드 화면 이동 or 분석화면 이동
        if (result) { //측정결과 - 운전 가능
            //initializing scan object
            scanQRcode();
        }
        else { //측정결과 - 운전 불가
            Toast.makeText(this, "분석결과 운전 불가합니다.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this,DetoxAnalysisActivity.class);
            intent.putExtra("bac", dValue);
            startActivity(intent);
            finish();
        }
    }

    private void scanQRcode() {
        qrScan = new IntentIntegrator(this);

        //scan option
        qrScan.setBeepEnabled(false);
        qrScan.setCaptureActivity(QReaderActivity.class);
        qrScan.setOrientationLocked(false);
        qrScan.initiateScan();
    }

    class MyTimer extends CountDownTimer {

        public MyTimer(long millisInFuture, long countDowninterval) {
            super(millisInFuture, countDowninterval);
        }
        @Override
        public void onTick(long millisUntilFinished) {
            tvSmallInfoM.setText((millisUntilFinished/1000)+1 + " 초 후에 측정을 시작합니다.");
        }

        @Override
        public void onFinish() {
            tvSmallInfoM.setText("3초간 바람을 세게 불어주세요!");
            //isTesting = true; //측정 시작 인지 확인

                if (btnTesting.getVisibility() == VISIBLE) { //측정중 버튼 없애기
                    btnTesting.setVisibility(INVISIBLE);
                }

                if (lottieBreathTesting.getVisibility() == INVISIBLE) { //측정중 로띠 띄우기
                    lottieBreathTesting.setVisibility(VISIBLE);
                    setUpAnimationMeasuring(lottieBreathTesting);
                }

        }
    }



//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent intent = new Intent(getApplicationContext(),)
//    }

    @Override
    public void onClick(View v) {
//        if(v == btnMeasureActivity) {
//            Intent intent = new Intent(this,BreathTestingActivity.class);
//            startActivity(intent);
//        }
//        else if (v== btnTesting) {
//            Intent intent = new Intent(this,TestingActivity.class);
//            startActivity(intent);
//        }

    }



}