package com.example.bulgota;

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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BluetoothSearchDialog extends Dialog {

    private final Context context;

    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothSocket;

    final static int BLUETOOTH_REQUEST_CODE = 100;

    //UI
    private TextView tvBluetooth;
    private TextView tvBluetoothError;
    private Button btnSearch;
    private ListView listBluetooth;

    //Adapter
    SimpleAdapter adapterBluetooth;


    //list - Bluetooth 목록 저장
    List<Map<String,String>> dataPaired;
    List<Map<String, String>> dataBluetooth;
    List<BluetoothDevice> bluetoothDevices;
    int selectDevice;

    private BluetoothSearchDialogListener bluetoothSearchDialogListener;
    final static UUID BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

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
            BluetoothDevice device = bluetoothDevices.get(position);

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
                //블루투스 디바이스 검색 종료
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    dataBluetooth.clear();
                    bluetoothDevices.clear();
                    Log.e("search start", "검색 시작");
                    //로띠시작---아래에서
                    break;
                //블루투스 디바이스 찾음
                case BluetoothDevice.ACTION_FOUND:
                    //검색한 블루투스 디바이스의 객체를 구한다
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
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
                    btnSearch.setEnabled(true);
                    break;
                //블루투스 디바이스 페어링 상태 변화
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    BluetoothDevice paired = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if(paired.getBondState()==BluetoothDevice.BOND_BONDED){
                        //데이터 저장
                        Log.e("paired", String.valueOf(paired));
//                        Map map2 = new HashMap();
//                        map2.put("name", paired.getName()); //device.getName() : 블루투스 디바이스의 이름
//                        map2.put("address", paired.getAddress()); //device.getAddress() : 블루투스 디바이스의 MAC 주소
//                        dataPaired.add(map2);
                        connectToSelectedDevice(paired);

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

    private void connectToSelectedDevice (final Object pairedDevice) {
        @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                if( msg.what == 1) {
                    Toast.makeText(context, "연결", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "연결 실패", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };


    //블루투스 검색 버튼 클릭*****************************
    public void OnBluetoothSearch(){
        //검색버튼 비활성화
        btnSearch.setEnabled(false);
        //mBluetoothAdapter.isDiscovering() : 블루투스 검색중인지 여부 확인
        //mBluetoothAdapter.cancelDiscovery() : 블루투스 검색 취소
        if(bluetoothAdapter.isDiscovering()){
            bluetoothAdapter.cancelDiscovery();
        }
        //mBluetoothAdapter.startDiscovery() : 블루투스 검색 시작
        bluetoothAdapter.startDiscovery();
    }


    @Override
    public void onBackPressed() {
        bluetoothSearchDialogListener.onCancleClicked();
    }
}


