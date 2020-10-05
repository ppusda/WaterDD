package com.example.waterdongdong;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class Setting_Activity extends AppCompatActivity {

    LinearLayout About_b, About_app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_);

        About_b = findViewById(R.id.About_beverage);
        About_app = findViewById(R.id.About_app);

        About_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Select_PopupActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        About_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), App_infoActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }
}
