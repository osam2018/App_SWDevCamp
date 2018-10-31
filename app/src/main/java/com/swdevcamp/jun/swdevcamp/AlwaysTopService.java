package com.swdevcamp.jun.swdevcamp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class AlwaysTopService extends Service {
    private View mView;
    private WindowManager mManager;
    float x,y,initX,initY;
    int screenWidth, screenHeight, statusbarHeight, screenDensity;
    FrameLayout layout;
    RelativeLayout.LayoutParams layoutParams;

    TextView infoTextView;
    Button btnCapture,btnCancel;

    public AlwaysTopService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate() {
        super.onCreate();

        //화면 너비 높이 구함
        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        screenDensity = dm.densityDpi;
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        int resoureceId = getResources().getIdentifier("status_bar_height","dimen","android");
        statusbarHeight = resoureceId > 0 ? getResources().getDimensionPixelSize(resoureceId):0;
        Log.d("status_bar_height","status_bar_height = " + String.valueOf(statusbarHeight));

        LayoutInflater mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = mInflater.inflate(R.layout.capture_view,null);

        layout = mView.findViewById(R.id.selected_territory);
        infoTextView = mView.findViewById(R.id.info_text);
        btnCapture = mView.findViewById(R.id.btn_capture);
        btnCancel = mView.findViewById(R.id.btn_cancel);

        layoutParams = (RelativeLayout.LayoutParams) layout.getLayoutParams();

        WindowManager.LayoutParams mParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.FILL_PARENT,
                WindowManager.LayoutParams.FILL_PARENT,

                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        mManager = (WindowManager)getSystemService(WINDOW_SERVICE);
        mManager.addView(mView, mParams);

        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initX = event.getRawX();
                        initY = event.getRawY();
                        infoTextView.setVisibility(View.INVISIBLE);
                        Log.d("TouchEvent/","action_down ( " + String.valueOf(event.getX()) +", "+ String.valueOf(event.getY()) + " )");
                        break;
                    case MotionEvent.ACTION_MOVE:
                        x = event.getRawX();
                        y = event.getRawY();
                        if(x >= initX && y >= initY) {
                            layoutParams.setMargins((int) initX, (int) initY - statusbarHeight, screenWidth - (int) x, screenHeight - (int) y);
                        } else if(x < initX && y >= initY) {
                            layoutParams.setMargins((int) x, (int) initY - statusbarHeight,screenWidth - (int) initX, screenHeight - (int) y);
                        } else if(x >= initX && y < initY) {
                            layoutParams.setMargins((int) initX, (int) y - statusbarHeight,screenWidth - (int) x, screenHeight - (int) initY);
                        } else if(x < initX && y < initY) {
                            layoutParams.setMargins((int) x, (int) y - statusbarHeight,screenWidth - (int) initX, screenHeight - (int) initY);
                        }
                        layout.setLayoutParams(layoutParams);
                        Log.d("TouchEvent/","action_move ( " + String.valueOf(event.getX()) +", "+ String.valueOf(event.getY()) + " )");
                        break;
                    case MotionEvent.ACTION_UP:
                        layoutParams.setMargins((int)initX,(int)initY-statusbarHeight,screenWidth-(int)x,screenHeight-(int)y);
                        layout.setLayoutParams(layoutParams);
                        btnCapture.setX(x);
                        btnCapture.setY(y);
                        btnCancel.setX(x);
                        btnCancel.setY(y+btnCapture.getHeight());
                        btnCapture.setVisibility(View.VISIBLE);
                        btnCancel.setVisibility(View.VISIBLE);
                        btnCapture.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                btnCapture.setVisibility(View.INVISIBLE);
                                btnCancel.setVisibility(View.INVISIBLE);
                                layout.setVisibility(View.INVISIBLE);
                                openScreenShotActivity();
                            }
                        });
                        btnCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                btnCapture.setVisibility(View.INVISIBLE);
                                btnCancel.setVisibility(View.INVISIBLE);
                                layout.setVisibility(View.INVISIBLE);
                                stopSelf();
                            }
                        });
                        if(Math.abs(initX-x) <= 0 || Math.abs(initY-y) <= 0) infoTextView.setVisibility(View.VISIBLE);
                        layout.setVisibility((View.VISIBLE));
                        Log.d("TouchEvent/","action_up ( " + String.valueOf(event.getX()) +", "+ String.valueOf(event.getY()) + " )" +
                                " + LayoutSize: width: " + String.valueOf(layout.getWidth()) + " height: " + String.valueOf(layout.getHeight()));

                        break;
                }

                return true;
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(mManager != null) {
            if(mView != null) {
                mManager.removeView(mView);
            }
        }
    }

    private void openScreenShotActivity() {
        Intent captureIntent = new Intent(getBaseContext(), ScreenShotActivity.class);
        captureIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        captureIntent.putExtra("territoryX",(int)initX);
        captureIntent.putExtra("terrotoryY",(int)initY);
        captureIntent.putExtra("territoryWidth",layout.getWidth());
        captureIntent.putExtra("territoryHeight",layout.getHeight());
        startActivity(captureIntent);
    }
}

