package me.numeros.numeroslib;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.util.Hashtable;
import java.util.concurrent.Semaphore;

/**
 * Created by Abraham on 05/08/2015.
 */
public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
    private static Hashtable<String, Bitmap> _downloaded;

    public ImageDownloader() {
        if (_downloaded == null)
            _downloaded = new Hashtable<String, Bitmap>();
    }

    @Override
    protected Bitmap doInBackground(String... args) {
        Semaphore semaphore = new Semaphore(1);
        String url = args[0];
        Bitmap result = null;

        if (!_downloaded.containsKey(url)) {
            try {
                semaphore.acquire();
            } catch (Exception ex) {
            }

            if (!_downloaded.containsKey(url)) {
                try {
                    InputStream in = new java.net.URL(url).openStream();
                    result = BitmapFactory.decodeStream(in);

                    if (result == null)
                        return null;
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();

                    return null;
                }

                _downloaded.put(url, result);
                semaphore.release();
            }
        }

        return _downloaded.get(url);
    }

    public Bitmap getImage(String url) {
        if (_downloaded.containsKey(url))
            return _downloaded.get(url);

        return null;
    }
}
