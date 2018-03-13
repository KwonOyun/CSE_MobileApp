package com.example.oyun.cse;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

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

        Intent intent = new Intent(getApplicationContext(), PopUpActivity.class);
        intent.putExtra("data", "1.공지사항 업데이트시 푸시알림을 통해 알려드립니다.\n\n2.학사공지와 사업단정보, 취업정보는 로그인이 필요합니다.(컴퓨터공학과 학생만 가능)\n\n3.보고싶은 공지사항을 클릭하면 과홈페이지를 통해 보여드립니다.");
        startActivityForResult(intent, 1);

        FirebaseMessaging.getInstance().subscribeToTopic("noticeMsg");

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
