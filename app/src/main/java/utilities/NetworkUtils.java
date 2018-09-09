package utilities;

import android.net.Uri;
import android.util.Log;

import model.Movie;
import model.Trailer;

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

    private static final String YOUTUBE_BASE_VIDEO_URL =
            "https://www.youtube.com/watch?v=";

    private static final String YOUTUBE_BASE_VIDEO_URL_FOR_APP = "vnd.youtube:";

    private static final String MOVIE_TRAILER_REVIEW_BASE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String TRAILER_VIDEOS_AFTER_MOVIE_ID = "/videos?api_key=";
    private static final String ENSURE_ENGLISH = "&language=en-US";
    private static final String REVIEW_AFTER_MOVIE_ID = "/reviews?api_key=";
    private static final String ADD_PAGE_NUMBER = "&page=";

    /**
     * URL Segments that are used to build query URL Strings.
     */
    private static final String MOVIE_BASE_URL = "http://image.tmdb.org/t/p/";

    private static final String PAGE = "page";

    private static final String MEDIUM_SMALL_IMAGE_SIZE = "w154";
    private static final String DEFAULT_IMAGE_SIZE = "w185";
    private static final String DEFAULT_LARGE_IMAGE_SIZE = "w500";

    /**
     * Returns a URL to the JSON data from the first page of the most popular movies.
     * @return URL to popular movies (first page) JSON data
     */
    public static URL getPopularMoviesURL() {
        Uri builtUri = Uri.parse(POPULAR_MOVIES_URL);
        return buildURLfromURI(builtUri);
    }

    /**
     * Returns the a URL to the JSON data to the page specified, sorted from most popular
     * to least popular.
     * @param pageNumber page number of movie db info to be scraped.
     * @return URL to popular movies JSON data for specific page.
     */
    public static URL getPopularMoviesURL(int pageNumber) {
        Uri builtUri = Uri.parse(POPULAR_MOVIES_URL + "&" + PAGE + "=" + pageNumber);
        return buildURLfromURI(builtUri);
    }

    /**
     * Returns a URL to the JSON data from the first page of the top-rated movies.
     * @return URL to top-rated (first page) JSON data
     */
    public static URL getTopRatedMoviesURL() {
        Uri builtUri = Uri.parse(TOP_RATED_MOVIES_URL);
        return buildURLfromURI(builtUri);
    }

    /**
     * Returns the a URL to the JSON data to the page specified, sorted from highest rated
     * to lowest rated.
     * @param pageNumber page number of movie db info to be scraped.
     * @return URL to top-rated movies JSON data for specific page.
     */
    public static URL getTopRatedMoviesURL(int pageNumber) {
        Uri builtUri = Uri.parse(TOP_RATED_MOVIES_URL + "&" + PAGE + "=" + pageNumber);
        return buildURLfromURI(builtUri);
    }

    /**
     * Queries a movie poster image from themoviedb.
     * @param movie single movie that poster img is supposed to be requested for.
     * @return direct link to movie poster image.
     */
    public static URL getMoviePosterImageURL(Movie movie) {
        String posterPath = movie.getPosterPath();

        Uri builtUri = Uri.parse(MOVIE_BASE_URL + DEFAULT_IMAGE_SIZE + posterPath);
        return buildURLfromURI(builtUri);
    }

    /**
     * Overloaded getMoviePosterImageURL that allows for size to be specified.
     * @param movie movie to get the image for
     * @param size desired size of image
     * @return direct link to movie poster image for specified size
     */
    public static URL getMoviePosterImageURL(Movie movie, int size) {
        String posterPath = movie.getPosterPath();

        String sizeString = findImageSize(size);

        Uri builtUri = Uri.parse(MOVIE_BASE_URL + sizeString + posterPath);
        return buildURLfromURI(builtUri);
    }

    /**
     * Queries a movie backdrop image from themoviedb.
     * @param movie single movie that poster img is supposed to be requested for.
     * @return direct link to movie backdrop image.
     */
    public static URL getMovieBackdropImageURL(Movie movie) {
        String backdropPath = movie.getBackdropPath();

        Uri builtUri = Uri.parse(MOVIE_BASE_URL + DEFAULT_LARGE_IMAGE_SIZE + backdropPath);
        return buildURLfromURI(builtUri);
    }

    /**
     * Overloaded getMovieBackdropImageURL that allows for size to be specified.
     * @param movie movie to get the image for
     * @param size desired size of image
     * @return direct link to movie backdrop image for specified size
     */
    public static URL getMovieBackdropImageURL(Movie movie, int size) {
        String backdropPath = movie.getBackdropPath();

        String sizeString = findImageSize(size);

        Uri builtUri = Uri.parse(MOVIE_BASE_URL + sizeString + backdropPath);
        return buildURLfromURI(builtUri);
    }

    public static Uri getTrailerYouTubeUriForApp(Trailer trailer) {
        String baseUrl = YOUTUBE_BASE_VIDEO_URL_FOR_APP;

        Uri builtUri = Uri.parse(baseUrl + trailer.getYouTubeUrlKey());
        return builtUri;
    }

    public static URL getTrailerYouTubeURLForApp(Trailer trailer) {
        return buildURLfromURI(getTrailerYouTubeUriForApp(trailer));
    }

    public static Uri getTrailerVideoYouTubeUri(Trailer trailer) {
        String baseUrl = YOUTUBE_BASE_VIDEO_URL;

        Uri builtUri = Uri.parse(baseUrl + trailer.getYouTubeUrlKey());
        return builtUri;
    }

    public static URL getTrailerVideoYouTubeUrl(Trailer trailer) {
        return buildURLfromURI(getTrailerVideoYouTubeUri(trailer));
    }

    public static URL getEngTrailersForMovie(Movie movie) {
        String raw = MOVIE_TRAILER_REVIEW_BASE_URL + movie.getMovieId() + TRAILER_VIDEOS_AFTER_MOVIE_ID
                + API_KEY + ENSURE_ENGLISH;

        Uri builtUri = Uri.parse(raw);

        return buildURLfromURI(builtUri);
    }

    public static URL getReviewsForMovie(Movie movie) {
        String raw = MOVIE_TRAILER_REVIEW_BASE_URL + movie.getMovieId() + REVIEW_AFTER_MOVIE_ID
                + API_KEY + ENSURE_ENGLISH;

        Uri builtUri = Uri.parse(raw);

        return buildURLfromURI(builtUri);
    }

    public static URL getReviewsForMovie(Movie movie, int page) {
        String raw = MOVIE_TRAILER_REVIEW_BASE_URL + movie.getMovieId() + REVIEW_AFTER_MOVIE_ID
                + API_KEY + ENSURE_ENGLISH + ADD_PAGE_NUMBER + page;

        Uri builtUri = Uri.parse(raw);

        return buildURLfromURI(builtUri);
    }
    /**
     * Allows for simple use of image size referencing by other classes.
     * @param size image size using NetworkUtils constants.
     * @return URL Segment for specified size
     */
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

    /**
     * Converts a URI to a URL, and throws necessary exceptions if cannot convert.
     * @param uri URI to be converted
     * @return converted URL
     */
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

    /**
     * Opens a connection to a specified url for data processing.
     * @param url URL to be connected to
     * @return string response
     * @throws IOException if error with opening connection
     */
    public static String GetResponseFromHttpUrl(URL url) throws IOException {

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
