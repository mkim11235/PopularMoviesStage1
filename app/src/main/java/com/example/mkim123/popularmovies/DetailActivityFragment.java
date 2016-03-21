package com.example.mkim123.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Movie Details.
 * Show original title, poster thumbnail, plot synopsis, user rating, release date
 */
public class DetailActivityFragment extends Fragment {
    private String mID;
    private String mPosterPath;

    private TextView mOriginalTitle;
    private TextView mUserRating;
    private TextView mReleaseDate;
    private TextView mPlot;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mOriginalTitle = (TextView) rootView.findViewById(R.id.detail_title);
        mUserRating = (TextView) rootView.findViewById(R.id.detail_rating);
        mReleaseDate = (TextView) rootView.findViewById(R.id.detail_release_date);
        mPlot = (TextView) rootView.findViewById(R.id.detail_plot);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("IDAndPoster")) {
            String[] idAndPoster = intent.getStringArrayExtra("IDAndPoster");
            mID = idAndPoster[0];
            mPosterPath = idAndPoster[1];
        }

        int displayWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = displayWidth / 2;
        ImageView v = (ImageView) rootView.findViewById(R.id.detail_poster);
        Picasso.with(getActivity()).load(mPosterPath)
                .resize(imageWidth, 0)
                .into(v);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateDetails();
    }

    private void updateDetails() {
        FetchMovieDetails detailsTask = new FetchMovieDetails();
        detailsTask.execute(mID);
    }

    // Given a movieID as string
    // Fetches Original title, vote_average (rating), release date, overview (plot synopsis)
    public class FetchMovieDetails extends FetchMovieInfo {

        @Override
        protected String[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            // Raw JSON response string
            String movieJsonStr = null;

            try {
                final String APPID_PARAM = "api_key";
                Uri builtUri = Uri.parse(BASE_URL).buildUpon().appendEncodedPath(params[0])
                        .appendQueryParameter(APPID_PARAM, API_KEY).build();
                URL url = new URL(builtUri.toString());
                movieJsonStr = queryTMDBForJsonStr(url);
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Malformed URL", e);
                return null;
            }

            try {
                return parseInfoFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] results) {
            super.onPostExecute(results);

            mOriginalTitle.append(results[0]);
            mUserRating.append(results[1]);
            mReleaseDate.append(results[2]);
            mPlot.append(results[3]);
        }

        // Return Original title, vote_average (rating), release date, overview (plot synopsis)
        @Override
        protected String[] parseInfoFromJson(String jsonStr) throws JSONException {
            final String TITLE = "original_title";
            final String RATING = "vote_average";
            final String RELEASE_DATE = "release_date";
            final String PLOT = "overview";
            final String[] DESIRED_INFO = {
                    TITLE,
                    RATING,
                    RELEASE_DATE,
                    PLOT
            };

            JSONObject movieJson = new JSONObject(jsonStr);
            String[] results = new String[DESIRED_INFO.length];
            for (int i = 0; i < results.length; i++) {
                results[i] = movieJson.getString(DESIRED_INFO[i]);
            }
            return results;
        }
    }
}
