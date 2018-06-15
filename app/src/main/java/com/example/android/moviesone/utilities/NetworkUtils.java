package com.example.android.moviesone.utilities;

import android.net.Uri;
import android.util.Log;

import com.example.android.moviesone.Movie;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Simple Network Utility to build URLs to perform Movie DB Queries.
 */

public class NetworkUtils {

    public static final int MED_SMALL_IMAGE = 0;
    public static final int MEDIUM_IMAGE = 1;
    public static final int LARGE_IMAGE = 2;

    private static final String TAG = NetworkUtils.class.getSimpleName();

    /**
     * IMPORTANT TODO - Do not upload this source code without scrubbing the API KEY
     *
     * NOTE: If there is no API Key, please insert your API key into this variable value;
     */
    private static final String API_KEY = "";

    /** To get additional page numbers, add "&page=[INSERT PAGE #]" to the URL */
    private static final String POPULAR_MOVIES_URL =
            "https://api.themoviedb.org/3/movie/popular?api_key=" + API_KEY;

    private static final String TOP_RATED_MOVIES_URL =
            "https://api.themoviedb.org/3/movie/top_rated?api_key=" + API_KEY;

    private static final String MOVIE_BASE_URL = "http://image.tmdb.org/t/p/";

    private static final String PAGE = "page";

    private static final String MEDIUM_SMALL_IMAGE_SIZE = "w154";
    private static final String DEFAULT_IMAGE_SIZE = "w185";
    private static final String DEFAULT_LARGE_IMAGE_SIZE = "w500";

    public static URL getPopularMoviesURL() {
        Uri builtUri = Uri.parse(POPULAR_MOVIES_URL);
        return buildURLfromURI(builtUri);
    }

    public static URL getPopularMoviesURL(int pageNumber) {
        Uri builtUri = Uri.parse(POPULAR_MOVIES_URL + "&" + PAGE + "=" + pageNumber);
        return buildURLfromURI(builtUri);
    }

    public static URL getTopRatedMoviesURL() {
        Uri builtUri = Uri.parse(TOP_RATED_MOVIES_URL);
        return buildURLfromURI(builtUri);
    }

    public static URL getTopRatedMoviesURL(int pageNumber) {
        Uri builtUri = Uri.parse(TOP_RATED_MOVIES_URL + "&" + PAGE + "=" + pageNumber);
        return buildURLfromURI(builtUri);
    }

    public static URL getMoviePosterImageURL(Movie movie) {
        String posterPath = movie.getPosterPath();

        Uri builtUri = Uri.parse(MOVIE_BASE_URL + DEFAULT_IMAGE_SIZE + posterPath);
        return buildURLfromURI(builtUri);
    }

    public static URL getMoviePosterImageURL(Movie movie, int size) {
        String posterPath = movie.getPosterPath();

        String sizeString = findImageSize(size);

        Uri builtUri = Uri.parse(MOVIE_BASE_URL + sizeString + posterPath);
        return buildURLfromURI(builtUri);
    }

    public static URL getMovieBackdropImageURL(Movie movie) {
        String backdropPath = movie.getBackdropPath();

        Uri builtUri = Uri.parse(MOVIE_BASE_URL + DEFAULT_LARGE_IMAGE_SIZE + backdropPath);
        return buildURLfromURI(builtUri);
    }

    public static URL getMovieBackdropImageURL(Movie movie, int size) {
        String backdropPath = movie.getBackdropPath();

        String sizeString = findImageSize(size);

        Uri builtUri = Uri.parse(MOVIE_BASE_URL + sizeString + backdropPath);
        return buildURLfromURI(builtUri);
    }

    private static String findImageSize(int size) {
        switch (size) {
            case MED_SMALL_IMAGE:
                return MEDIUM_SMALL_IMAGE_SIZE;
            case MEDIUM_IMAGE:
                return DEFAULT_IMAGE_SIZE;
            case LARGE_IMAGE:
                return DEFAULT_LARGE_IMAGE_SIZE;
            default:
                return DEFAULT_IMAGE_SIZE;
        }
    }

    public static URL buildURLfromURI(Uri uri) {
        URL url = null;

        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    public static String getReponseFromHttpUrl(URL url) throws IOException {

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try{
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }

        } finally {
            urlConnection.disconnect();
        }
    }

}
