package me.numeros.numeroslib;

import android.location.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.concurrent.Semaphore;

/**
 * Created by Abraham on 04/08/2015.
 */
public class MapDirecction extends BaseModel {
    public static final int DRIVING = 1;
    public static final int WALKING = 2;
    public static final int BICYCLING = 3;
    public static final int TRANSIT = 4;
    private static String[] modes = new String[]{"driving", "walking", "bicycling", "transit"};
    public Pair distance, duration;
    public String start_address, end_address, html_instructions;
    public Location start_location, end_location;
    public MapDirecction[] steps;

    public MapDirecction(JSONObject data) throws JSONException {
        super(data);
    }

    public static MapDirecction[] parse(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            return parse(jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs"));
        } catch (Exception ex) {
        }

        return null;
    }

    public static MapDirecction[] parse(JSONArray array) {
        try {
            MapDirecction[] result = new MapDirecction[array.length()];

            for (int con1 = 0; con1 < array.length(); con1++)
                result[con1] = new MapDirecction(array.getJSONObject(con1));

            return result;

        } catch (Exception ex) {
        }

        return null;
    }

    public static void traceRoute(Location from, Location to, int mode, IDataCallback<MapDirecction[]> callback) {
        traceRoute(from, to, mode, "ES", callback);
    }

    public static void traceRoute(Location from, Location to, int mode, String lang, final IDataCallback<MapDirecction[]> callback) {
        final Semaphore semaphore = new Semaphore(1);

        try {
            semaphore.acquire();
        } catch (Exception ex) {
        }

        GenericCommunicator comm = new GenericCommunicator(BaseCommunicator.GET) {
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                semaphore.release();

                ServerResponse response = ServerResponse.connectionError();
                response.success = s != null;

                if (response.success)
                    callback.onFinish(response, parse(s));
                else
                    callback.onFinish(response, null);
            }
        };

        comm.execute(String.format("http://maps.googleapis.com/maps/api/directions/json?origin=%s,%s&destination=%s,%s&sensor=false&units=metric&mode=%s&language=%s",
                from.getLatitude(), from.getLongitude(), to.getLatitude(), to.getLongitude(), modes[mode - 1], lang));
    }

    @Override
    public void setValue(Field field) {
        try {
            switch (field.getName()) {
                case "DRIVING":
                case "WALKING":
                case "BICYCLING":
                case "TRANSIT":
                    break;
                case "distance":
                case "duration":
                    if (_data.isNull(field.getName()))
                        field.set(this, null);
                    else {
                        JSONObject jsonObject = _data.getJSONObject(field.getName());
                        field.set(this, new Pair(jsonObject));
                    }
                    break;
                case "start_location":
                case "end_location":
                    if (_data.isNull(field.getName()))
                        field.set(this, null);
                    else {
                        JSONObject jsonObject = _data.getJSONObject(field.getName());
                        Location location = new Location(field.getName());
                        location.setLatitude(jsonObject.getDouble("lat"));
                        location.setLongitude(jsonObject.getDouble("lng"));
                        field.set(this, location);
                    }

                    break;
                case "steps":
                    if (!_data.isNull(field.getName()))
                        field.set(this, parse(_data.getJSONArray(field.getName())));
                    break;
                default:
                    super.setValue(field);
            }
        } catch (Exception ex) {
        }
    }
}
