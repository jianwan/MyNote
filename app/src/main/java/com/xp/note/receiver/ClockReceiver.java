package com.xp.note.receiver;

import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Vibrator;
import android.view.WindowManager;

import com.xp.note.activity.MainActivity;

public class ClockReceiver extends BroadcastReceiver {


    private Vibrator vibrator;   //手机震动


    @Override
    public void onReceive(Context context, Intent intent) {

        vibrator= (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);

        vibrator.vibrate(new long[]{500, 1000, 500, 2000}, 0);


//        Toast.makeText(context, "收到定时广播", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(context, MainActivity.class);
        String  content = intent.getStringExtra("content");

        int LAYOUT_FLAG;

        //TODO API>23 时候要申请权限
//        if (Build.VERSION.SDK_INT < 23){



        AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("小主~你定的任务时间到了~");
            builder.setMessage(content);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    vibrator.cancel();
                }
            });
            AlertDialog alertDialog = builder.create();
//            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            alertDialog.getWindow().setType((WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY));
        }else {
            alertDialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
        }
            alertDialog.show();
//        }


        context.startService(i);
    }

}
