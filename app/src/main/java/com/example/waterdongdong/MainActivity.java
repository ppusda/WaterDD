package com.example.waterdongdong;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.gelitenight.waveview.library.WaveView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class MainActivity extends AppCompatActivity {

    ImageView img_setting;
    TextView txt_name, txt_temp, txt_intake;
    Button btn_db, btn_mod;

    String chk_mod;
    float goal_intake = 0;
    int bf_intake = 0;
    static int now_intake = 0;
    static int my_intake = 0;

    String date, time, weekDay;
    int tot_cal;

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
                readMod();
                readUser();
                readIntake();
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

        mDatabase = FirebaseDatabase.getInstance().getReference();

        final WaveView waveView = (WaveView) findViewById(R.id.wave);

        Date currentTime = Calendar.getInstance().getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        date = dateFormat.format(currentTime);

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //데이터 수신
            TextView temp = findViewById(R.id.txt_temp);
            TextView intake = findViewById(R.id.txt_intake);
            @SuppressLint("SetTextI18n")
            public void onDataReceived(byte[] data, String message) {

                String[] array=message.split(",");
                now_intake =  Integer.parseInt(array[1]);

                readIntake();
                my_intake += now_intake;
                writeIntake(my_intake);
                tot_cal = now_intake * Select_PopupActivity.cal;
                writeCalorie(tot_cal);

                if(now_intake != 0){
                    Intent q_pop_in = new Intent(getApplicationContext(), Select_PopupActivity.class);
                    startActivityForResult(q_pop_in, 1);
                }

                if (my_intake == 0) {
                    Waterlevel = 0.0f;
                }else{
                    float chk_intake =  my_intake/goal_intake;
                    String bs = String.format("%.1f", chk_intake);
                    Waterlevel = Float.parseFloat(bs);
                }

                setWaterView(waveView, Waterlevel);
                intake.setText(my_intake + " ml/" + (goal_intake/1000) + " L");
                temp.setText(array[0].concat("C"));

                bf_intake = now_intake;
                now_intake = 0;
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

        img_setting = findViewById(R.id.Img_setting);
        txt_name = findViewById(R.id.txt_name);
        txt_temp = findViewById(R.id.txt_temp);
        txt_intake = findViewById(R.id.txt_intake);

        btn_db = findViewById(R.id.btn_db);
        btn_mod = findViewById(R.id.btn_mod);

        readMod();
        readUser();
        readIntake();

        mDatabase.child("record").child(date).child("d_date").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //Log.d("MainActivity", "onChildAdded : " + snapshot.getValue());

                Data data = snapshot.getValue(Data.class);
                txt_name.setText(data.getD_name());
            } // Log로 값을 확인해 보았을 때 snapshot.getValue는 값들을 모두 가져온다는 걸 알 수 있다.
            // 이를 전체 모두 표현하려면 ArrayList에 담아서 해결해야할 듯 하다.
            // ** cnt에 대해서 - 위에 Run버튼을 누르면 앱이 새로 install 되는데 그 때마다 값이 리셋 된다.
            // 012345가 쌓이다가도 다시 디버깅하려고 누르면 0이 되기에 참고하기 바람.

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // String Key = snapshot.getKey(); 키 받아오기

        img_setting.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Setting_Activity.class);
                startActivityForResult(intent, 1);
            }
        });

        setWaterView(waveView, Waterlevel);

        btn_db.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent it = new Intent(getApplicationContext(), Database_main.class);
                startActivityForResult(it, 1);
            }
        });
        btn_mod.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Toast.makeText(getApplicationContext(), "현재 모드 : " + chk_mod, Toast.LENGTH_SHORT).show();
                Intent it = new Intent(getApplicationContext(), SelectModActivity.class);
                startActivityForResult(it, 1);
            }
        });
    }

    public void setWaterView(WaveView waveView, float level){ // waterview를 설정하는 메소드
        waveView.setBorder(mBorderWidth, mBorderColor);
        waveView.setWaveColor(Color.parseColor("#516de8"), Color.parseColor("#548dd1")); // 물의 색과 투명도를 조절하는 옵션이다 ex #55548dd1
        // 처음 두자리는 투명도에 관한 옵션이다. 그 뒤에는 알고있던 #hex 코드와 같다. - 투명도 표현을 위해 위에 예시에 투명도를 포함해 작성함 - 가장 물같은 색으로 바꿔주길 바람

        mWaveHelper = new WaveHelper(waveView, Waterlevel);
    }

//    private void readData(){
//        mDatabase.child("record").child(date).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Data data = dataSnapshot.getValue(Data.class);
//                txt_name.setText(data.getD_name());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                System.out.println("The read failed: " + databaseError.getCode());
//            }
//        });
//    } 구버전 데이터 읽기

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

    private void readUser(){
        mDatabase.child("user_info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                goal_intake = user.getGoal_intake();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void writeIntake(int Intake){
        Intake intake = new Intake(Intake);

        mDatabase.child("record").child(date).child("d_intake").setValue(intake)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

    }

    private void readIntake(){
        mDatabase.child("record").child(date).child("d_intake").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Intake intake = dataSnapshot.getValue(Intake.class);
                my_intake = intake.getT_intake();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void writeCalorie(int Calorie){
        Calorie calorie = new Calorie(Calorie);

        mDatabase.child("record").child(date).child("d_calorie").setValue(calorie)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
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
