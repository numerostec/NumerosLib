package me.numeros.numeroslib.Google;

import org.json.JSONArray;
import org.json.JSONObject;

import me.numeros.numeroslib.BaseCommunicator;
import me.numeros.numeroslib.GenericCommunicator;
import me.numeros.numeroslib.IDataCallback;
import me.numeros.numeroslib.ServerResponse;

/**
 * Created by abrahamechenique on 18/09/15.
 */
public class Translator {

    public static void getLanguages(String key, String target, final IDataCallback<Language[]> callback) {
        GenericCommunicator comm = new GenericCommunicator(BaseCommunicator.GET) {
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {
                    JSONObject result = new JSONObject(s);
                    JSONObject data = result.getJSONObject("data");

                    callback.onFinish(ServerResponse.success(), parseLang(data.getJSONArray("languages")));
                } catch (Exception ex) {
                    callback.onFinish(ServerResponse.connectionError(), null);
                }
            }
        };

        comm.Parameters.put("key", key);
        comm.Parameters.put("target", target);
        comm.execute("https://www.googleapis.com/language/translate/v2/languages");
    }

    public static void translate(String key, String q, String source, String target, final IDataCallback<String> callback) {
        GenericCommunicator comm = new GenericCommunicator(BaseCommunicator.GET) {
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {
                    JSONObject result = new JSONObject(s);
                    JSONObject data = result.getJSONObject("data");
                    JSONArray translations = data.getJSONArray("translations");
                    JSONObject translation = translations.getJSONObject(0);

                    callback.onFinish(ServerResponse.success(), translation.getString("translatedText"));
                } catch (Exception ex) {
                    callback.onFinish(ServerResponse.connectionError(), null);
                }
            }
        };

        comm.Parameters.put("key", key);
        comm.Parameters.put("source", source);
        comm.Parameters.put("target", target);
        comm.Parameters.put("q", q);
        comm.execute("https://www.googleapis.com/language/translate/v2");
    }


    private static Language[] parseLang(JSONArray data) {
        Language[] result = new Language[data.length()];

        for (int con1 = 0; con1 < data.length(); con1++)
            try {
                result[con1] = new Language(data.getJSONObject(con1));
            } catch (Exception ex) {
            }

        return result;
    }
}
