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

        makeJson(token, timer, jsonObject);

        try {
            // Open the connection
            URL url = new URL(this.url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //서버 쓰기모드 저장
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");

            StringBuffer buffer = new StringBuffer();
            buffer = buffer.append(jsonObject);

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(buffer.toString());
            writer.flush();
            writer.close();

            Log.e("서버전송 완료","성공");

            //TODO 서버에서 값을 받아오지 않더라도 작성??    추후 삭제 시 구동되는지 확인
            BufferedReader reader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            StringBuffer buffer1 = new StringBuffer();
            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer1.append(line);
            }
            conn.disconnect();

        }
        catch (Exception e) {
            // Error calling the rest api
            Log.e("REST_API", "GET method failed: " + e.getMessage());
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
