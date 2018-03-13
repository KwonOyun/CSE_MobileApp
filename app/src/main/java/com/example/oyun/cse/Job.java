package com.example.oyun.cse;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

public class Job extends AppCompatActivity {

    private ListView listView;
    private InformationListAdapter adapter;
    private Information information4;
    private List<Information> informationList = new ArrayList<Information>();
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job);

        listView = (ListView) findViewById(R.id.noticeListview);
        new BackgroundTask().execute();
        adapter = new InformationListAdapter(getApplicationContext(), informationList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Information item = (Information) adapter.getItem(position);
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getUrl()));
                startActivity(intent);
            }
        });

    }

    class BackgroundTask extends AsyncTask<String, String, String>{

        String target;
        @Override
        protected void onPreExecute() {
            target = "http://104.199.178.189/information4.php";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try{
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                int count = 0;
                String anumber, atitle, awriter, atime, aurl;
                while(count<jsonArray.length()){
                    JSONObject object = jsonArray.getJSONObject(count);
                    anumber = object.getString("number");
                    atitle = object.getString("title");
                    awriter = object.getString("writer");
                    atime = object.getString("time");
                    aurl = object.getString("url");
                    information4 = new Information(atitle, anumber, awriter, atime, aurl);
                    informationList.add(information4);
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
