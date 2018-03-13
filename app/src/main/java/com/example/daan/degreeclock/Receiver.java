package com.example.daan.degreeclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Daan on 11-3-2018.
 */

public class Receiver  extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent = new Intent(context, FloatingViewService.class);
        context.startService(startServiceIntent);
    }
}