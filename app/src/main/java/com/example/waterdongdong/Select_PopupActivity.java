package com.example.waterdongdong;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.PopupWindowCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.waterdongdong.Data;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import app.akexorcist.bluetotohspp.library.BluetoothState;

public class Select_PopupActivity extends AppCompatActivity {

    RadioButton Rdb1, Rdb2;
    EditText ed_name;
    ArrayAdapter<CharSequence> sc, sn;
    Button btn_confirm;

    String chk_category;
    String chk_name;
    int chk_ed = 1;

    String date, time, weekDay;
    int cal = 0;
    int chk_cal = 0;
    static int cnt = 0;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_select__popup);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Rdb1 = findViewById(R.id.Rdb_sp_name);
        Rdb2 = findViewById(R.id.Rdb_ed_name);

        final Spinner sp_category = (Spinner)findViewById(R.id.sp_category);
        final Spinner sp_name = (Spinner)findViewById(R.id.sp_name);
        ed_name = findViewById(R.id.ed_name);
        btn_confirm = findViewById(R.id.btn_b_confirm);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Date currentTime = Calendar.getInstance().getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        SimpleDateFormat weekdayFormat = new SimpleDateFormat("EE", Locale.getDefault());

        date = dateFormat.format(currentTime);
        time = timeFormat.format(currentTime);
        weekDay = weekdayFormat.format(currentTime);

        Rdb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sp_name.setEnabled(true);
                    ed_name.setEnabled(false);

                    chk_ed = 1;
                }
            }
        });

        sc = ArrayAdapter.createFromResource(this, R.array.category, R.layout.support_simple_spinner_dropdown_item);
        sc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_category.setAdapter(sc);

        sp_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                ((TextView)adapterView.getChildAt(0)).setTextColor(Color.BLACK);
                if(sc.getItem(position).equals("물")){
                    cal = 0;
                    sn = ArrayAdapter.createFromResource(Select_PopupActivity.this, R.array.water, R.layout.support_simple_spinner_dropdown_item);
                    sn.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    chk_category = sc.getItem(position).toString();
                    sp_name.setAdapter(sn);

                    sp_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            ((TextView)parent.getChildAt(0)).setTextColor(Color.DKGRAY);
                            chk_name = sn.getItem(position).toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                } else if(sc.getItem(position).equals("유제품")){
                    cal = 45;
                    sn = ArrayAdapter.createFromResource(Select_PopupActivity.this, R.array.dairy, R.layout.support_simple_spinner_dropdown_item);
                    sn.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    chk_category = sc.getItem(position).toString();
                    sp_name.setAdapter(sn);

                    sp_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            ((TextView)parent.getChildAt(0)).setTextColor(Color.DKGRAY);
                            chk_name = sn.getItem(position).toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                } else if(sc.getItem(position).equals("탄산음료")){
                    cal = 40;
                    sn = ArrayAdapter.createFromResource(Select_PopupActivity.this, R.array.carbonated, R.layout.support_simple_spinner_dropdown_item);
                    sn.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    chk_category = sc.getItem(position).toString();
                    sp_name.setAdapter(sn);

                    sp_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            ((TextView)parent.getChildAt(0)).setTextColor(Color.DKGRAY);
                            chk_name = sn.getItem(position).toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                } else if(sc.getItem(position).equals("스포츠음료")){
                    cal = 11;
                    sn = ArrayAdapter.createFromResource(Select_PopupActivity.this, R.array.sports, R.layout.support_simple_spinner_dropdown_item);
                    sn.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    chk_category = sc.getItem(position).toString();
                    sp_name.setAdapter(sn);

                    sp_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            ((TextView)parent.getChildAt(0)).setTextColor(Color.DKGRAY);
                            chk_name = sn.getItem(position).toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                } else if(sc.getItem(position).equals("주스")){
                    cal = 55;
                    sn = ArrayAdapter.createFromResource(Select_PopupActivity.this, R.array.juice, R.layout.support_simple_spinner_dropdown_item);
                    sn.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    chk_category = sc.getItem(position).toString();
                    sp_name.setAdapter(sn);

                    sp_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            ((TextView)parent.getChildAt(0)).setTextColor(Color.DKGRAY);
                            chk_name = sn.getItem(position).toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                } else if(sc.getItem(position).equals("차")){
                    cal = 1;
                    sn = ArrayAdapter.createFromResource(Select_PopupActivity.this, R.array.tea, R.layout.support_simple_spinner_dropdown_item);
                    sn.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    chk_category = sc.getItem(position).toString();
                    sp_name.setAdapter(sn);

                    sp_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            ((TextView)parent.getChildAt(0)).setTextColor(Color.DKGRAY);
                            chk_name = sn.getItem(position).toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Rdb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sp_name.setEnabled(false);
                    ed_name.setEnabled(true);

                    chk_ed = -1;
                }
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chk_ed == -1) {
                    chk_name = ed_name.getText().toString();
                }

                chk_cal = chk_cal + (MainActivity.now_intake*cal);
                //Log.d("Select_PopupActivity", "Success : " + chk_cal);

                writeRecord(chk_category, chk_name, time, weekDay, cnt);
                writeCalorie(chk_cal);
                cnt ++;

                finish();
            }
        });
    }

    private void writeRecord(String category, String name, String time, String weekDay, int cnt){
        Data data = new Data(category, name, time, weekDay, cnt);

        mDatabase.child("record").child(date).child("d_date").push().setValue(data)
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
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

}

