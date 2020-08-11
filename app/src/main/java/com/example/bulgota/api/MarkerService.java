package com.example.bulgota.api;

import com.example.bulgota.api.Marker_list;
import com.example.bulgota.api.ResponseWithMarkerData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MarkerService {
    String BASE_URL = "https://bullgota.ml";
    @GET("/list/marker")
    Call<ResponseWithMarkerData> getMarkerAll();
}

