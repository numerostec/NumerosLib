package me.numeros.numeroslib.Weather;

import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;

import me.numeros.numeroslib.BaseCommunicator;
import me.numeros.numeroslib.GenericCommunicator;
import me.numeros.numeroslib.IDataCallback;
import me.numeros.numeroslib.ImageDownloader;
import me.numeros.numeroslib.NumerosLibApp;
import me.numeros.numeroslib.R;
import me.numeros.numeroslib.ServerResponse;

/**
 * Created by abrahamechenique on 21/09/15.
 */
public class OpenWeather {
    public static final int CELCIUS = 1;
    public static final int FAHRENHEIT = 2;
    private static HashMap<String, String> _text;
    public String cityName, icon, id, text;
    public Date date;
    public double temp, temp_max, temp_min;

    public static void getCurrent(String apiKey, double lat, double lng, final int scale, final IDataCallback<OpenWeather> callback) {
        GenericCommunicator comm = new GenericCommunicator(BaseCommunicator.GET) {
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {
                    OpenWeather result = new OpenWeather();
                    result.date = new Date();

                    JSONObject data = new JSONObject(s);
                    result.cityName = data.getString("name");

                    JSONArray weathers = data.getJSONArray("weather");
                    JSONObject weather = weathers.getJSONObject(0);
                    result.icon = weather.getString("icon");
                    result.id = weather.getString("id");
                    result.text = getText(result.id);

                    JSONObject main = data.getJSONObject("main");
                    result.temp = getTemp(scale, main.getDouble("temp"));
                    result.temp_max = getTemp(scale, main.getDouble("temp_max"));
                    result.temp_min = getTemp(scale, main.getDouble("temp_min"));

                    callback.onFinish(ServerResponse.success(), result);
                } catch (Exception ex) {
                    callback.onFinish(ServerResponse.connectionError(), null);
                }
            }
        };

        comm.Parameters.put("APPID", apiKey);
        comm.Parameters.put("lat", String.valueOf(lat));
        comm.Parameters.put("lon", String.valueOf(lng));
        comm.execute("http://api.openweathermap.org/data/2.5/weather");
    }

    public static void getDays(String apiKey, double lat, double lng, final int cnt, final int scale, final IDataCallback<OpenWeather[]> callback) {
        GenericCommunicator comm = new GenericCommunicator(BaseCommunicator.GET) {
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {
                    JSONObject data = new JSONObject(s);

                    JSONObject city = data.getJSONObject("city");
                    String cityName = city.getString("name");

                    JSONArray list = data.getJSONArray("list");
                    OpenWeather[] result = new OpenWeather[list.length()];

                    for (int con1 = 0; con1 < list.length(); con1++) {
                        OpenWeather weather = new OpenWeather();
                        weather.cityName = cityName;

                        JSONObject day = list.getJSONObject(con1);
                        weather.date = new Date(day.getLong("dt") * 1000);

                        JSONObject temp = day.getJSONObject("temp");
                        weather.temp = getTemp(scale, temp.getDouble("day"));
                        weather.temp_max = getTemp(scale, temp.getDouble("max"));
                        weather.temp_min = getTemp(scale, temp.getDouble("min"));

                        JSONArray weathers = day.getJSONArray("weather");
                        JSONObject w = weathers.getJSONObject(0);
                        weather.icon = w.getString("icon");
                        weather.id = w.getString("id");
                        weather.text = getText(weather.id);

                        result[con1] = weather;
                    }

                    callback.onFinish(ServerResponse.success(), result);
                } catch (Exception ex) {
                    callback.onFinish(ServerResponse.connectionError(), null);
                }
            }
        };

        comm.Parameters.put("APPID", apiKey);
        comm.Parameters.put("lat", String.valueOf(lat));
        comm.Parameters.put("lon", String.valueOf(lng));
        comm.Parameters.put("cnt", String.valueOf(cnt));
        comm.execute("http://api.openweathermap.org/data/2.5/forecast/daily");
    }

    private static double getTemp(int to, double kelvin) {
        switch (to) {
            case CELCIUS:
                return kelvin - 273.15;
            case FAHRENHEIT:
                return kelvin * 9d / 5d - 459.67d;
            default:
                return kelvin;
        }
    }

    private static String getText(String id) {
        if (_text == null) {
            String[] tags = NumerosLibApp.getContext().getResources().getStringArray(R.array.weather);

            HashMap<String, String> tmp = new HashMap<>();
            for (String tag : tags) {
                String[] pair = tag.split(":");

                String key = pair[0];
                String value = pair[1];

                tmp.put(key, value);
            }

            _text = tmp;
        }

        return _text.get(id);
    }

    public void getIcon(final IDataCallback<Bitmap> callback) {
        ImageDownloader downloader = new ImageDownloader() {
            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);

                callback.onFinish(bitmap == null ? ServerResponse.connectionError() : ServerResponse.success(), bitmap);
            }
        };

        downloader.execute(String.format("http://openweathermap.org/img/w/%s.png", icon));
    }
}
