package com.example.elabuga;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    TextView city, temp, main, humidity, wind, realFeel, time, monTmp, tueTmp, wedTmp, thuTmp;
    TextView day1, day2, day3, day4;
    ImageView weatherImage;
    double tempForecastMax = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        city = findViewById(R.id.id_city);
        temp = findViewById(R.id.id_degree);
        main = findViewById(R.id.id_main);
        humidity = findViewById(R.id.id_humidity);
        wind = findViewById(R.id.id_wind);
        realFeel = findViewById(R.id.id_realfeel);
        weatherImage = findViewById(R.id.id_forecastIcon0);
        time = findViewById(R.id.id_time);

        monTmp = findViewById(R.id.id_forecastTemp1);
        tueTmp = findViewById(R.id.id_forecastTemp2);
        wedTmp = findViewById(R.id.id_forecastTemp3);
        thuTmp = findViewById(R.id.id_forecastTemp4);

        day1 = findViewById(R.id.id_forecastDay1);
        day2 = findViewById(R.id.id_forecastDay2);
        day3 = findViewById(R.id.id_forecastDay3);
        day4 = findViewById(R.id.id_forecastDay4);

        // Получаем погоду в городе Елабуга
        getWeatherForCity("Elabuga");
    }

    private void setWeatherIcon(ImageView imageView, String weatherCode) {
        runOnUiThread(() -> {
            switch (weatherCode) {
                case "01d":
                case "01n":
                    imageView.setImageResource(R.drawable.w01d);
                    break;
                case "02d":
                case "02n":
                    imageView.setImageResource(R.drawable.w02d);
                    break;
                case "03d":
                case "03n":
                    imageView.setImageResource(R.drawable.w03d);
                    break;
                case "04d":
                case "04n":
                    imageView.setImageResource(R.drawable.w04d);
                    break;
                case "09d":
                case "09n":
                    imageView.setImageResource(R.drawable.w09d);
                    break;
                case "10d":
                case "10n":
                    imageView.setImageResource(R.drawable.w10d);
                    break;
                case "11d":
                case "11n":
                    imageView.setImageResource(R.drawable.w11d);
                    break;
                case "13d":
                case "13n":
                    imageView.setImageResource(R.drawable.w13d);
                    break;
                default:
                    imageView.setImageResource(R.drawable.w03d);
            }
        });
    }

    private void getWeatherForCity(String city) {
        OkHttpClient client = new OkHttpClient();
        String apiKey = "09aef57358b15e4792bc3b4d56e74d45";
        Request request = new Request.Builder()
                .url("https://api.openweathermap.org/data/2.5/forecast?q=" + city + "&appid=" + apiKey + "&units=metric")
                .get()
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseData = response.body().string();

                        JSONObject jsonResponse = new JSONObject(responseData);
                        JSONArray forecastList = jsonResponse.getJSONArray("list");

                        List<Object> currentDayData = new ArrayList<>();
                        List<String> dayOfWeekForOthers = new ArrayList<>();
                        List<Double> maxTemperatures = new ArrayList<>();

                        for (int i = 0; i < forecastList.length(); i++) {
                            JSONObject dayForecast = forecastList.getJSONObject(i);
                            String dateTxt = dayForecast.getString("dt_txt").split(" ")[0];

                            String weatherCode = dayForecast.getJSONArray("weather").getJSONObject(0).getString("icon");
                            setWeatherIcon(weatherImage, weatherCode);
                            double tempMax = dayForecast.getJSONObject("main").getDouble("temp_max");
                            double humidity = dayForecast.getJSONObject("main").getDouble("humidity");
                            double feelsLike = dayForecast.getJSONObject("main").getDouble("feels_like");
                            double windSpeed = dayForecast.getJSONObject("wind").getDouble("speed");
                            String description = dayForecast.getJSONArray("weather").getJSONObject(0).getString("main");

                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                            Date date = format.parse(dateTxt);
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            String dayOfWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(calendar.getTime());

                            if (i == 0) {
                                currentDayData.add(tempMax);
                                currentDayData.add(humidity);
                                currentDayData.add(feelsLike);
                                currentDayData.add(windSpeed);
                                currentDayData.add(description);
                                currentDayData.add(dayOfWeek);
                                tempForecastMax = tempMax;
                            } else {
                                maxTemperatures.add(tempMax);
                                dayOfWeekForOthers.add(dayOfWeek);
                            }
                        }

//                        Вот какой порядок в матрице: Температура, Влажность, По ощущению, скорость ветра, состояние погоды


                        runOnUiThread(() -> {
                            temp.setText(String.valueOf(Math.round(tempForecastMax)));
                            humidity.setText(currentDayData.get(1) + "%");
                            realFeel.setText(currentDayData.get(2) + "°");
                            wind.setText(currentDayData.get(3) + " km/h");
                            main.setText(currentDayData.get(4) + "");
                            time.setText(currentDayData.get(5) + "");

                            monTmp.setText(String.valueOf(Math.round(maxTemperatures.get(0))) + "°");
                            tueTmp.setText(String.valueOf(Math.round(maxTemperatures.get(1))) + "°");
                            wedTmp.setText(String.valueOf(Math.round(maxTemperatures.get(2))) + "°");
                            thuTmp.setText(String.valueOf(Math.round(maxTemperatures.get(3))) + "°");

                            day1.setText(dayOfWeekForOthers.get(1));
                            day2.setText(dayOfWeekForOthers.get(10));
                            day3.setText(dayOfWeekForOthers.get(20));
                            day4.setText(dayOfWeekForOthers.get(30));
                        });
                    }

                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}