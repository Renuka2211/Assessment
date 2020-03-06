package com.boda.renuka.assessment;

import com.google.gson.JsonElement;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface EcommerceApi {

    //@Headers("Content-Type: application/json")

    @GET("json")

    //Call<JSONObject> getData(@Header("Content-Type") String contentType);
    Call<JsonElement> getData();

}
