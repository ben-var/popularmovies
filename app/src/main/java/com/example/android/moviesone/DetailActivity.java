package com.example.android.moviesone;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.moviesone.utilities.NetworkUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * View to display individual movie data queried from themoviedb. This page acts as an overview
 * for a specific (singular) movie.
 */

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();
    private static final String MOVIE_CLASS =  Movie.class.getSimpleName();

    private static final int HIGHEST_POSSIBLE_SCORE = 10;

    private Movie mMovie;

    private TextView mTitleTextView;
    private ImageView mPosterView;
    private TextView mYearTextView;
    private TextView mRatingTextView;
    private TextView mOverviewTextView;
    private ImageView mBackdropView;
    // TODO Implement the correct views to display movie trailers
    private TextView mTrailerViews;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mTitleTextView = (TextView) findViewById(R.id.movie_title_tv);
        mPosterView = (ImageView) findViewById(R.id.movie_poster_iv);
        mYearTextView = (TextView) findViewById(R.id.movie_year_tv);
        mRatingTextView = (TextView) findViewById(R.id.movie_rating_tv);
        mOverviewTextView = (TextView) findViewById(R.id.movie_overview_tv);
        mBackdropView = (ImageView) findViewById(R.id.backdrop_iv);
        mTrailerViews = (TextView) findViewById(R.id.trailer_views);

        Intent intentStartedThisActivity = getIntent();

        /*
         * Loading every single view with the appropriate information, broken into logical
         * steps.
         */
        if (intentStartedThisActivity != null) {
            if (intentStartedThisActivity.hasExtra(MOVIE_CLASS)) {
                mMovie = (Movie) intentStartedThisActivity.getSerializableExtra(MOVIE_CLASS);

                mTitleTextView.setText(mMovie.getTitle());

                URL moviePosterImageURL = NetworkUtils
                        .getMoviePosterImageURL(mMovie, NetworkUtils.MEDIUM_IMAGE);

                Picasso.with(this)
                        .load(moviePosterImageURL.toString())
                        .fit()
                        .into(mPosterView, new Callback() {
                            @Override
                            public void onSuccess() {
                                // no message necessary
                            }

                            @Override
                            public void onError() {
                                mPosterView.setVisibility(View.GONE);
                                Toast.makeText(DetailActivity.this,
                                        R.string.error_image_load, Toast.LENGTH_SHORT).show();
                            }
                        });

                String yearText =  getFormattedDate(mMovie);
                mYearTextView.setText(yearText);

                String voteAvgText = mMovie.getVoteAverage() + " / " + HIGHEST_POSSIBLE_SCORE;
                mRatingTextView.setText(voteAvgText);
                int ratingColor = ContextCompat.getColor(this, getRatingColor(mMovie));
                mRatingTextView.setTextColor(ratingColor);

                String overviewText = getString(R.string.overview_label) + "\n\n"
                                                                    + mMovie.getOverview();
                mOverviewTextView.setText(overviewText);

                URL movieBackgroundImageURL = NetworkUtils
                        .getMovieBackdropImageURL(mMovie, NetworkUtils.LARGE_IMAGE);

                Picasso.with(this)
                        .load(movieBackgroundImageURL.toString())
                        .resize(1100, 900)
                        .centerCrop()
                        .into(mBackdropView, new Callback() {
                            @Override
                            public void onSuccess() {
                                // no message necessary
                            }

                            @Override
                            public void onError() {
                                mBackdropView.setVisibility(View.GONE);
                                Toast.makeText(DetailActivity.this,
                                        "Error loading image", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        } else {
            Toast.makeText(DetailActivity.this,
                    R.string.error_movie_retrieval_message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This method will return an associated color ID according to the following classification:
     *
     * GREEN: 0.6 < SCORE <= 10
     * ORANGE: 0.5 <= SCORE <= 6.0
     * RED: SCORE < 0.5
     *
     * @param movie movie with an associated vote average rating
     * @return the appropriate color coding (id of a color in the colors.xml file
     */
    private int getRatingColor(Movie movie) {
        double score = movie.getVoteAverage();

        if (score > 6.0) {
            return R.color.good_reviews;
        } else if (score >= 5.0 && score <= 6.0) {
            return R.color.mediocre_reviews;
        } else if (score < 5.0) {
            return R.color.poor_reviews;
        } else {
            return R.color.unknown_score;
        }
    }

    /**
     * Utilizing the SimpleDateFormat android library class, this function will take
     * as input a movie DB release date format, and return a user friendly formatted
     * version: [FULL MONTH NAME], DAY OF MONTH, YEAR
     *
     * @param movie Movie object with an associated release date
     * @return formatted String version of the release date
     */
    private String getFormattedDate(Movie movie) {
        String resultString = null;

        SimpleDateFormat inputFormat = new SimpleDateFormat(
                getString(R.string.input_date_format_moviedb), Locale.US);
        SimpleDateFormat outputFormat = new SimpleDateFormat(
                getString(R.string.user_facing_pretty_date_format), Locale.US);

        Date movieDate = null;
        try {
            movieDate = inputFormat.parse(movie.getReleaseDate());

            resultString = outputFormat.format(movieDate);
        } catch (ParseException e) {
            Log.e(TAG, getString(R.string.error_parse_release_date));
            resultString = movie.getReleaseDate();
        }

        return resultString;
    }
}

