package com.compiler.abohaoya.service;

import com.compiler.abohaoya.pojo.CurrentWeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by User on 8/4/2016.
 */
public interface CurrentWeatherServiceApi {
    @GET//("weather?q=Dhaka&appid=b29e08009a6e462c31893962c837c9ad")
    Call<CurrentWeatherResponse> getAllWeather(@Url String userUrl);
}
