package com.example.mkim123.popularmovies;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by mkim123 on 3/18/2016.
 */
public abstract class FetchMovieInfo extends AsyncTask<String, Void, String[]> {
    protected static final String API_KEY = "ILLEGAL_TO_UPLOAD_API_KEY_ON_GIT";
    protected static final String BASE_URL = "http://api.themoviedb.org/3/movie/";
    protected static final String BASE_POSTER_URL = "http://image.tmdb.org/t/p/w185/";
    protected final String LOG_TAG = getClass().getSimpleName();

    // Given Json as string, returns desired string data
    protected abstract String[] parseInfoFromJson(String jsonStr) throws JSONException;

    // Queries TMDB using given URL
    // Returns Json as string
    protected String queryTMDBForJsonStr(URL url) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Raw JSON response as string
        String jsonStr = null;

        try {
            // Create request to DB and open connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read input stream into string
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                Log.v(LOG_TAG, "Input stream is null");
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                Log.v(LOG_TAG, "Buffer is null");
                return null;
            }

            jsonStr = buffer.toString();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing reader stream", e);
                }
            }
        }
        return jsonStr;
    }
}
