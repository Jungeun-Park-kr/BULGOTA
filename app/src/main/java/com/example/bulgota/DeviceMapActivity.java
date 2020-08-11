package com.example.bulgota;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.ColorUtils;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.bulgota.api.MarkerService;
import com.example.bulgota.api.Marker_list;
import com.example.bulgota.api.ResponseWithMarkerData;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.CircleOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DeviceMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mapView;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private FusedLocationSource locationSource;
    private NaverMap map;

    private ConstraintLayout clModelInfo;
    private Marker lastMarker;
    private Marker[] markerItems;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_device_map);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);

        clModelInfo = findViewById(R.id.cl_model_info);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated()) {
                map.setLocationTrackingMode(LocationTrackingMode.None);
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        UiSettings uiSettings = naverMap.getUiSettings();

        uiSettings.setCompassEnabled(false);
        uiSettings.setScaleBarEnabled(false);
        uiSettings.setZoomControlEnabled(true);//AVD용(나중에 false로 바꾸기)

        getMarker(naverMap);
        makeCircle(naverMap);

        naverMap.setOnMapClickListener((point, coord) -> {
            clModelInfo.setVisibility(View.GONE);
            if(lastMarker != null) {
                lastMarker.setIcon(OverlayImage.fromResource(R.drawable.marker));
            }
        });

        naverMap.setLocationSource(locationSource);

        naverMap.addOnOptionChangeListener(() -> {
            LocationTrackingMode mode = naverMap.getLocationTrackingMode();
            locationSource.setCompassEnabled(mode == LocationTrackingMode.Follow);
        });

        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
    }

    protected void getMarker(NaverMap naverMap) {
        TextView tvModelNum = findViewById(R.id.tv_model_num);
        TextView tvBatteryValue = findViewById(R.id.tv_battery_value);
        TextView tvTimeValue = findViewById(R.id.tv_time_value);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MarkerService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MarkerService markerService = retrofit.create(MarkerService.class);
        markerService.getMarkerAll().enqueue(new Callback<ResponseWithMarkerData>() {
            @Override
            public void onResponse(Call<ResponseWithMarkerData> call, Response<ResponseWithMarkerData> response) {
                if (response.body().getSuccess()) {
                    ArrayList<Marker_list> markerDataList = response.body().getData();
                    markerItems = new Marker[markerDataList.size()];

                    if (markerDataList.get(0) == null) {
                        return;
                    }

                    for (int i = 0; i < markerDataList.size(); i++) {
                        Log.e("data" + i, markerDataList.get(i).getModel() + "");
                        markerItems[i] = new Marker();
                        markerItems[i].setTag(i + 1);
                        markerItems[i].setPosition(new LatLng(markerDataList.get(i).getLatitude(), markerDataList.get(i).getLongitude()));
                        markerItems[i].setIcon(OverlayImage.fromResource(R.drawable.marker));
                        markerItems[i].setWidth(60);
                        markerItems[i].setHeight(60);
                        markerItems[i].setMap(naverMap);

                        int finalI = i;

                        markerItems[i].setOnClickListener(overlay -> {
                            if (OverlayImage.fromResource(R.drawable.marker).equals(markerItems[finalI].getIcon())) {
                                if (lastMarker != null) {
                                    lastMarker.setIcon(OverlayImage.fromResource(R.drawable.marker));
                                }
                                markerItems[finalI].setIcon(OverlayImage.fromResource(R.drawable.selected_marker));
                                lastMarker = markerItems[finalI];
                            } else {
                                markerItems[finalI].setIcon(OverlayImage.fromResource(R.drawable.marker));
                            }

                            tvModelNum.setText(markerDataList.get(finalI).getModel());
                            tvBatteryValue.setText(String.valueOf(markerDataList.get(finalI).getBattery()) + "%");
                            tvTimeValue.setText(markerDataList.get(finalI).getTime());

                            clModelInfo.setVisibility(View.VISIBLE);
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

    protected void makeCircle(NaverMap naverMap) {
        int color = Color.parseColor("#FFAAEDF6");

        CircleOverlay circleOverlay = new CircleOverlay();
        circleOverlay.setCenter(new LatLng(37.4963111,126.9574596));
        circleOverlay.setRadius(2000);
        circleOverlay.setColor(ColorUtils.setAlphaComponent(color, 31));
        circleOverlay.setOutlineColor(Color.parseColor("#FF00BCD4"));
        circleOverlay.setOutlineWidth(5);
        circleOverlay.setMap(naverMap);
    }
}