package me.numeros.numeroslib;

/**
 * Created by Abraham on 27/07/2015.
 */
public interface IDataCallback<T> {
    void onFinish(ServerResponse response, T result);
}
