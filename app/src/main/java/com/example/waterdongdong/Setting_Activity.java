package com.example.waterdongdong;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class Setting_Activity extends AppCompatActivity {

    LinearLayout About_b, About_app, About_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_);

        About_b = findViewById(R.id.About_beverage);
        About_app = findViewById(R.id.About_app);
        About_user = findViewById(R.id.Save_user);

        About_b.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent it_b = new Intent(getApplicationContext(), Select_PopupActivity.class);
                startActivityForResult(it_b, 1);
            }
        });

        About_app.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent it_info = new Intent(getApplicationContext(), App_Info.class);
                startActivityForResult(it_info, 1);
            }
        });

        About_user.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent it_user_info = new Intent(getApplicationContext(), User_Info_Calculate.class);
                startActivityForResult(it_user_info, 1);
            }
        });
    }
}
