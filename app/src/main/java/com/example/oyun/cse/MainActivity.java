package com.example.oyun.cse;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button noticebtn;
    Button gnoticebtn;
    Button saccordbtn;
    Button jobbtn;
    Button eventbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noticebtn = (Button) findViewById(R.id.notice);
        gnoticebtn = (Button) findViewById(R.id.gnotice);
        saccordbtn = (Button) findViewById(R.id.saccord);
        jobbtn = (Button) findViewById(R.id.job);
        eventbtn = (Button) findViewById(R.id.event);

        noticebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Notice.class);
                startActivity(intent);
            }
        });

        gnoticebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, gNotice.class);
                startActivity(intent);
            }
        });

        saccordbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Saccord.class);
                startActivity(intent);
            }
        });

        jobbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Job.class);
                startActivity(intent);
            }
        });

        eventbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Event.class);
                startActivity(intent);
            }
        });
    }
}
