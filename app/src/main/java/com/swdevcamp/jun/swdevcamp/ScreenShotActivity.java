package com.swdevcamp.jun.swdevcamp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ScreenShotActivity extends Activity {
    private static final int REQUEST_CODE = 100;
    private static String STORE_DIRECTORY;
    private static int IMAGES_PRODUCED;
    private static final String SCREENCAP_NAME = "screencap";
    private static final int VIRTUAL_DISPLAY_FLAGS = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
    private static MediaProjection mp;


    private MediaProjectionManager mpm;
    private int dispWidth, dispHeight, layoutWidth, layoutHeight, screenDensity, statusbarHeight;
    private ImageReader imageReader;
    private Handler handler;
    private DisplayMetrics displayMetrics;
    private VirtualDisplay vrDisp;

    private Intent data = null;

    private class ImageAvailableListener implements ImageReader.OnImageAvailableListener {
        @Override
        public void onImageAvailable(ImageReader imageReader) {
            Image image = null;
            FileOutputStream fos = null;
            Bitmap bitmap = null;

            try {
                image = imageReader.acquireLatestImage();
                if (image != null) {
                    Image.Plane[] planes = image.getPlanes();
                    ByteBuffer buffer = planes[0].getBuffer();
                    int pixelStride = planes[0].getPixelStride();
                    int rowStride = planes[0].getRowStride();
                    int rowPadding = rowStride - pixelStride * dispWidth;
                    Bitmap captureBitmap = Bitmap.createBitmap(dispWidth + rowPadding / pixelStride, dispHeight, Bitmap.Config.ARGB_8888);

                    data = getIntent();
                    int cropX = data.getIntExtra("territoryX",0);
                    int cropY = data.getIntExtra("territoryY", 0);
                    layoutWidth = data.getIntExtra("territoryWidth",dispWidth);
                    layoutHeight = data.getIntExtra("territoryHeight",dispHeight);
                    Log.d("createVirtualDisplay", "LayoutSize: width: " + String.valueOf(dispWidth) + " height: " + String.valueOf(dispHeight));
                    bitmap = Bitmap.createBitmap(captureBitmap,cropX,cropY,layoutWidth*captureBitmap.getWidth()/dispWidth,layoutHeight*captureBitmap.getHeight()/dispHeight);
                    /*
                    int bitmapHeight = dispHeight -statusbarHeight;
                    int bitmapWidth = dispWidth * bitmapHeight / dispHeight;
                    bitmap = Bitmap.createBitmap(bitmapWidth , bitmapHeight, Bitmap.Config.ARGB_8888);
                    */

                    bitmap.copyPixelsFromBuffer(buffer);

                    //저장
                    fos = new FileOutputStream(STORE_DIRECTORY + "/myscreen_" + IMAGES_PRODUCED + ".jpg");
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                    SharedPreferences sp = getSharedPreferences("bitmap",Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("bitmap_address",STORE_DIRECTORY + "/myscreen_" + IMAGES_PRODUCED + ".jpg");
                    editor.apply();

                    IMAGES_PRODUCED++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }

                if (bitmap != null) {
                    bitmap.recycle();
                }

                if (image != null) {
                    image.close();
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private class MediaProjectionStopCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            handler.post(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void run() {
                    if (vrDisp != null) vrDisp.release();
                    if (imageReader != null) imageReader.setOnImageAvailableListener(null, null);
                    mp.unregisterCallback(MediaProjectionStopCallback.this);
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_shot_view);

        int resoureceId = getResources().getIdentifier("status_bar_height","dimen","android");
        statusbarHeight = resoureceId > 0 ? getResources().getDimensionPixelSize(resoureceId):0;

        mpm = (MediaProjectionManager)getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startProjection();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                mp = mpm.getMediaProjection(resultCode,data);
                if(mp != null) {
                    STORE_DIRECTORY = Environment.getExternalStorageDirectory() + "/data/capturetest/";
                    File storeDirectory = new File(STORE_DIRECTORY);
                    if (!storeDirectory.exists()) {
                        boolean success = storeDirectory.mkdirs();
                        if (!success) {
                            return;
                        }
                    }
                }
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                displayMetrics = new DisplayMetrics();
                screenDensity = metrics.densityDpi;
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

                createVirtualDisplay();

                mp.registerCallback(new MediaProjectionStopCallback(), handler);
            }
        }
        stopProjection();
        finish();
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startProjection() {
        startActivityForResult(mpm.createScreenCaptureIntent(), REQUEST_CODE);
    }

    private void stopProjection() {
        if(handler == null) handler = new Handler();
        handler.post(new Runnable() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                if (mp != null) {
                    mp.stop();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void createVirtualDisplay() {
        //가로,세로 고려 사이즈는 다시 설정하고
        dispWidth = displayMetrics.widthPixels;
        dispHeight = displayMetrics.heightPixels;
        Log.d("createVirtualDisplay", "is it null?: " + String.valueOf(data == null));

        imageReader = ImageReader.newInstance(dispWidth, dispHeight, PixelFormat.RGBA_8888, 2);
        vrDisp = mp.createVirtualDisplay(SCREENCAP_NAME, dispWidth, dispHeight, screenDensity, VIRTUAL_DISPLAY_FLAGS, imageReader.getSurface(), null, handler);
        imageReader.setOnImageAvailableListener(new ImageAvailableListener(), handler);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //private Bitmap Tt
}

