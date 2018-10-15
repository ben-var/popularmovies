package com.example.android.moviesone;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import adapters.ReviewAdapter;
import adapters.TrailerAdapter;
import database.AppDatabase;
import database.AppExecutors;
import model.Movie;
import model.Review;
import model.Trailer;
import utilities.NetworkUtils;
import utilities.ReviewJSONUtils;
import utilities.TrailerJSONUtils;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import adapters.TrailerAdapter.TrailerAdapterOnClickHandler;
import viewmodels.MainViewModel;

/**
 * View to display individual movie data queried from themoviedb. This page acts as an overview
 * for a specific (singular) movie.
 */

public class DetailActivity extends AppCompatActivity implements TrailerAdapterOnClickHandler {

    private static final String TAG = DetailActivity.class.getSimpleName();
    private static final String MOVIE_CLASS =  Movie.class.getSimpleName();

    private AppDatabase mDb;
    private List<Movie> mFavorites;

    private static final int HIGHEST_POSSIBLE_SCORE = 10;

    private Movie mMovie;

    private TextView mTitleTextView;
    private ImageView mPosterView;
    private TextView mYearTextView;
    private TextView mRatingTextView;
    private Button mFavoritesButton;
    private TextView mOverviewTextView;
    private ImageView mBackdropView;

    private TextView mTrailerTitleView;
    private RecyclerView mTrailerViews;
    private TrailerAdapterOnClickHandler mTrailerClickHandler;
    private TrailerAdapter mTrailerAdapter;
    private List<Trailer> trailerList;

    private RecyclerView mReviewViews;
    private ReviewAdapter mReviewAdapter;
    private List<Review> reviewList;

    private ProgressBar mLoadingIndicator;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mDb = AppDatabase.getInstance(getApplicationContext());

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        mTitleTextView = (TextView) findViewById(R.id.movie_title_tv);
        mPosterView = (ImageView) findViewById(R.id.movie_poster_iv);
        mYearTextView = (TextView) findViewById(R.id.movie_year_tv);
        mRatingTextView = (TextView) findViewById(R.id.movie_rating_tv);
        mFavoritesButton = (Button) findViewById(R.id.add_to_favorites_button);
        mOverviewTextView = (TextView) findViewById(R.id.movie_overview_tv);
        mBackdropView = (ImageView) findViewById(R.id.backdrop_iv);

        mTrailerTitleView = (TextView) findViewById(R.id.trailer_section_title);
        mTrailerViews = (RecyclerView) findViewById(R.id.trailer_views);

        mReviewViews = (RecyclerView) findViewById(R.id.review_views);

        Intent intentStartedThisActivity = getIntent();

        /*
         * Loading every single view with the appropriate information, broken into logical
         * steps.
         */
        if (intentStartedThisActivity != null) {
            if (intentStartedThisActivity.hasExtra(MOVIE_CLASS)) {
                mMovie = intentStartedThisActivity.getParcelableExtra(MOVIE_CLASS);

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

                View.OnClickListener favButtonListener = new View.OnClickListener() {

                    /**
                     * Called when a view has been clicked.
                     *
                     * @param v The view that was clicked.
                     */
                    @Override
                    public void onClick(View v) {
                        onFavoriteButtonClicked();
                    }
                };

                mFavoritesButton.setOnClickListener(favButtonListener);
                checkFavoriteState();

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



            int numOfColumns = 3;
            GridLayoutManager trailerLayoutManager = new GridLayoutManager(this, numOfColumns);
            mTrailerViews.setLayoutManager(trailerLayoutManager);
            mTrailerAdapter = new TrailerAdapter(this);
            mTrailerViews.setAdapter(mTrailerAdapter);
            loadTrailerData();

            LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(this);
            mReviewViews.setLayoutManager(reviewLayoutManager);
            mReviewAdapter = new ReviewAdapter();
            mReviewViews.setAdapter(mReviewAdapter);
            loadReviewData();

        } else {
            Toast.makeText(DetailActivity.this,
                    R.string.error_movie_retrieval_message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This method is called when the "Add To Favorites" button is clicked.
     *
     * This method will take the currently selected movie and add it to the underlying database.
     */
    public void onFavoriteButtonClicked() {

        final Movie movieSelected = mMovie;
        final Context context = getApplicationContext();

        final LiveData<List<Movie>> moviesLD = mDb.movieDao().loadAllMovies();

        moviesLD.observe(this, new Observer<List<Movie>>() {
            boolean added;

            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                if (movies != null && !movies.contains(movieSelected)) {
                    Log.d(TAG, movies.toString());
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            mDb.movieDao().insertMovie(movieSelected);
                        }
                    });
                    added = true;
                } else {
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            mDb.movieDao().deleteMovieByMovieId(movieSelected.getMovieId());
                        }
                    });
                    added = false;
                }

