package me.numeros.numeroslib;

import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Abraham on 25/07/2015.
 */
public abstract class BaseCommunicator extends AsyncTask<String, Void, String> {
    public static final int POST = 1;
    public static final int GET = 2;
    public Hashtable<String, String> Parameters;
    public HttpContext context;
    private String _baseUrl;
    private int _method;

    public BaseCommunicator(int method) {
        this(method, null);
    }

    public BaseCommunicator(int method, String baseUrl) {
        _method = method;
        _baseUrl = baseUrl;

        Parameters = new Hashtable<String, String>();
    }

    public String GenerateUrl(String servicePath, String... args) {
        StringBuilder builder = new StringBuilder();

        if (_baseUrl != null) {
            builder.append(_baseUrl);

            if (!servicePath.startsWith("/"))
                builder.append("/");
        }

        builder.append(servicePath);

        for (int con1 = 0; con1 < args.length; con1++) {
            String value;

            try {
                value = URLEncoder.encode(args[con1], "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                value = "";
            }

            builder.append("/");
            builder.append(value);
        }

        return builder.toString();
    }

    public String doProcess(String... args) {
        HttpParams httpParameters = new BasicHttpParams();
        httpParameters.setParameter("Cache-Control", "no-cache");

        HttpProtocolParams.setContentCharset(httpParameters, HTTP.UTF_8);
        HttpProtocolParams.setHttpElementCharset(httpParameters, HTTP.UTF_8);

        DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
        httpClient.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);
        httpClient.getParams().setParameter("http.socket.timeout", new Integer(60000));
        httpClient.getParams().setParameter("http.protocol.content-charset", HTTP.UTF_8);
        httpParameters.setBooleanParameter("http.protocol.expect-continue", false);

        HttpUriRequest request;

        if (_method == POST) {
            request = new HttpPost(args[0]);
            request.getParams().setParameter("http.socket.timeout", new Integer(60000));

            if (Parameters.size() > 0) {
                List<NameValuePair> parameters = new ArrayList<NameValuePair>();
                for (String key : Parameters.keySet())
                    parameters.add(new BasicNameValuePair(key, Parameters.get(key)));

                try {
                    ((HttpPost) request).setEntity(new UrlEncodedFormEntity(parameters, HTTP.UTF_8));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        } else {
            String url = args[0];

            if (Parameters.size() > 0) {
                for (String key : Parameters.keySet())
                    try {
                        url += String.format("%s%s=%s",
                                url.contains("?") ? "&" : "?",
                                key,
                                URLEncoder.encode(Parameters.get(key), "utf-8"));
                    } catch (Exception ex) {
                    }
            }

            request = new HttpGet(url);
        }

        InputStream inputStream = null;
        String result = null;
        try {
            HttpResponse response = context == null ? httpClient.execute(request) : httpClient.execute(request, context);
            HttpEntity entity = response.getEntity();

            inputStream = entity.getContent();
            // json is UTF-8 by default
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            result = sb.toString();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) inputStream.close();
            } catch (Exception squish) {
            }
        }

        return null;
    }

    @Override
    protected String doInBackground(String... args) {
        return doProcess(args);
    }
}
