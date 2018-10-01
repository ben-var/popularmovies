package com.example.android.moviesone;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import adapters.MovieAdapter;
import adapters.MovieAdapter.MovieAdapterOnClickHandler;

import database.AppDatabase;
import database.AppExecutors;
import model.Movie;
import utilities.MovieJSONUtils;
import utilities.NetworkUtils;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Main Activity of App is a grid view of movie poster images. Images are queried from
 * themoviedb. A recycler view is used in order to populate the main GUI page.
 *
 * @author Ben Vargas
 * @version 1.1 (prototype)
 * Date Last Modified: 6/20/2018
 */
public class MainActivity extends AppCompatActivity implements MovieAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String MOVIE_CLASS =  Movie.class.getSimpleName();
    public static final String PARCELABLE_KEY = "List Of Movies";
    public static final String PARCELABLE_SORT_STATE = "Sort State";

    private AppDatabase mDb;

    private static final String POPULAR_SORT = "popular";
    private static final String TOP_RATED_SORT = "top-rated";
    private static final String FAVORITES_DISPLAY = "favorites";
    private static final String DEFAULT_SORT = POPULAR_SORT;

    private static boolean needsNetworkCall = true;

    private static String stateOfSortPreferred = DEFAULT_SORT;

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private TextView mErrorMessageDisplay;
    private TextView mEmptyFavoritesMessageDisplay;
    private ProgressBar mLoadingIndicator;

    private List<Movie> moviesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        mDb = AppDatabase.getInstance(getApplicationContext());

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movie);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mEmptyFavoritesMessageDisplay = (TextView) findViewById(R.id.empty_favorites_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        int numOfColumns = 2;
        GridLayoutManager layoutManager = new GridLayoutManager(
                this, numOfColumns);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);
        mLoadingIndicator.setVisibility(View.VISIBLE);

        if (savedInstanceState != null) {
            stateOfSortPreferred = savedInstanceState.getString(PARCELABLE_SORT_STATE);
            moviesList = savedInstanceState.getParcelableArrayList(PARCELABLE_KEY);
            if (moviesList == null) {
                needsNetworkCall = true;
            } else {
                needsNetworkCall = false;
            }
            loadMovieData();
            needsNetworkCall = true;
        } else {
            loadMovieData();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);

        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(PARCELABLE_KEY, (ArrayList<Movie>) moviesList);
        outState.putString(PARCELABLE_SORT_STATE, stateOfSortPreferred);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        final LiveData<List<Movie>> moviesLD;
        int itemId = item.getItemId();

        if (itemId == R.id.display_favorites) {
            stateOfSortPreferred = FAVORITES_DISPLAY;
        } else if (itemId == R.id.popular_sort_option) {
            stateOfSortPreferred = POPULAR_SORT;
        } else if (itemId == R.id.top_rated_sort_option) {
            stateOfSortPreferred = TOP_RATED_SORT;
        } else if (itemId == R.id.refresh_page_option) {
            // no change in state required
            moviesLD = mDb.movieDao().loadAllMovies();
            moviesLD.observe(this, new Observer<List<Movie>>() {
                @Override
                public void onChanged(@Nullable List<Movie> movies) {
                    Log.d(TAG, "Receiving database update from LiveData");
                    mMovieAdapter.setMovieData(movies);
                    if (mMovieAdapter.getMovieData().size() < 1) {
                        showEmptyFavoritesMessage();
                    }

                    moviesLD.removeObserver(this);
                }
            });
        } else if (itemId == R.id.clear_all_favorites) {
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    mDb.movieDao().nukeTable();
                }
            });

            moviesLD = mDb.movieDao().loadAllMovies();
            moviesLD.observe(this, new Observer<List<Movie>>() {
                @Override
                public void onChanged(@Nullable List<Movie> movies) {
                    Log.d(TAG, "Receiving database update from LiveData");
                    mMovieAdapter.setMovieData(movies);

                    moviesLD.removeObserver(this);
                }
            });

            Toast.makeText(getApplicationContext(), getString(R.string.deleted_favorites_message),
                    Toast.LENGTH_SHORT)
                    .show();

            showEmptyFavoritesMessage();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }

        Toast.makeText(MainActivity.this, getString(R.string.page_load_confirm),
                                                                    Toast.LENGTH_SHORT).show();

        // only runs if one of options defined in settings is selected
        // value of stateOfSortPreferred will determine which network call is made
        loadMovieData();
        return true;
    }

    /**
     * Kickoff method that starts the query for the movie information (i.e., poster
     * images attached to instances of the Movie class after JSONObjects from themoviedb
     * are parsed.
     */
    private void loadMovieData() {

        if (isOnline()) {
            showMovieDataView();
            if (needsNetworkCall) {
                new FetchMoviesTask().execute(stateOfSortPreferred);
            } else {
                showMovieDataView();
            }
        } else {
            Log.e(TAG, stateOfSortPreferred);
            showErrorMessage();
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

    /**
     * Starts a new DetailActivity for a movie poster that is clicked.
     * @param movieSelected movie that was pressed from the Main Activity.
     */
    @Override
    public void onClick(Movie movieSelected) {
        Log.e("TSTING", movieSelected.toString());
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(MOVIE_CLASS, (Parcelable) movieSelected);
        startActivity(intentToStartDetailActivity);
    }

    /**
     * Hides the error display and shows the movie poster grid.
     */
    private void showMovieDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mEmptyFavoritesMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Hides the movie poster grid and shows the error display.
     */
    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mEmptyFavoritesMessageDisplay.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        Log.e(TAG, getString(R.string.network_error_message));

        mLoadingIndicator.setVisibility(View.INVISIBLE);
    }

    private void showEmptyFavoritesMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mEmptyFavoritesMessageDisplay.setVisibility(View.VISIBLE);
        Log.e(TAG, getString(R.string.null_favorites_error));

        mLoadingIndicator.setVisibility(View.INVISIBLE);

    }

    /**
     * Asynctask that will perform an asynchronous query to return to the main activity.
     */
    public class FetchMoviesTask extends AsyncTask<String, Void, List<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param strings The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected List<Movie> doInBackground(String... strings) {

            final List<Movie> movies;
            final LiveData<List<Movie>> moviesLD;
            URL movieRequestURL = null;

            if (strings.length == 0) { return null; }

            String sortBy = strings[0];
            if (sortBy.equals(POPULAR_SORT)) {
                movieRequestURL = NetworkUtils.getPopularMoviesURL();
            } else if (sortBy.equals(TOP_RATED_SORT)) {
                movieRequestURL = NetworkUtils.getTopRatedMoviesURL();
            } else if (sortBy.equals(FAVORITES_DISPLAY)) {
                // Project was built with Asynctask prior to implementation
                // of LiveData - code can be refactored to better utilize
                // features of LiveData.
                moviesLD = mDb.movieDao().loadAllMovies();
                moviesLD.observe(MainActivity.this, new Observer<List<Movie>>() {
                    @Override
                    public void onChanged(@Nullable List<Movie> moviesObs) {
                       mMovieAdapter.setMovieData(moviesObs);
                       moviesLD.removeObserver(this);
                    }
                });
                return mMovieAdapter.getMovieData();
            } else {
                return null;
            }

            if (movieRequestURL != null) {
                try{
                    String JSONResponse = NetworkUtils.GetResponseFromHttpUrl(movieRequestURL);

                    movies = MovieJSONUtils.getMoviesFromJSON(JSONResponse);
                    return movies;
                } catch (Exception e) {
                    e.printStackTrace();

                    return null;
                }
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {

            moviesList = movies;

            // Ths size must be checked first because a NONNULL List is returned
            // even if the database is empty.
            if (movies == null) {
                showErrorMessage();
            } else if (stateOfSortPreferred.equals(FAVORITES_DISPLAY)) {
                if (movies.size() < 1) {
                    showEmptyFavoritesMessage();
                }
                else {
                    showMovieDataView();
                }
            } else {
                mMovieAdapter.setMovieData(movies);
            }

            mLoadingIndicator.setVisibility(View.INVISIBLE);
        }
    }

}
