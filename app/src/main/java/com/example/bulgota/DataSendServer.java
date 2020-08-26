package com.example.bulgota;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class DataSendServer extends AsyncTask<Integer, Void, Void> {

    int timer;
    String url;
    String token;
    JSONObject jsonObject;

    //내부 DB를 위한 string 변수
    String DBtimer;

    public DataSendServer(String url, String token, int timer) {
        this.url = url;
        this.token = token;
        this.timer = timer;
        this.jsonObject = null;
        this.DBtimer = null;
    }

    public String getUrl() {
        return url;
    }

    public String getToken() {
        return token;
    }

    public int getTimer() {
        return timer;
    }

    public JSONObject getJsonObject() {
      return jsonObject;
    }

    public String getDBtimer() {
        return DBtimer;
    }

    public void setDBtimer(String DBtimer) {
        this.DBtimer = DBtimer;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;

        try {
            jsonObject.put("deviceToken", token);
            jsonObject.put("timer", timer);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(Integer... integers) {

        jsonObject = new JSONObject();

        //  서버에 전달할 json 객체 생성
        setJsonObject(jsonObject);

        try {
            // Open the connection
            URL url = new URL(this.url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //서버 쓰기모드 저장
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");

            //respose데이터 json으로 설정
            conn.setRequestProperty("Content-type","application/json");

            StringBuffer buffer = new StringBuffer();
            buffer = buffer.append(jsonObject);

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(buffer.toString());
            writer.flush();
            writer.close();

            Log.e("json 값 : ",jsonObject.toString());
            Log.e("서버전송 완료","성공");

            //응답바디 받기
            BufferedReader reader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            StringBuffer buffer1 = new StringBuffer();
            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer1.append(line);

                Log.e("응답바디 : ", line);
            }
            conn.disconnect();

        }
        catch (Exception e) {
            // Error calling the rest api
            Log.e("REST_API", "GET method failed: " + e.getMessage());

            Log.e("서버 전송 여부 : ","실패");
            e.printStackTrace();
        }

        //String timer 변수 저장
        makeStringTimer(timer);

        return null;
    }

    private void makeStringTimer(int timer) {
        int sec;
        int min;
        int  hour;
        String Timer;

        sec = timer%60;
        min = timer/60;
        hour = min/60;

        Timer = hour+"시"+min+"분"+sec+"초";

        setDBtimer(Timer);

        DatabaseManager.sTimer = Timer;
        //static 변수로 해독 시간 저장
        DatabaseManager databaseManager = new DatabaseManager();
        //객체 생성 -> 생성자로 데이터베이스 생성 및 수정 자동 완성 코드 구현
    }

}
