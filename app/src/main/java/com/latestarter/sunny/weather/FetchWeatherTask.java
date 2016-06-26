package com.latestarter.sunny.weather;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.latestarter.sunny.BuildConfig;
import com.latestarter.sunny.R;

import java.io.IOException;
import java.net.URL;

public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

    private final Context context;
    private final ArrayAdapter<String> listAdapter;
    private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

    private static final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
    private static final String QUERY_PARAM = "q";
    private static final String FORMAT_PARAM = "mode";
    private static final String UNITS_PARAM = "units";
    private static final String DAYS_PARAM = "cnt";
    private static final String APPID_PARAM = "APPID";


    public FetchWeatherTask(Context context, ArrayAdapter<String> listAdapter) {
        this.context = context;
        this.listAdapter = listAdapter;
    }

    @Override
    protected String[] doInBackground(String... params) {

        String format = "json";
        int numDays = 7;

        // Build URL
        Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, params[0])
                .appendQueryParameter(FORMAT_PARAM, format)
                .appendQueryParameter(UNITS_PARAM, params[1])
                .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                .appendQueryParameter(APPID_PARAM, BuildConfig.OPEN_WEATHER_MAP_API_KEY)
                .build();
        Log.i(LOG_TAG, builtUri.toString());

        String[] weatherData = null;

        try {
            URL url = new URL(builtUri.toString());

            String textData = new UrlDataFetcher().fetchStringResponse(url);
            if (null == textData || textData.length() == 0) {
                throw new IOException(context.getString(R.string.url_empty_response));
            }

            // Does user prefers to see in Fahrenheit? Check the settings.
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            String units = preferences.getString(context.getString(R.string.pref_key_units), context.getString(R.string.pref_default_units));

            weatherData = new JsonWeatherParser().getWeatherDataFromJson(textData, units);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }

        return weatherData;
    }

    @Override
    protected void onPostExecute(String[] strings) {
        if (null != strings) {
            listAdapter.clear();
            listAdapter.addAll(strings);
        }
    }
}
