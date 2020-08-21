package com.example.bulgota;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class BluetoothSearchDialog extends Dialog {

    private final Context context;

    BluetoothDevice device;
    BluetoothDevice paired;
    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothSocket;
    Handler bluetoothHandler;
    ConnectedBluetoothThread threadConnectedBluetooth;

    final static int BT_MESSAGE_READ = 2;
    final static int BT_CONNECTING_STATUS = 3;
    final static UUID BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    //UI
    private TextView tvBluetooth;
    private TextView tvBluetoothError;
    private Button btnSearch;
    private ListView listBluetooth;
    public static LottieAnimationView lottieAnimationView; //(로딩모양)측정중 로띠띠

    //Adapter
    SimpleAdapter adapterBluetooth;


    //list - Bluetooth 목록 저장
    List<Map<String,String>> dataPaired;
    List<Map<String, String>> dataBluetooth;
    List<BluetoothDevice> bluetoothDevices;
    Set<BluetoothDevice> pairedDevices;
    int selectDevice;

    private BluetoothSearchDialogListener bluetoothSearchDialogListener;

    public BluetoothSearchDialog(Context context) {
        super(context);
        this.context = context;
    }

    //인터페이스 설정
    interface BluetoothSearchDialogListener {
        void onBluetoothClicked();
        void onCancleClicked();
    }

    //호출할 리스너 초기화
    public void setDialogListener(BluetoothSearchDialog.BluetoothSearchDialogListener bluetoothSearchDialogListener){
        this.bluetoothSearchDialogListener = bluetoothSearchDialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_bluetooth_search);

        //UI
        tvBluetooth = findViewById(R.id.tv_bluetooth);
        tvBluetoothError = findViewById(R.id.tv_bluetooth_error);
        btnSearch = findViewById(R.id.btn_search);
        listBluetooth = findViewById(R.id.list_bluetooth);
        lottieAnimationView = findViewById(R.id.lottie_breathe_testing);

        //Adapter
        dataBluetooth = new ArrayList<>();
        adapterBluetooth = new SimpleAdapter(context, dataBluetooth, R.layout.item_bluetooth_list, new String[]{"name"}, new int[]{R.id.tv_bluetooth});
        listBluetooth.setAdapter(adapterBluetooth);

        //검색된 블루투스 디바이스 데이터
        bluetoothDevices = new ArrayList<>();
        //선택한 디바이스 없음
        selectDevice = -1;

        //블루투스 지원 유무 확인
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        btnSearch.setOnClickListener(l -> {
            OnBluetoothSearch();
        });

        bluetoothHandler = new Handler(){
            public void handleMessage(android.os.Message msg){
                if(msg.what == 1){
                    Log.e("connect success", "연결 성공");

                }
            }
        };

        //블루투스 지원하지 않으면 null 리턴
        if(bluetoothAdapter == null) {
            tvBluetoothError.setText("블루투스를 지원하지 않는 단말기입니다.");
            tvBluetoothError.setVisibility(View.VISIBLE);
        }

        //블루투스 브로드캐스트 리시버 등록
        IntentFilter searchFilter = new IntentFilter();
        searchFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED); //BluetoothAdapter.ACTION_DISCOVERY_STARTED : 블루투스 검색 시작
        searchFilter.addAction(BluetoothDevice.ACTION_FOUND); //BluetoothDevice.ACTION_FOUND : 블루투스 디바이스 찾음
        searchFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED); //BluetoothAdapter.ACTION_DISCOVERY_FINISHED : 블루투스 검색 종료
        searchFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        context.registerReceiver(bluetoothSearchReceiver, searchFilter);


        //블루투스가 꺼져있으면 사용자에게 활성화 요청
        if(!bluetoothAdapter.isEnabled()) {
            tvBluetoothError.setText("블루투스를 활성화한 후 다시 검색해주세요.");
            tvBluetoothError.setVisibility(View.VISIBLE);
        } else {
        }


        //검색된 디바이스목록 클릭시 페어링 요청
        listBluetooth.setOnItemClickListener((parent, view, position, id) -> {
            device = bluetoothDevices.get(position);

            try {
                Method method = device.getClass().getMethod("createBond", (Class[]) null);
                method.invoke(device, (Object[]) null);

                selectDevice = position;

                //Log.e("device", String.valueOf(device));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }


    //블루투스 검색결과
    BroadcastReceiver bluetoothSearchReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch(action){
                //블루투스 디바이스 검색 시작
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    setUpAnimation(lottieAnimationView);

                    dataBluetooth.clear();
                    bluetoothDevices.clear();
                    Log.e("search start", "검색 시작");
                    //로띠시작---아래에서
                    break;
                //블루투스 디바이스 찾음
                case BluetoothDevice.ACTION_FOUND:
                    //검색한 블루투스 디바이스의 객체를 구한다
                    device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    //데이터 저장
                    Map map = new HashMap();
                    map.put("name", device.getName()); //device.getName() : 블루투스 디바이스의 이름
                    //Log.e("search name", device.getName());

//                    **** 블루투스 이름 필터링 - 나중에 필요하면 추가****
//                    if(device.getName().substring(0,3).equals("GSM")){
//                        map.put("name", device.getName()); //device.getName() : 블루투스 디바이스의 이름
//                        Log.e("search name", device.getName());
//                        //map.put("address", device.getAddress()); //device.getAddress() : 블루투스 디바이스의 MAC 주소
//                    }

                    dataBluetooth.add(map);
                    //리스트 목록갱신
                    adapterBluetooth.notifyDataSetChanged();

                    //블루투스 디바이스 저장
                    bluetoothDevices.add(device);
                    break;
                //블루투스 디바이스 검색 종료
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    //로띠종료 검색버튼 활성화
                    lottieAnimationView.setVisibility(View.INVISIBLE);
                    btnSearch.setEnabled(true);
                    break;
                //블루투스 디바이스 페어링 상태 변화
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    paired = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if(paired.getBondState()==BluetoothDevice.BOND_BONDED){
                        //데이터 저장
                        pairedDevices = bluetoothAdapter.getBondedDevices();
                        Log.e("paired", String.valueOf(pairedDevices));

//                        Map map2 = new HashMap();
//                        map2.put("name", paired.getName()); //device.getName() : 블루투스 디바이스의 이름
//                        map2.put("address", paired.getAddress()); //device.getAddress() : 블루투스 디바이스의 MAC 주소
//                        dataPaired.add(map2);
                        connectSelectedDevice(paired.getName());

                        //검색된 목록
                        if(selectDevice != -1){
                            bluetoothDevices.remove(selectDevice);

                            dataBluetooth.remove(selectDevice);
                            adapterBluetooth.notifyDataSetChanged();
                            selectDevice = -1;
                        }
                    }
                    break;
            }
        }
    };

    void connectSelectedDevice(String selectedDeviceName) {
        for(BluetoothDevice tempDevice : pairedDevices) {
            if (selectedDeviceName.equals(tempDevice.getName())) {
                Log.e("click3", tempDevice.getName());
                paired = tempDevice;
                break;
            }
        }
        try {
            bluetoothSocket = paired.createRfcommSocketToServiceRecord(BT_UUID);
            bluetoothSocket.connect();
            threadConnectedBluetooth = new ConnectedBluetoothThread(bluetoothSocket);
            threadConnectedBluetooth.start();
            bluetoothHandler.obtainMessage(BT_CONNECTING_STATUS, 1, -1).sendToTarget();

            Log.e("click4", String.valueOf(bluetoothSocket));


        } catch (IOException e) {
            Toast.makeText(context, "블루투스 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
        }
    }

    private class ConnectedBluetoothThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedBluetoothThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Toast.makeText(context, "소켓 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            Log.e("bluetooth", "연결");

        }
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = mmInStream.available();
                    if (bytes != 0) {
                        SystemClock.sleep(100);
                        bytes = mmInStream.available();
                        bytes = mmInStream.read(buffer, 0, bytes);
                        bluetoothHandler.obtainMessage(BT_MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                        Log.e("bluetooth2", "연결");

                    }
                } catch (IOException e) {
                    break;
                }
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Toast.makeText(context, "소켓 해제 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
        }
    }


    //블루투스 검색 버튼 클릭*****************************
    public void OnBluetoothSearch(){
        //검색버튼 비활성화
        btnSearch.setEnabled(false);
        //mBluetoothAdapter.isDiscovering() : 블루투스 검색중인지 여부 확인
        //mBluetoothAdapter.cancelDiscovery() : 블루투스 검색 취소
        if(bluetoothAdapter.isDiscovering()){
            bluetoothAdapter.cancelDiscovery();
        }

        if (bluetoothAdapter.isEnabled()) {
            pairedDevices = bluetoothAdapter.getBondedDevices();
        }
            //mBluetoothAdapter.startDiscovery() : 블루투스 검색 시작
        bluetoothAdapter.startDiscovery();
    }

    private void setUpAnimation(LottieAnimationView animview) { //로띠 애니메이션 설정
        //재생할 애니메이션
        animview.setAnimation("lottie_breathe_testing.json");
        //반복횟수 지정 : 무한
        animview.setRepeatCount(LottieDrawable.INFINITE); //아니면 횟수 지정
        //시작
        animview.playAnimation();
    }


    @Override
    public void onBackPressed() {
        dismiss();
    }
}


