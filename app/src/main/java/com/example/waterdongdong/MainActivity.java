package com.example.waterdongdong;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gelitenight.waveview.library.WaveView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class MainActivity extends AppCompatActivity {

    ImageView img_setting;
    TextView txt_name, txt_temp, txt_intake;
    Button btn_db, btn_mod;

    String chk_mod;

    private WaveHelper mWaveHelper;

    private int mBorderColor = Color.parseColor("#FFFFFF"); // 도형 테두리의 색깔
    private int mBorderWidth = 10; // 도형 테두리의 넓이
    private float Waterlevel = 0.5f; // 물의 높이

    private DatabaseReference mDatabase;

    private BluetoothSPP bt;

    @Override

    /////////블루투스
    public void onDestroy() {
        super.onDestroy();
        bt.stopService(); //블루투스 중지
    }

    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) { //
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER); //DEVICE_ANDROID는 안드로이드 기기 끼리
                setup();
            }
        }
    }

    public void setup() {
        Button btnSend = findViewById(R.id.btnSend); //데이터 전송
        btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                bt.send("1", true);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            if(resultCode==RESULT_OK){
                readData();
                readMod();
            }
        }
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) { /////블루투스
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
                setup();
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /////블루투스
        bt = new BluetoothSPP(this); //Initializing

        if (!bt.isBluetoothAvailable()) { //블루투스 사용 불가
            Toast.makeText(getApplicationContext()
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //데이터 수신
            TextView temp = findViewById(R.id.txt_temp);
            TextView intake = findViewById(R.id.txt_intake);
            public void onDataReceived(byte[] data, String message) {

                String[] array=message.split(",");

                temp.setText(array[0].concat("C"));
                intake.setText(array[1].concat("ml"));

            }
        });

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() { //연결됐을 때
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext()
                        , "Connected to " + name + "\n" + address
                        , Toast.LENGTH_SHORT).show();
            }

            public void onDeviceDisconnected() { //연결해제
                Toast.makeText(getApplicationContext()
                        , "Connection lost", Toast.LENGTH_SHORT).show();
            }

            public void onDeviceConnectionFailed() { //연결실패
                Toast.makeText(getApplicationContext()
                        , "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnConnect = findViewById(R.id.btnConnect); //연결시도
        btnConnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    bt.disconnect();
                } else {
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }
            }
        });

        ////

        img_setting = findViewById(R.id.Img_setting);
        txt_name = findViewById(R.id.txt_name);
        txt_temp = findViewById(R.id.txt_temp);
        txt_intake = findViewById(R.id.txt_intake);

        btn_db = findViewById(R.id.btn_db);
        btn_mod = findViewById(R.id.btn_mod);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        readData();
        readMod();

        img_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Setting_Activity.class);
                startActivityForResult(intent, 1);
            }
        });

        final WaveView waveView = (WaveView) findViewById(R.id.wave);
        waveView.setBorder(mBorderWidth, mBorderColor);
        waveView.setWaveColor(Color.parseColor("#516de8"), Color.parseColor("#548dd1")); // 물의 색과 투명도를 조절하는 옵션이다 ex #55548dd1
        // 처음 두자리는 투명도에 관한 옵션이다. 그 뒤에는 알고있던 #hex 코드와 같다. - 투명도 표현을 위해 위에 예시에 투명도를 포함해 작성함 - 가장 물같은 색으로 바꿔주길 바람

        mWaveHelper = new WaveHelper(waveView, Waterlevel);

        btn_db.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getApplicationContext(), Database_main.class);
                startActivityForResult(it, 1);
            }
        });
        btn_mod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "현재 모드 : " + chk_mod, Toast.LENGTH_SHORT).show();
                Intent it = new Intent(getApplicationContext(), SelectModActivity.class);
                startActivityForResult(it, 1);
            }
        });

    }

    private void readData(){
        mDatabase.child("record").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Data data = dataSnapshot.getValue(Data.class);
                txt_name.setText(data.getD_name());
                txt_temp.setText(Integer.toString(data.getTemp()) + " C");
                txt_intake.setText(Integer.toString(data.getIntake()) + " ml");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void readMod(){
        mDatabase.child("mod").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Mod mod = dataSnapshot.getValue(Mod.class);
                chk_mod = mod.getMod();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWaveHelper.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWaveHelper.start();
    }

}

