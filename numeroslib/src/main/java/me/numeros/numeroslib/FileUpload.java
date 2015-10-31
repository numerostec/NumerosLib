package me.numeros.numeroslib;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Abraham on 23/10/2015.
 */
public class FileUpload extends BaseModel {
    public String guid, name, type;
    public long size;

    public FileUpload(JSONObject data) throws JSONException {
        super(data);
    }

    public static FileUpload[] parse(JSONArray data) {
        FileUpload[] result = new FileUpload[data.length()];

        for (int con1 = 0; con1 < data.length(); con1++)
            try {
                result[con1] = new FileUpload(data.getJSONObject(con1));
            } catch (Exception ex) {
            }

        return result;
    }

    public static void upload(String filePath, String url, final IDataCallback<FileUpload> callback) {
        UploadTask uploadTask = new UploadTask() {
            @Override
            protected void onPostExecute(JSONArray jsonArray) {
                super.onPostExecute(jsonArray);

                if (jsonArray == null || jsonArray.length() == 0) {
                    callback.onFinish(ServerResponse.connectionError(), null);
                    return;
                }

                FileUpload[] uploads = FileUpload.parse(jsonArray);
                callback.onFinish(ServerResponse.success(), uploads[0]);
            }
        };

        uploadTask.execute("0", filePath, url);
    }
}
