package me.numeros.numeroslib;

/**
 * Created by abrahamechenique on 18/09/15.
 */
public class SendGridMessage {

    public static void send(String from, String to, String subject, String text, String apiUser, String apiKey, final ICallback callback) {

        GenericCommunicator comm = new GenericCommunicator(BaseCommunicator.GET) {
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                callback.onFinish(s.contains("success") ? ServerResponse.success() : ServerResponse.connectionError());
            }
        };

        comm.Parameters.put("api_user", apiUser);
        comm.Parameters.put("api_key", apiKey);
        comm.Parameters.put("to", to);
        comm.Parameters.put("subject", subject);
        comm.Parameters.put("text", text);
        comm.Parameters.put("from", from);
        comm.execute("https://api.sendgrid.com/api/mail.send.json");
    }

}
