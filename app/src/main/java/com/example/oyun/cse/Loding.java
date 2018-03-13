package com.example.oyun.cse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by oyun on 2018-03-11.
 */

public class Loding extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            Thread.sleep(2000);
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}
