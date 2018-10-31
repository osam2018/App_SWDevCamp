package com.swdevcamp.jun.swdevcamp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;

public class DetailActivity extends AppCompatActivity {
    ImageView resultView;
    ImageButton btnCut,btnToolOn,btnEditText,btnToolOff;

    private Bitmap allCapturedView;
    private Bitmap selectedCapturedView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        int image = intent.getIntExtra("image",0);
        Log.d("Debug: ","image value is" + String.valueOf(image));

        resultView = findViewById(R.id.data_image);
        resultView.setImageResource(image);

        btnCut = findViewById(R.id.btn_cut);
        btnToolOn = findViewById(R.id.btn_tool_on);
        btnEditText = findViewById(R.id.btn_edit_text);
        btnToolOff = findViewById(R.id.btn_tool_off);

        btnToolOff.setVisibility(View.INVISIBLE);
        btnCut.setVisibility(View.INVISIBLE);
        btnEditText.setVisibility(View.INVISIBLE);
        btnToolOn.setVisibility(View.VISIBLE);

        btnToolOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnToolOn.getVisibility() == View.VISIBLE) btnToolOn.setVisibility(View.INVISIBLE);
                if(btnCut.getVisibility() == View.INVISIBLE) btnCut.setVisibility(View.VISIBLE);
                if(btnToolOff.getVisibility() == View.INVISIBLE) btnToolOff.setVisibility(View.VISIBLE);
                if(btnEditText.getVisibility() == View.INVISIBLE) btnEditText.setVisibility(View.VISIBLE);
            }
        });
        btnCut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnCut.getVisibility() == View.VISIBLE) btnCut.setVisibility(View.INVISIBLE);
                if(btnToolOff.getVisibility() == View.VISIBLE) btnToolOff.setVisibility(View.INVISIBLE);
                if(btnEditText.getVisibility() == View.VISIBLE) btnEditText.setVisibility(View.INVISIBLE);

                Intent serviceIntent = new Intent(getBaseContext(),AlwaysTopService.class);
                startService(serviceIntent);

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp = getSharedPreferences("bitmap",Activity.MODE_PRIVATE);
        String fAddress = sp.getString("bitmap_address","");
        resultView.setImageBitmap(readFromFile(fAddress));
        stopService(new Intent(getBaseContext(),AlwaysTopService.class));
        if(btnToolOn.getVisibility() == View.INVISIBLE) btnToolOn.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean stopService(Intent name) {
        if(btnToolOn.getVisibility() == View.INVISIBLE) btnToolOn.setVisibility(View.VISIBLE);
        return super.stopService(name);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private Bitmap readFromFile(String dir) {

        Bitmap bitmapFile = BitmapFactory.decodeFile(dir);
        return bitmapFile;
    }
}
