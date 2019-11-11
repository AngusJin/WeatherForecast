package com.sitan.weatherforecast;

import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SendRequest implements Runnable {
    private WeatherInfo info;
    private String city_code;

    public WeatherInfo getInfo() {
        return this.info;
    }

    public SendRequest(String city_code) {
        this.city_code = city_code;
    }

    @Override
    public void run() {
        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            String url = "http://t.weather.sojson.com/api/weather/city/" + city_code;
            Request request = new Request.Builder().url(url).build();
            Response response = okHttpClient.newCall(request).execute();
            switch (response.code()) {
                case 200:
                    info = new Gson().fromJson(response.body().string(), WeatherInfo.class);
                    break;
                case 400:
                case 403:
                case 404:
                    info = new WeatherInfo();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
