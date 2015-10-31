package me.numeros.numeroslib;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.provider.Settings;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Abraham on 25/07/2015.
 */
public class NumerosLibApp extends Application {

    private static Context _context;
    private static String _androidId;
    private static String _appName;

    public static Context getContext() {
        return _context;
    }

    public static String getDeviceId() {
        return _androidId;
    }

    public static String getAppName() {
        return _appName;
    }

    //
    protected static JSONObject getSavedJSON(String propertyName, JSONObject defaultValue) {
        String stored = getSavedString(propertyName, null);
        if (stored == null)
            return defaultValue;

        try {
            JSONObject result = new JSONObject(stored);
            return result;
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    protected static JSONArray getSavedJSONArray(String propertyName, JSONArray defaultValue) {
        String stored = getSavedString(propertyName, null);
        if (stored == null)
            return defaultValue;

        try {
            JSONArray result = new JSONArray(stored);
            return result;
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    protected static String getSavedString(String propertyName, String defaultValue) {
        SharedPreferences sharedPref = _context.getSharedPreferences(_appName + ".PREFERENCES", Context.MODE_PRIVATE);
        return sharedPref.getString(propertyName, defaultValue);
    }

    protected static void saveJSON(String propertyName, JSONObject value) {
        saveString(propertyName, value == null ? null : value.toString());
    }

    protected static void saveJSONArray(String propertyName, JSONArray value) {
        saveString(propertyName, value == null ? null : value.toString());
    }

    protected static void saveString(String propertyName, String value) {
        SharedPreferences sharedPref = _context.getSharedPreferences(_appName + ".PREFERENCES", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(propertyName, value);
        editor.commit();
    }

    public static boolean checkGPS(final Activity activity, DialogInterface.OnClickListener action) {
        // Get Location Manager and check for GPS & Network location services
        LocationManager lm = (LocationManager) activity.getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // Build the alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(_context.getString(R.string.gps_inactivo));
            builder.setMessage(_context.getString(R.string.gps_inactivo_msg));
            builder.setPositiveButton(_context.getString(R.string.habilitar), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialogInterface, int i) {
                    // Show location settings when the user acknowledges the alert dialog
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    activity.startActivity(intent);
                }
            });

            if (action != null)
                builder.setNegativeButton(_context.getString(R.string.continuar), action);

            Dialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        } else
            return true;

        return false;
    }

    public static Bitmap getScaledBitmap(String picturePath, int width, int height) {
        BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
        sizeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, sizeOptions);

        int inSampleSize = calculateInSampleSize(sizeOptions, width, height);

        sizeOptions.inJustDecodeBounds = false;
        sizeOptions.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(picturePath, sizeOptions);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        _context = getApplicationContext();
        _androidId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        _appName = _context.getPackageName();
    }
}
