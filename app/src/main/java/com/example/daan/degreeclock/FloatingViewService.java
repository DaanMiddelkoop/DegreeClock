package com.example.daan.degreeclock;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.Calendar;

import static java.lang.Math.floor;

/**
 * Created by Daan on 11-3-2018.
 */

public class FloatingViewService extends Service {
    private View mView;

    private WindowManager.LayoutParams mParams;
    private WindowManager mWindowManager;

    @Override
    public void onCreate() {
        super.onCreate();

        mView = new MyLoadView(this);
        mParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,// TYPE_SYSTEM_ALERT is denied in apiLevel >=19
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT
        );

        if (Build.VERSION.SDK_INT >= 26) {
            mParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,// TYPE_SYSTEM_ALERT is denied in apiLevel >=19
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    PixelFormat.TRANSLUCENT
            );
        }
        //PixelFormat.TRANSLUCENT |
        mParams.setTitle("Window test");
        mParams.gravity = Gravity.TOP;
        mParams.height = 50;


        mWindowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mView, mParams);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((WindowManager)getSystemService(WINDOW_SERVICE)).removeView(mView);

        Intent startServiceIntent = new Intent(this, FloatingViewService.class);
        this.startService(startServiceIntent);
        mView = null;
    }

    public class MyLoadView extends View {

        private Paint mPaint;
        String am_pm;
        float h_angle;
        float m_angle;
        float m_angle_p;
        String result;

        private Handler mHandler;

        Runnable mStatusChecker = new Runnable() {
            @Override
            public void run() {
                Calendar c = Calendar.getInstance();
                long now = c.getTimeInMillis();
                c.set(Calendar.HOUR_OF_DAY, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
                long passed = now - c.getTimeInMillis();
                float secondsPassed = (float)passed / 1000f;

                float h = secondsPassed / 3600f;
                float m = ((float)secondsPassed - (float)floor(h) * 60f * 60f) / 60.0f;

                h_angle = (h % 12) / 12.0f * 360f;
                m_angle = (m / 60.0f) * 360f;

                am_pm = "AM";
                if (h > 12) {
                    am_pm = "PM";
                }
                String form = "{:3.0f}";

                result = String.format("%3.0f° %3.0f°", h_angle, m_angle);

                if (m_angle != m_angle_p) {
                    invalidate();
                }
                m_angle_p = m_angle;
                Log.d("Draw", "Executing draw function");
                mHandler.postDelayed(mStatusChecker, 5000);
            }
        };


        public MyLoadView(Context context) {
            super(context);
            mPaint = new Paint();
            mPaint.setTextSize(35);
            mPaint.setARGB(255, 255, 255, 255);

            mHandler = new Handler();
            mStatusChecker.run();

        }

        @Override
        protected void onDraw(Canvas canvas) {


            super.onDraw(canvas);
            canvas.drawText(result + " " + am_pm, 450, 40, mPaint);
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {

            // ATTENTION: GET THE X,Y OF EVENT FROM THE PARAMETER
            // THEN CHECK IF THAT IS INSIDE YOUR DESIRED AREA

            Toast.makeText(getContext(),"onTouchEvent", Toast.LENGTH_LONG).show();
            return false;
        }
    }
}
