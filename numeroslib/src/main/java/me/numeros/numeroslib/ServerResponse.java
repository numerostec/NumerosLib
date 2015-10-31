package me.numeros.numeroslib;

/**
 * Created by Abraham on 25/07/2015.
 */
public class ServerResponse extends BaseModel {
    public boolean success;
    public int results;
    public String message;
    public Object data;

    private ServerResponse() {
    }

    public ServerResponse(String data) throws Exception {
        super(data);
    }

    public static ServerResponse connectionError() {
        ServerResponse result = new ServerResponse();
        result.message = NumerosLibApp.getContext().getString(R.string.connection_error);

        return result;
    }

    public static ServerResponse success() {
        ServerResponse result = new ServerResponse();
        result.success = true;

        return result;
    }
}
