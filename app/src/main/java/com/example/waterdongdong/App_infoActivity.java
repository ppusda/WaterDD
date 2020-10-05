package com.example.waterdongdong;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class App_infoActivity extends AppCompatActivity {

    Button btn_info_confirm;
    TextView txt_now_ver, txt_re_ver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btn_info_confirm = findViewById(R.id.btn_info_confirm);
        txt_now_ver = findViewById(R.id.txt_now_version);
        txt_re_ver = findViewById(R.id.txt_recent_version);

        ImageView image = (ImageView)findViewById(R.id.img_icon);
        image.setImageResource(R.drawable.ic_launcher_foreground);

        String chk_ver = "현재 버전 : " + BuildConfig.VERSION_NAME;

        txt_now_ver.setText(chk_ver);
        txt_re_ver.setText("최신 버전 : 1.0");

        btn_info_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}