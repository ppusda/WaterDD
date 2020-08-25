package com.example.waterdongdong;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gelitenight.waveview.library.WaveView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    ImageView img_setting;
    TextView txt_name, txt_temp, txt_intake;

    private WaveHelper mWaveHelper;

    private int mBorderColor = Color.parseColor("#FFFFFF");
    private int mBorderWidth = 10;
    private float Waterlevel = 0.5f; // 물의 높이

    private DatabaseReference mDatabase;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            if(resultCode==RESULT_OK){
                readData();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img_setting = findViewById(R.id.Img_setting);
        txt_name = findViewById(R.id.txt_name);
        txt_temp = findViewById(R.id.txt_temp);
        txt_intake = findViewById(R.id.txt_intake);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        readData();

        img_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Select_PopupActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        final WaveView waveView = (WaveView) findViewById(R.id.wave);
        waveView.setBorder(mBorderWidth, mBorderColor);
        waveView.setWaveColor(Color.parseColor("#516de8"), Color.parseColor("#548dd1"));

        mWaveHelper = new WaveHelper(waveView, Waterlevel);

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