                makeToast();
                moviesLD.removeObserver(this);
            }

            public void makeToast() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // source of if statement and code block to help with Toast off the main thread
                        // https://stackoverflow.com/questions/23038682/java-lang-runtimeexception-only-one-looper-may-be-created-per-thread
                        if (Looper.myLooper() == null) {
                            Looper.prepare();
                        }
                        if (added) {
                            Toast.makeText(getApplicationContext(), getString(R.string.add_favorites_confirm),
                                    Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.in_favorites_already_msg),
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
            }
        });
    }

    public void checkFavoriteState() {

        final Movie movieSelected = mMovie;
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                if (movies != null) {
                    updateFavorites(movies.contains(movieSelected));
                }
            }

            /**
             * Will update the favorites button to appropriate appearance depending on boolean
             * value representing whether the movie is in the DB or not.
             * @param inDB
             */
            public void updateFavorites(boolean inDB) {
                if (inDB) {
                    mFavoritesButton.setText(R.string.remove_from_favorites);
                    mFavoritesButton.setBackgroundColor(Color.argb(255,139, 0, 0));
                } else {
                    mFavoritesButton.setText(R.string.add_to_favorites);
                    mFavoritesButton.setBackgroundColor(Color.argb(255,34,139,34));
                }
            }
        });
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

    @Override
    public void onClick(Trailer trailerSelected) {
        final Context context = getApplicationContext();
        // Code block found on https://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent
        // Opens the youtube video via app if installed, otherwise uses a browser
        Intent appIntent = new Intent(Intent.ACTION_VIEW,
                NetworkUtils.getTrailerYouTubeUriForApp(trailerSelected));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                NetworkUtils.getTrailerVideoYouTubeUri(trailerSelected));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }

    public void loadTrailerData() {
        if (isOnline()) {
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mTrailerViews.setVisibility(View.VISIBLE);
                    new FetchTrailersTask().execute();
                }
            });
        } else {
            Log.e(TAG, "Error retrieving trailers for movie:" + mMovie.getMovieId());
        }
    }

    public void loadReviewData() {
        if (isOnline()) {
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mReviewViews.setVisibility(View.VISIBLE);
                    new FetchReviewsTask().execute();
                }
            });
        } else {
            Log.e(TAG, "Error retrieving reviews for movie: " + mMovie.getMovieId());
        }
    }

    /**
     * Asynctask that will perform an asynchronous query to return to the main activity.
     */
    public class FetchTrailersTask extends AsyncTask<Void, Void, List<Trailer>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param voids The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected List<Trailer> doInBackground(Void... voids) {

            final List<Trailer> trailers;
            URL trailersRequestURL = null;

            trailersRequestURL = NetworkUtils.getEngTrailersForMovie(mMovie);

            if (trailersRequestURL != null) {
                try{
                    String JSONResponse = NetworkUtils.GetResponseFromHttpUrl(trailersRequestURL);

                    trailers = TrailerJSONUtils.getTrailersFromJSON(JSONResponse);
                    return trailers;
                } catch (Exception e) {
                    e.printStackTrace();

                    return null;
                }
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Trailer> trailers) {

            trailerList = trailers;

            // Ths size must be checked first because a NONNULL List is returned
            // even if the database is empty.
            if (trailers == null || trailers.size() < 1) {
                mTrailerViews.setVisibility(View.INVISIBLE);
            } else {
                mTrailerAdapter.setTrailerData(trailers);
            }

            mTrailerViews.setVisibility(View.VISIBLE);
        }
    }

    public class FetchReviewsTask extends AsyncTask<Void, Void, List<Review>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param voids The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected List<Review> doInBackground(Void... voids) {
            final List<Review> reviews;
            URL reviewsRequestURL = null;

            int pageNumber = 1;
            reviewsRequestURL = NetworkUtils.getReviewsForMovie(mMovie, pageNumber);

            if (reviewsRequestURL != null) {
                try{
                    String JSONResponse = NetworkUtils.GetResponseFromHttpUrl(reviewsRequestURL);

                    reviews = ReviewJSONUtils.getReviewsFromJSON(JSONResponse);
                    return reviews;
                } catch (Exception e) {
                    e.printStackTrace();

                    return null;
                }
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Review> reviews) {
            reviewList = reviews;

            // Ths size must be checked first because a NONNULL List is returned
            // even if the database is empty.
            if (reviews == null || reviews.size() < 1) {
                mReviewViews.setVisibility(View.INVISIBLE);
            } else {
                mReviewAdapter.setReviewData(reviews);
            }

            mReviewViews.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Method used to check if the device has a valid internet connection.
     *
     * Source code was taken from the following site:
     *
     * https://stackoverflow.com/questions/1560788/
     * how-to-check-internet-access-on-android-inetaddress-never-times-out
     *
     * @return true if valid internet connection, false if not connected
     */
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}

