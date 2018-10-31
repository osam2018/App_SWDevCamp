package com.swdevcamp.jun.swdevcamp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this,LoadingActivity.class);
        startActivity(intent);

        RecyclerView recyclerView = findViewById(R.id.data_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        TextView userName = findViewById(R.id.user_name);
        TextView dataNum = findViewById(R.id.data_num);
        ImageButton btnCreateDataItem = findViewById(R.id.btn_create_data);

        ArrayList<DataCardItem> items = new ArrayList<>();
        PersonalDataCardAdapter adapter = new PersonalDataCardAdapter(this,items,R.layout.activity_main);
        items.add(new DataCardItem(R.drawable.capture1,"capture1"));
        items.add(new DataCardItem(R.drawable.capture2,"capture2"));
        items.add(new DataCardItem(R.drawable.capture1,"capture3"));
        items.add(new DataCardItem(R.drawable.capture2,"capture4"));

        userName.setText("HongGildong");
        dataNum.setText("총 자료수: " + String.valueOf(adapter.getItemCount()));

        btnCreateDataItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"btn_create_data cliked",Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(getBaseContext(),AlwaysTopService.class));
    }
}
