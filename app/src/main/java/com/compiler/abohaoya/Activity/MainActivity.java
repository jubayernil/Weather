package com.compiler.abohaoya.Activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.icu.text.NumberFormat;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.compiler.abohaoya.R;
import com.compiler.abohaoya.pojo.CurrentWeatherResponse;
import com.compiler.abohaoya.service.Constant;
import com.compiler.abohaoya.service.CurrentWeatherServiceApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    //private TextView temperatureTextView;
    //private EditText cityNameEditText;

    private TextView cityNameTextView;
    private TextView tempTextView;
    private TextView degreeTextView;
    private TextView celciusFahrenheitTextView;
    private ImageView skyImageView;

    private CurrentWeatherServiceApi currentWeatherServiceApi;

    private String cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //temperatureTextView = (TextView) findViewById(R.id.temperatureTextView);
        //cityNameEditText = (EditText) findViewById(R.id.cityNameEditText);
        cityNameTextView = (TextView) findViewById(R.id.cityNameTextView);
        tempTextView = (TextView) findViewById(R.id.tempTextView);
        degreeTextView = (TextView) findViewById(R.id.degreeTextView);
        celciusFahrenheitTextView = (TextView) findViewById(R.id.celciusFahrenheitTextView);
        skyImageView = (ImageView) findViewById(R.id.skyImageView);

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
                /*double celcius = currentWeatherResponse.getMain().getTemp() - 273.16;
                NumberFormat numberFormat = NumberFormat.getInstance();
                numberFormat.setMaximumFractionDigits(2);
                String sCelcius = numberFormat.format(celcius);*/
                //temperatureTextView.setText(sCelcius);

                cityNameTextView.setText(currentWeatherResponse.getName());
                int temp = (int) Math.ceil(currentWeatherResponse.getMain().getTemp()- 273.16);
                tempTextView.setText(String.valueOf(temp));
                degreeTextView.setText(""+(char) 0x00B0);
                celciusFahrenheitTextView.setText("C");
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

    public void onToggleClicked(View view) {
        // Is the toggle on?
        boolean on = ((ToggleButton) view).isChecked();

        if (on) {
            String sTemp = tempTextView.getText().toString();
            int iTemp = ((Integer.parseInt(sTemp)*9)/5)+32;
            tempTextView.setText(String.valueOf(iTemp));
            celciusFahrenheitTextView.setText("F");
        } else {
            getCurrentWeatherData();
        }
    }

    /*public void findTempurature(View view) {
        if (cityNameEditText.getText().length() == 0){
            cityNameEditText.setError("City Name Required");
        } else {
            cityName = cityNameEditText.getText().toString();
        }
        getCurrentWeatherData();
    }*/
}
