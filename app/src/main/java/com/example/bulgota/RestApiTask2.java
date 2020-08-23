package com.example.bulgota;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class RestApiTask2 extends AsyncTask<Integer, Void, Void> {

    String url;
    String token;
    String timer;
    JSONObject jsonObject;

    public RestApiTask2(String url, String token) {
        this.url = url;
        this.token = token;
        this.timer = "05:00:00";    //TODO 임시설정
        this.jsonObject = null;
    }

    public String getUrl() {
        return url;
    }

    public String getToken() {
        return token;
    }

    public String getTimer() {
        return timer;
    }

    public JSONObject getJsonObject() {
      return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    @Override
    protected Void doInBackground(Integer... integers) {

        jsonObject = new JSONObject();

        //json 파일 생성
        makeJson(token, timer, jsonObject);

        try {
            // Open the connection
            URL url = new URL(this.url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //서버 쓰기모드 저장
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");

            conn.setRequestProperty("content-type","application/json");

            StringBuffer buffer = new StringBuffer();
            buffer = buffer.append(jsonObject.toString());

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(buffer.toString());
            writer.flush();     //서버전송
            writer.close();     //객체 닫기

            Log.e("서버 전송 여부 : ","성공");

            //응답 바디 받기
            BufferedReader reader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            StringBuffer buffer1 = new StringBuffer();
            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer1.append(line);

                Log.e("응답바디 : ",line);
            }
            conn.disconnect();

        }
        catch (Exception e) {

            Log.e("서버 전송 여부 : ","실패");
            e.printStackTrace();
        }
        return null;
    }

    //서버에 전달할 json 객체 생성
    void makeJson(String token, String timer, JSONObject jsonObject){
            try{
                jsonObject.put("deviceToken",token);
                jsonObject.put("timer",timer);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

}
