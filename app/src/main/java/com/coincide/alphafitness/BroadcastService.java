package com.coincide.alphafitness;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;


import java.util.Timer;
import java.util.TimerTask;

import static com.google.android.gms.internal.zzid.runOnUiThread;

public class BroadcastService extends Service {

    //    public static final String COUNTDOWN_BR = "com.coincide.countdown.countdown_br";
    public static final String COUNTDOWN_BR = "package.action.string";
    private final static String TAG = "BroadcastService";
//    private final int UPDATE_TIME_PERIOD = 5000;
//    ISocketOperator socketOperator = new SocketOperator(this);
    Intent bi = new Intent(COUNTDOWN_BR);
    long test;
//    CountDownTimer cdt = null;
    private Timer timer;
    private int currentTime = 0;
    @Override
    public void onCreate() {
        super.onCreate();

        Log.e(TAG, "Starting timer...");

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        currentTime += 1;
//                        lapTime += 1;

                        bi.putExtra("currentTime", currentTime);
                        sendBroadcast(bi);

                           /*manager = (NotificationManager)
                                    getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

                            // update notification text
                            builder.setContentText(TimeFormatUtil.toDisplayString(currentTime));
                            manager.notify(mId, builder.build());*/

                        // update ui
//                        tv_duration.setText(TimeFormatUtil.toDisplayString(currentTime));
                    }
                });
            }
        }, 0, 100);



      /*  cdt = new CountDownTimer(300000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                test = millisUntilFinished / 1000;
                Log.e(TAG, "Countdown seconds remaining: " + millisUntilFinished / 1000);
                bi.putExtra("countdown", millisUntilFinished);
                sendBroadcast(bi);
                timer = new Timer();
                Thread thread = new Thread() {
                    @Override
                    public void run() {

                        Log.e(TAG, "Countdown seconds remaining: " + test);

                    }
                };
                thread.start();

            }

            @Override
            public void onFinish() {
                UpdateTask();
                Log.e(TAG, "Timer finished");

            }
        };

        cdt.start();*/
    }

    @Override
    public void onDestroy() {
        timer.cancel();
//        cdt.cancel();
//            UpdateTask();
        Log.i(TAG, "Timer cancelled");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }


    public void UpdateTask() {
        Thread thread = new Thread() {
            @Override
            public void run() {
//                String abc = socketOperator.sendHttpRequest("temp_mail");
//                Log.e("Updated task started", "show me");
//                SharedPreferences.Editor editor = CustomSharedPreference.getEditor(getApplicationContext());
//                editor.remove(getString(R.string.sp_OrderSummaryListObject));
////                editor.remove("password");
//
//                editor.commit();
                stopService(new Intent(getApplicationContext(), BroadcastService.class));
            }
        };
        thread.start();
    }
}