package me.numeros.numeroslib;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Abraham on 04/08/2015.
 */
public class Pair extends BaseModel {
    public String text;
    public int value;

    public Pair(JSONObject data) throws JSONException {
        super(data);
    }
}
