package com.compiler.abohaoya.Activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.icu.text.NumberFormat;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.compiler.abohaoya.Adapter.CityAdapter;
import com.compiler.abohaoya.R;
import com.compiler.abohaoya.model.City;
import com.compiler.abohaoya.pojo.CurrentWeatherResponse;
import com.compiler.abohaoya.service.Constant;
import com.compiler.abohaoya.service.CurrentWeatherServiceApi;

import java.util.ArrayList;
import java.util.List;

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
    private TextView todayMaxMinTempTextView;
    private TextView weatherSummaryTextView;
    private TextView weatherDetailTextView;
    private ImageView skyImageView;

    ToggleButton temtogglebutton;

    Spinner cityNameSpinner;
    CityAdapter cityAdapter;
    City city;
    ArrayList<City> cities;

    private CurrentWeatherServiceApi currentWeatherServiceApi;

    private String cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        cityNameSpinner = (Spinner) findViewById(R.id.cityNameSpinner);

        city = new City();
        cities = city.getAllCity();
        cityAdapter = new CityAdapter(this, cities);
        cityNameSpinner.setAdapter(cityAdapter);

        //temperatureTextView = (TextView) findViewById(R.id.temperatureTextView);
        //cityNameEditText = (EditText) findViewById(R.id.cityNameEditText);
        cityNameTextView = (TextView) findViewById(R.id.cityNameTextView);
        tempTextView = (TextView) findViewById(R.id.tempTextView);
        degreeTextView = (TextView) findViewById(R.id.degreeTextView);
        celciusFahrenheitTextView = (TextView) findViewById(R.id.celciusFahrenheitTextView);
        todayMaxMinTempTextView = (TextView) findViewById(R.id.todayMaxMinTempTextView);
        weatherSummaryTextView = (TextView) findViewById(R.id.weatherSummaryTextView);
        weatherDetailTextView = (TextView) findViewById(R.id.weatherDetailTextView);
        skyImageView = (ImageView) findViewById(R.id.skyImageView);
        temtogglebutton = (ToggleButton) findViewById(R.id.temtogglebutton);

        temtogglebutton.setTextOff((char) 0x00B0+"C");
        temtogglebutton.setTextOn((char) 0x00B0+"F");
        temtogglebutton.setText((char) 0x00B0+"C");

        cityNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(CityActivity.this, ""+adapterView.getItemAtPosition(i).toString(), Toast.LENGTH_SHORT).show();
                cityName = cities.get(i).getCityName();
                cityNameTextView.setText(cityName);
                getCurrentWeatherData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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
                final CurrentWeatherResponse currentWeatherResponse = response.body();

                tempTextView.setText(convertToCelsius(currentWeatherResponse.getMain().getTemp()));
                degreeTextView.setText(""+(char) 0x00B0);
                celciusFahrenheitTextView.setText("C");
                todayMaxMinTempTextView.setText("Today "+convertToCelsius(currentWeatherResponse.getMain().getTempMax())+(char) 0x00B0+" ~ "+
                        convertToCelsius(currentWeatherResponse.getMain().getTempMin())+(char) 0x00B0);
                weatherSummaryTextView.setText(currentWeatherResponse.getWeather().get(0).getMain());
                weatherDetailTextView.setText(currentWeatherResponse.getWeather().get(0).getDescription());
                temtogglebutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean on = ((ToggleButton) view).isChecked();
                        if (on) {
                            tempTextView.setText(convertToFahrenheit(currentWeatherResponse.getMain().getTemp()));
                            celciusFahrenheitTextView.setText("F");
                            todayMaxMinTempTextView.setText("Today "+convertToFahrenheit(currentWeatherResponse.getMain().getTempMax())+(char) 0x00B0+" ~ "+
                                    convertToFahrenheit(currentWeatherResponse.getMain().getTempMin())+(char) 0x00B0);
                        } else {
                            tempTextView.setText(convertToCelsius(currentWeatherResponse.getMain().getTemp()));
                            celciusFahrenheitTextView.setText("C");
                            todayMaxMinTempTextView.setText("Today "+convertToCelsius(currentWeatherResponse.getMain().getTempMax())+(char) 0x00B0+" ~ "+
                                    convertToCelsius(currentWeatherResponse.getMain().getTempMin())+(char) 0x00B0);
                        }
                    }
                });
                temtogglebutton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<CurrentWeatherResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Need Internet Connection", Toast.LENGTH_SHORT).show();
                temtogglebutton.setVisibility(View.GONE);
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

    private String convertToCelsius(double temp){
        double dTemp = Math.ceil(temp);
        int iTemp = (int) (dTemp - 273.16);
        return String.valueOf(iTemp);
    }
    private String convertToFahrenheit(double temp){
        double dTemp = Math.ceil(temp);
        int iTemp = (int) (dTemp*9/5 - 459.67);
        return String.valueOf(iTemp);
    }
}
