package com.compiler.abohaoya;

import android.annotation.TargetApi;
import android.icu.text.NumberFormat;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.compiler.abohaoya.pojo.CurrentWeatherResponse;
import com.compiler.abohaoya.service.Constant;
import com.compiler.abohaoya.service.CurrentWeatherServiceApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private TextView temperatureTextView;
    private EditText cityNameEditText;
    private CurrentWeatherServiceApi currentWeatherServiceApi;

    private String cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temperatureTextView = (TextView) findViewById(R.id.temperatureTextView);
        cityNameEditText = (EditText) findViewById(R.id.cityNameEditText);

        cityName = "Dhaka";

        networkLibraryInitialize();
        getCurrentWeatherData();
    }

    private void getCurrentWeatherData() {
        String userUrl = "weather?q="+cityName+"&appid=b29e08009a6e462c31893962c837c9ad";
        Call<CurrentWeatherResponse> currentWeatherResponseCall = currentWeatherServiceApi.getAllWeather(userUrl);
        currentWeatherResponseCall.enqueue(new Callback<CurrentWeatherResponse>() {
            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<CurrentWeatherResponse> call, Response<CurrentWeatherResponse> response) {
                CurrentWeatherResponse currentWeatherResponse = response.body();
                double celcius = currentWeatherResponse.getMain().getTemp() - 273.16;
                NumberFormat numberFormat = NumberFormat.getInstance();
                numberFormat.setMaximumFractionDigits(2);
                String sCelcius = numberFormat.format(celcius);
                temperatureTextView.setText(sCelcius);
            }

            @Override
            public void onFailure(Call<CurrentWeatherResponse> call, Throwable t) {

            }
        });
    }

    private void networkLibraryInitialize() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        currentWeatherServiceApi = retrofit.create(CurrentWeatherServiceApi.class);
    }

    public void findTempurature(View view) {
        if (cityNameEditText.getText().length() == 0){
            cityNameEditText.setError("City Name Required");
        } else {
            cityName = cityNameEditText.getText().toString();
        }
        getCurrentWeatherData();
    }
}
