package com.example.bulgota;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DatabaseManager {
    static final String fileDB = "timer.db";    //DB이름

    static String sTimer;    //타이머 시 분 초 문자열

    SQLiteDatabase sqliteDB;
    String sqlCreateTable;


    public DatabaseManager() {

        sqliteDB =null;
        sqlCreateTable =null;

        try{
            sqliteDB = SQLiteDatabase.openOrCreateDatabase(fileDB,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //SQLiteDataBase 객체 참조 획득

        sqlCreateTable = "CREATE TABLE IF NOT EXISTS TIMER (LEFT_TIME TEXT)";
        //TIMER  테이블 생성 (테이블 이름)
        //테이블 string

        sqliteDB.execSQL(sqlCreateTable);
        //테이블 생성

        addTimer();
        //데이터 베이스에 시간 추가 & 존재 시 수정

    }


    //TODO 데이터베이스에 ADD 기능  &     일반적으로 수정 시  UPDATE를 사용하는 것이 일반적이라고함
    //TODO 코드 줄이기 위해 REPLACE 기능도 있다하여 사용  -> 에러 시 UPDATE로 변경
    private void addTimer() {
        String sqlInsert = "INSERT OR REPLACE INTO TIMER (LEFT_TIME) VALUES ('"+ sTimer+"')";
        Log.e("뀨?",sqlInsert);
        sqliteDB.execSQL(sqlInsert);
    }



    //데이터 베이스 TIMER 모든 데이터 삭제
    public void deleteTimer(){
        String sqlDelete =  "DELETE FROM TIMER";
        sqliteDB.execSQL(sqlDelete);
    }


    public static String getsTimer() {
        return sTimer;
    }



}
