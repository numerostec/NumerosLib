package me.numeros.numeroslib;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * Created by Abraham on 25/07/2015.
 */
public abstract class BaseModel {
    protected JSONObject _data;

    public BaseModel() {
    }

    public BaseModel(String data) throws JSONException {
        this(new JSONObject(data));
    }

    public BaseModel(JSONObject data) throws JSONException {
        _data = data;

        Field[] fields = this.getClass().getFields();
        for (Field field : fields)
            setValue(field);
    }

    public void setValue(Field field) {
        try {
            String fieldName = field.getName();
            Type type = field.getType();

            if (type == int.class)
                field.setInt(this, _data.isNull(fieldName) ? 0 : _data.getInt(fieldName));
            else if (type == double.class)
                field.setDouble(this, _data.isNull(fieldName) ? 0 : _data.getDouble(fieldName));
            else if (type == boolean.class)
                field.setBoolean(this, _data.isNull(fieldName) ? false : _data.getBoolean(fieldName));
            else if (type == long.class)
                field.setLong(this, _data.isNull(fieldName) ? 0 : _data.getLong(fieldName));
            else
                field.set(this, _data.get(fieldName));
        } catch (Exception ex) {
        }
    }

    @Override
    public String toString() {
        return _data.toString();
    }
}
