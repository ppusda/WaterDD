package com.example.waterdongdong;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class User_Info_Calculate extends AppCompatActivity {

    EditText Text_cm, Text_kg; // cm, kg
    Button Btn_calc; //계산 버튼
    TextView Text_result; //결과 값

    int result = 0;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀 바 없애기

        setContentView(R.layout.activity_calculate);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_calculate);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mDatabase = FirebaseDatabase.getInstance().getReference();

        init();
    }


    void init(){
        Text_cm = (EditText)findViewById(R.id.text_cm);
        Text_kg = (EditText)findViewById(R.id.text_kg);

        Btn_calc = (Button)findViewById(R.id.btn_calc);

        Text_result = (TextView)findViewById(R.id.text_result);


        // 목표 음수량 계산 버튼 이벤트
        Btn_calc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String sCm = Text_cm.getText().toString();
                String sKg = Text_kg.getText().toString();

                //이전의 문자열의 값 지우기
                Text_result.setText("");

                // 두값의 하나라도 기입하지 않을 시
                if(sCm.isEmpty()||sKg.isEmpty()){
                    Toast.makeText(getApplicationContext(), "모든 칸을 기입해 주세요.",Toast.LENGTH_SHORT).show();
                    return; //값 없이 반환
                }


                int t1 = Integer.parseInt(sCm);
                int t2 = Integer.parseInt(sKg);

                result = (t1 + t2)*10;

                Text_result.setText(result+"");

            }
        });
    }

   public void confirmClick(View v){
       if(result == 0){
           Toast.makeText(getApplicationContext(), "계산을 끝낸 후 확인을 눌러주세요.",Toast.LENGTH_SHORT).show();
           return;
       }

       writeRecord(result); // '계산 후 결과 값' 저장 기능 추가
       finish();
    }


    private void writeRecord(int g_intake){
        User user = new User(g_intake);

        mDatabase.child("user_info").setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "저장을 완료했습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "저장을 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });

    }

   @Override
    public boolean onTouchEvent(MotionEvent event){
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

}