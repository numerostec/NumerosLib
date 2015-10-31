package me.numeros.numeroslib.Google;

import org.json.JSONException;
import org.json.JSONObject;

import me.numeros.numeroslib.BaseModel;

/**
 * Created by abrahamechenique on 18/09/15.
 */
public class Language extends BaseModel {
    public String name, language;

    public Language(JSONObject data) throws JSONException {
        super(data);
    }
}
