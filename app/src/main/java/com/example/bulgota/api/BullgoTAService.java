package com.example.bulgota.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface BullgoTAService {
    String BASE_URL = "https://bullgota.ml";

    @GET("/model/info/{model}")
    Call<ResponseSelectModel> checkModel(@Path("model") String model);

    @GET("/list/marker")
    Call<ResponseWithMarkerData> getMarkerAll();
}

