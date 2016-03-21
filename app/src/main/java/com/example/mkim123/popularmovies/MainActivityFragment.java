package com.example.mkim123.popularmovies;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Display Grid of Movie Thumbnails
 */
public class MainActivityFragment extends Fragment {

    private ImageAdapter mMovieAdapter;
    private List<String> mMovieIDs;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.grid_view);

        mMovieIDs = new ArrayList<String>();
        mMovieAdapter = new ImageAdapter(getActivity(), R.layout.poster_thumbnail, new ArrayList<String>());
        int displayWidth = getResources().getDisplayMetrics().widthPixels;
        gridView.setColumnWidth(displayWidth / gridView.getNumColumns());
        gridView.setAdapter(mMovieAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String moviePoster = (String) mMovieAdapter.getItem(position);
                String movieID = mMovieIDs.get(position);
                String[] idAndPoster = {movieID, moviePoster};
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("IDAndPoster", idAndPoster);
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    private void updateMovies() {
        FetchMovieIDPoster moviesTask = new FetchMovieIDPoster();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortBy = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_def));
        moviesTask.execute(sortBy);
    }

    // Fetches all movie id img_path space separated String arr
    public class FetchMovieIDPoster extends FetchMovieInfo {

        @Override
        protected String[] doInBackground(String... params) {

            // If no params, nothing to lookup
            if (params.length == 0) {
                return null;
            }
            String sortBy = params[0];

            // Raw JSON response as string
            String moviesJsonStr = null;
            try {
                final String APPID_PARAM = "api_key";
                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendEncodedPath(sortBy)
                        .appendQueryParameter(APPID_PARAM, API_KEY).build();

                URL url = new URL(builtUri.toString());
                moviesJsonStr = queryTMDBForJsonStr(url);
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Malformed URL", e);
                return null;
            }

            try {
                return parseInfoFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.toString(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);

            if (result != null) {
                mMovieAdapter.clear();
                for (String posterPath : result) {
                    mMovieAdapter.add(posterPath);
                }
            }
        }

        protected String[] parseInfoFromJson(String moviesJsonStr) throws JSONException {
            final String RESULTS = "results";
            final String MOVIE_ID = "id";
            final String POSTER_PATH = "poster_path";

            JSONObject movieJson = new JSONObject(moviesJsonStr);
            JSONArray resultsArray = movieJson.getJSONArray(RESULTS);

            String[] resultStrs = new String[resultsArray.length()];
            mMovieIDs.clear();
            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject curResult = resultsArray.getJSONObject(i);
                String curID = curResult.getString(MOVIE_ID);
                mMovieIDs.add(curID);

                String curPoster = BASE_POSTER_URL + curResult.getString(POSTER_PATH);
                resultStrs[i] = curPoster;
            }
            return resultStrs;
        }
    }
}
