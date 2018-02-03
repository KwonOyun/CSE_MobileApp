package com.example.oyun.cse;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class gNotice extends AppCompatActivity {

    private ListView listView;
    private InformationListAdapter adapter;
    private Information information2;
    private List<Information> informationList = new ArrayList<Information>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_g_notice);

        listView = (ListView) findViewById(R.id.noticeListview);
        new BackgroundTask().execute();
        adapter = new InformationListAdapter(getApplicationContext(), informationList);
        listView.setAdapter(adapter);

    }

    class BackgroundTask extends AsyncTask<String, String, String>{

        String target;
        @Override
        protected void onPreExecute() {
            target = "http://104.199.178.189/information2.php";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try{
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                int count = 0;
                String anumber, atitle, awriter, atime;
                while(count<jsonArray.length()){
                    JSONObject object = jsonArray.getJSONObject(count);
                    anumber = object.getString("number");
                    atitle = object.getString("title");
                    awriter = object.getString("writer");
                    atime = object.getString("time");
                    information2 = new Information(atitle, anumber, awriter, atime);
                    informationList.add(information2);
                    count++;
                }
                adapter.notifyDataSetChanged();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(String... strings) {
            try{
                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String temp; //한줄 한줄 저장하기 위해서 임의의 temp설정
                StringBuilder stringBuilder = new StringBuilder();

                while ((temp = bufferedReader.readLine()) != null){
                    stringBuilder.append(temp+"\n");
                }
                bufferedReader.close();
                inputStream.close();
                inputStreamReader.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim(); //다 들어간 문자열을 반환
            }
            catch (Exception e){
                e.printStackTrace();
                return  null;
            }
        }
    }
}
