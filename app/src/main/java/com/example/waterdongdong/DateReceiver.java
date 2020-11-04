package com.example.waterdongdong;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        if (Intent.ACTION_DATE_CHANGED.equals(action)) {
            // 날짜가 변경된 경우 해야 될 작업을 한다.
            MainActivity.my_intake = 0;
            Select_PopupActivity.cnt = 0;
        }
    }
}

