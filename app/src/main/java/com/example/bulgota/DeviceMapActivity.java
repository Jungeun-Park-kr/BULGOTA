package com.example.bulgota;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bulgota.api.BullgoTAService;
import com.example.bulgota.api.Marker_list;
import com.example.bulgota.api.RequestReturnModel;
import com.example.bulgota.api.ResponseReturnModel;
import com.example.bulgota.api.ResponseWithMarkerData;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.CircleOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.widget.LocationButtonView;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DeviceMapActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {
    //퍼미션 리스트
    private static String[] permission_list = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA
    };
    private static final int request_code = 0;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private static final int PAGE_UP = 8;
    private static final int PAGE_LEFT = 4;
    private static final int PAGE_RIGHT = 6;
    private static final int PAGE_DOWN = 2;

    //상수로 넘어갈 공지사항 페이지 넘버 설정
    private static final int PLAN_BACKGROUND = 0;
    private static final int LEGAL = 1;
    private static final int GUIDE = 2;

    private MapView mapView;
    private FusedLocationSource locationSource;
    private NaverMap map;

    private Marker lastMarker;
    private Marker[] markerItems;

    private Button btnHomeLend;
    private Button btnInfoLend;
    private Button btnHomeZoomIn;
    private Button btnHomeZoomOut;
    private Button btnInfoZoomIn;
    private Button btnInfoZoomOut;
    private Button btnReturn;

    private ImageView btnHamberger;

    private View viewLayer;

    private ConstraintLayout clHamberger;
    private ConstraintLayout clModelInfo;
    private ConstraintLayout clToolbar;

    private Animation translateUpAim;
    private Animation translateDownAim;
    private Animation translateRightAim;
    private Animation translateLeftAim;

    private boolean initMapLoad = true;
    private boolean isInfoPageOpen = false;
    private boolean isHambergerOpen = false;

    private LocationButtonView btnHomeLocation;
    private LocationButtonView btnInfoLocation;

    private int pageValue;
    private String modelName;

    private SlidingPageAnimationListener animationListener;

    //기획 배경 등 텍스트를 클릭하면 해당 항목의 공지사항으로 이동할 예정.
    private TextView tvPlanBackground;
    private TextView tvLegal;
    private TextView tvGuide;

    private IntentIntegrator qrScan;

    //현재시간 변수 추가
    final int CURSECOND=0, CURMINUTE=1, CURHOUR=2, CURDATE=3, CURMONTH=4, CURYEAR=5;
    int curTime[] = new int[6];
    //해독시간 텍스트뷰
    TextView tvDetoxTime;
    //

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_device_map);


        //퍼미션 확인
        if(DeviceMapActivity.checkPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)
                &&(DeviceMapActivity.checkPermissions(this, Manifest.permission.ACCESS_COARSE_LOCATION))
                &&(DeviceMapActivity.checkPermissions(this, Manifest.permission.CAMERA))) {
            //권한 있음 - 원하는 메소드 사용
            Toast.makeText(this, "권한 설정이 완료되었습니다.", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this, "권한 하나이상 없음.", Toast.LENGTH_LONG).show();
            DeviceMapActivity.requestExternalPermissions(this);
        }

        //기획 배경 등 텍스트를 클릭하면 해당 항목의 공지사항으로 이동할 예정.
        tvPlanBackground = (TextView)findViewById(R.id.tv_tab_list_1);
        tvPlanBackground.setOnClickListener(this);
        tvLegal = (TextView)findViewById(R.id.tv_tab_list_2);
        tvLegal.setOnClickListener(this);
        tvGuide = (TextView)findViewById(R.id.tv_tab_list_3);
        tvGuide.setOnClickListener(this);

        //해독시간 텍스트뷰
        tvDetoxTime = (TextView)findViewById(R.id.tv_detox_time);

        //임시 데이터
        int tmpHour = 26; //임시 시 분 초 입니다.
        int tmpMinute = 44;
        int tmpSecond = 25;

        //정보 가져오기
        //여기 코딩 하실?

        //
        //
        String strDetoxTime = "해독 예상 시간은 ";

        if(tmpHour > 24){
            strDetoxTime += "다음날 ";
        }

        strDetoxTime += (tmpHour % 24) + "시 ";
        strDetoxTime += tmpMinute + "분 입니다.";
        tvDetoxTime.setText(strDetoxTime);
        //

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this::onMapReady);

        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
        


        //현재시간 변수 값 설정
        Calendar cal = Calendar.getInstance();
        curTime[CURSECOND] = cal.get(Calendar.SECOND);
        curTime[CURMINUTE] = cal.get(Calendar.MINUTE);
        curTime[CURHOUR] = cal.get(Calendar.HOUR_OF_DAY); // 24시간 넘어가도 ㄱㅊ
        curTime[CURDATE] = cal.get(Calendar.DATE);
        curTime[CURMONTH] = cal.get(Calendar.MONDAY)+1;
        curTime[CURYEAR] = cal.get(Calendar.YEAR);
        //

        //알람 메세지 클릭 시 map으로 이동 변경(firebasemessageservice)
        //firebasemessage service Intent
        //임시로 설정해둔 것이니 추후 논의 후 변경
       receiveMessage();

    }

    //jonghun add code(firebasemessageservice)
    //firebasemessage service Intent
    private void receiveMessage() {
        Intent intent = getIntent();
        if(intent != null) {//푸시알림을 선택해서 실행한것이 아닌경우 예외처리
            String notificationData = intent.getStringExtra("test");
            if(notificationData != null)
                Log.d("FCM_TEST", notificationData);
        }
    }

    @Override
    public void onBackPressed() {
        if(isHambergerOpen) {
            viewLayer.performClick();
            return;
        } else if(isInfoPageOpen) {
            map.getOnMapClickListener().onMapClick(new PointF(10,10), lastMarker.getPosition());
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public static boolean checkPermissions(Activity activity, String permission) {
        int permissionResult = ActivityCompat.checkSelfPermission(activity, permission);
        if (permissionResult == PackageManager.PERMISSION_GRANTED)
            return true;
        else
            return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 0) { //if(requestCode == BreathTestingActivity.request_code)
            if(DeviceMapActivity.verifyPermission(grantResults)) {
                //요청한 권한 얻음, 원하는 메소드 사용
                Toast.makeText(this, "권한 설정이 모두 완료되었습니다.", Toast.LENGTH_LONG).show();
                if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
                    if (!locationSource.isActivated()) {
                        map.setLocationTrackingMode(LocationTrackingMode.None);
                    }
                    map.setLocationSource(locationSource);
                    map.setLocationTrackingMode(LocationTrackingMode.Follow);
                    return;
                }
            }
            else {
                //showRequestAgainDialog();
                Toast.makeText(this, "불고타 서비스 이용을 위해 권한이 필요합니다.", Toast.LENGTH_LONG).show();
            }
        }
        else
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showRequestAgainDialog() {
    }

    public static void requestExternalPermissions(Activity activity) {
        ActivityCompat.requestPermissions(activity, permission_list, request_code);
    }

    public static boolean verifyPermission(int[] grantresults) { //하나라도 허용 안되어있으면 flase리턴
        if (grantresults.length < 1) {
            return false;
        }
        for (int result : grantresults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        map = naverMap;
        animationListener = new SlidingPageAnimationListener();

        mapLoad();
        setHamberger();
        makeUiSetting();
        getMarker();
        makeCircle();
    }

    private void makeUiSetting() {
        btnHomeLend = findViewById(R.id.btn_home_lend);
        btnInfoLend = findViewById(R.id.btn_info_lend);
        btnHomeLocation = findViewById(R.id.btn_home_location);
        btnInfoLocation = findViewById(R.id.btn_info_location);
        btnHomeZoomIn = findViewById(R.id.btn_home_zoom_in);
        btnHomeZoomOut = findViewById(R.id.btn_home_zoom_out);
        btnInfoZoomIn = findViewById(R.id.btn_info_zoom_in);
        btnInfoZoomOut = findViewById(R.id.btn_info_zoom_out);
        btnReturn = findViewById(R.id.btn_return);

        qrScan = new IntentIntegrator(this);

        UiSettings uiSettings = map.getUiSettings();

        uiSettings.setCompassEnabled(false);
        uiSettings.setScaleBarEnabled(false);
        uiSettings.setZoomControlEnabled(false);

        btnHomeLocation.setMap(map);

        btnHomeLend.setOnClickListener(l -> {
            ChooseMarkerDialog dialog = new ChooseMarkerDialog(this);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            dialog.show();
        });

        btnInfoLend.setOnClickListener(l -> {
            Intent intent = new Intent(this,BreatheTestingActivity.class);
            intent.putExtra("modelName", modelName);//측정중 액티비티로 선택한 모델명 전달
            startActivity(intent);
        });

        btnHomeZoomIn.setOnClickListener(l -> {
            btnZoomClickEvent(btnHomeZoomIn, true);
        });

        btnHomeZoomOut.setOnClickListener(l -> {
            btnZoomClickEvent(btnHomeZoomOut, false);
        });

        btnInfoZoomIn.setOnClickListener(l -> {
            btnZoomClickEvent(btnInfoZoomIn, true);
        });

        btnInfoZoomOut.setOnClickListener(l -> {
            btnZoomClickEvent(btnInfoZoomOut, false);
        });

        btnReturn.setOnClickListener(l -> {
            qrScan.setBeepEnabled(false);
            qrScan.setCaptureActivity(QReaderReturnActivity.class);
            qrScan.setOrientationLocked(false);
            qrScan.initiateScan();
        });
    }

    private void btnZoomClickEvent(Button button, boolean zoom) {
        if(zoom) {
            map.moveCamera(CameraUpdate.zoomIn().animate(CameraAnimation.Easing, 1500));
        } else {
            map.moveCamera(CameraUpdate.zoomOut().animate(CameraAnimation.Fly, 1500));
        }
    }

    protected void getMarker() {
        //애니메이션 준비
        translateUpAim = AnimationUtils.loadAnimation(this, R.anim.translate_up);
        clModelInfo = findViewById(R.id.cl_model_info);

        TextView tvModelNum = findViewById(R.id.tv_model_num);
        TextView tvBatteryValue = findViewById(R.id.tv_battery_value);
        TextView tvTimeValue = findViewById(R.id.tv_time_value);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BullgoTAService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        BullgoTAService bullgoTAService = retrofit.create(BullgoTAService.class);
        bullgoTAService.getMarkerAll().enqueue(new Callback<ResponseWithMarkerData>() {
            @Override
            public void onResponse(Call<ResponseWithMarkerData> call, Response<ResponseWithMarkerData> response) {
                if (response.body().getSuccess()) {
                    ArrayList<Marker_list> markerDataList = response.body().getData();

                    if (markerDataList.get(0) == null) {
                        return;
                    }

                    markerItems = new Marker[markerDataList.size()];

                    for (int i = 0; i < markerDataList.size(); i++) {
                        markerItems[i] = new Marker();
                        markerItems[i].setTag(i + 1);
                        markerItems[i].setPosition(new LatLng(markerDataList.get(i).getLatitude(), markerDataList.get(i).getLongitude()));
                        markerItems[i].setIcon(OverlayImage.fromResource(R.drawable.normal_marker));
                        markerItems[i].setWidth(70);
                        markerItems[i].setHeight(70);
                        markerItems[i].setMap(map);

                        int finalI = i;

                        markerItems[i].setOnClickListener(overlay -> {
                            if(lastMarker == null || lastMarker.getTag() != markerItems[finalI].getTag()) {
                                LatLng coord = new LatLng(markerDataList.get(finalI).getLatitude(), markerDataList.get(finalI).getLongitude());
                                map.moveCamera(CameraUpdate.scrollAndZoomTo(coord, 16)
                                        .animate(CameraAnimation.Easing, 1500));

                                if (OverlayImage.fromResource(R.drawable.normal_marker).equals(markerItems[finalI].getIcon())) {
                                    if (lastMarker != null) {
                                        lastMarker.setIcon(OverlayImage.fromResource(R.drawable.normal_marker));
                                    }
                                    markerItems[finalI].setIcon(OverlayImage.fromResource(R.drawable.selected_marker));
                                    markerItems[finalI].setWidth(90);
                                    markerItems[finalI].setHeight(90);
                                    lastMarker = markerItems[finalI];
                                    modelName = markerDataList.get(finalI).getModelNum();
                                } else {
                                    markerItems[finalI].setIcon(OverlayImage.fromResource(R.drawable.normal_marker));
                                }

                                tvModelNum.setText(markerDataList.get(finalI).getModelNum());
                                tvBatteryValue.setText(String.valueOf(markerDataList.get(finalI).getBattery()) + "%");
                                tvTimeValue.setText(markerDataList.get(finalI).getTime());

                                //애니메이션 실행
                                pageValue = PAGE_UP;
                                translateUpAim.setAnimationListener(animationListener);
                                clModelInfo.setVisibility(View.VISIBLE);
                                clModelInfo.startAnimation(translateUpAim);
                            }
                            return true;
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseWithMarkerData> call, Throwable t) {
            }
        });
    }

    protected void makeCircle() {
        CircleOverlay circleOverlay = new CircleOverlay();
        circleOverlay.setCenter(new LatLng(37.4963111, 126.9574596));
        circleOverlay.setRadius(2000);
        circleOverlay.setColor(Color.parseColor("#196ED3EF"));
        circleOverlay.setOutlineColor(Color.parseColor("#FF4EBFDE"));
        circleOverlay.setOutlineWidth(3);
        circleOverlay.setMap(map);
    }

    private void mapLoad() {
        map.setLocationSource(locationSource);

        map.addOnLocationChangeListener(location -> {
            if(initMapLoad) {
                map.moveCamera(CameraUpdate.scrollAndZoomTo(new LatLng(location.getLatitude(), location.getLongitude()), 14)
                        .animate(CameraAnimation.Linear, 3000));
                map.setLocationTrackingMode(LocationTrackingMode.Follow);
                initMapLoad = false;
            }
        });

        map.addOnOptionChangeListener(() -> {
            locationSource.setCompassEnabled(true);
        });
        map.setLocationTrackingMode(LocationTrackingMode.Follow);

        map.setOnMapClickListener((point, coord) -> {
            //애니메이션
            if(isInfoPageOpen) {
                //애니메이션 준비
                translateDownAim = AnimationUtils.loadAnimation(this, R.anim.translate_down);
                translateDownAim.setAnimationListener(animationListener);
                pageValue = PAGE_DOWN;
                clModelInfo.startAnimation(translateDownAim);

                lastMarker.setIcon(OverlayImage.fromResource(R.drawable.normal_marker));
                lastMarker.setWidth(70);
                lastMarker.setHeight(70);
                lastMarker = null;
            }
        });
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
            clToolbar.setVisibility(View.GONE);

        });

        viewLayer.setOnClickListener((l -> {
            translateLeftAim = AnimationUtils.loadAnimation(this, R.anim.translate_left);
            translateLeftAim.setAnimationListener(animationListener);

            pageValue = PAGE_LEFT;
            clHamberger.startAnimation(translateLeftAim);
        }));
    }

    @Override
    public void onClick(View v) {
        if(v == tvPlanBackground){
            Intent intent = new Intent(this,NoticeActivity.class);
            Log.d("asdf1","1");
            intent.putExtra("tag", PLAN_BACKGROUND);
            Log.d("asdf2","2");
            viewLayer.performClick();
            Log.d("asdf3","4");
            startActivity(intent);
            Log.d("asdf4","4");
        } else if(v == tvLegal){
            Intent intent = new Intent(this,NoticeActivity.class);
            intent.putExtra("tag", LEGAL);
            viewLayer.performClick();
            startActivity(intent);
        } else if(v == tvGuide){
            Intent intent = new Intent(this,NoticeActivity.class);
            intent.putExtra("tag", GUIDE);
            viewLayer.performClick();
            startActivity(intent);
        }
    }

    private class SlidingPageAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {
            switch (pageValue) {
                case PAGE_DOWN : {
                    isInfoPageOpen = false;
                    btnInfoLocation.setVisibility(View.GONE);
                    btnInfoZoomIn.setVisibility(View.GONE);
                    btnInfoZoomOut.setVisibility(View.GONE);
                    break;
                }
                case PAGE_UP : {
                    isInfoPageOpen = true;
                    break;
                }
                case PAGE_LEFT : {
                    clHamberger.setVisibility(View.GONE);
                    viewLayer.setVisibility(View.GONE);
                    clToolbar.setVisibility(View.VISIBLE);
                    isHambergerOpen = false;
                    break;
                }
                case PAGE_RIGHT : {
                    clToolbar.setVisibility(View.GONE);
                    viewLayer.setVisibility(View.VISIBLE);
                    isHambergerOpen = true;
                }
            }
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            switch (pageValue) {
                case PAGE_DOWN : {
                    clModelInfo.setVisibility(View.GONE);
                    break;
                }
                case PAGE_UP : {
                    clModelInfo.setVisibility(View.VISIBLE);
                    btnInfoLocation.setVisibility(View.VISIBLE);
                    btnInfoZoomIn.setVisibility(View.VISIBLE);
                    btnInfoZoomOut.setVisibility(View.VISIBLE);
                    btnInfoLocation.setMap(map);
                    break;
                }
                case PAGE_LEFT : {
                    clHamberger.setVisibility(View.GONE);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {

            String modelNum = result.getContents();
            //return 모델 명 string
            if (result.getContents() == null) {

            } else {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BullgoTAService.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                BullgoTAService bullgoTAService = retrofit.create(BullgoTAService.class);
                Log.e("result.getContent2", result.getContents());

                double latitude = locationSource.getLastLocation().getLatitude();   //위도
                double longitude = locationSource.getLastLocation().getLongitude(); //경도

                bullgoTAService.returnModel(modelNum, new RequestReturnModel(latitude,longitude)).enqueue(new Callback<ResponseReturnModel>() {

                    @Override
                    public void onResponse(Call<ResponseReturnModel> call, Response<ResponseReturnModel> response) {
                        Log.e("getSuccess", String.valueOf(response.body().getSuccess()));

                        String message = response.body().getMessage();
                        Object object = response.body().getData();


                        if (message.equals("킥보드 반납 성공")) {
                            ReturnModelDialog returnModelDialog = new ReturnModelDialog(getApplicationContext());
                            returnModelDialog.setReturnModelDialog(0,modelNum, (int)object);

                        } else if(message.equals("이미 반납된 킥보드입니다.")){
                            ReturnModelDialog returnModelDialog = new ReturnModelDialog(getApplicationContext());
                            returnModelDialog.setReturnModelDialog(0,modelNum,0);
                        }
                        else if(message.equals("킥보드 반납 실패")){
                            ReturnModelDialog returnModelDialog = new ReturnModelDialog(getApplicationContext());
                            returnModelDialog.setReturnModelDialog(0,modelNum,0);
                        }
                        else{
                            Log.e("retrofit2 message :", message +"이건 서버담당자가 잘못한거임 ! 반성하세요. ");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseReturnModel> call, Throwable t) {
                        Log.e("fail", "fail");
                    }
                });

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data); }    }

}