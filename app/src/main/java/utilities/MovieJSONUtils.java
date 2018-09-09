package utilities;

import android.util.Log;

import model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * JSON parser for data queried from themoviedb.
 *
 * Based off documentation found at https://developers.themoviedb.org/3/discover/movie-discover
 */
public class MovieJSONUtils {

    public static final String TAG = MovieJSONUtils.class.getSimpleName();

    /**
     * All JSON keys for movie JSONObjects returned via themovieDB queries.
     */
    public static final String PAGE = "page";
    public static final String RESULTS = "results";
    public static final String POSTER_PATH = "poster_path";
    public static final String ADULT_BOOL = "adult";
    public static final String OVERVIEW = "overview";
    public static final String RELEASE_DATE = "release_date";
    public static final String GENRE_ID_INTARRAY = "genre_ids";
    public static final String MOVIE_ID = "id";
    public static final String ORIGINAL_TITLE = "original_title";
    public static final String ORIGINAL_LANGUAGE = "original_language";
    public static final String TITLE = "title";
    public static final String BACKDROP_PATH = "backdrop_path";
    public static final String POPULARITY = "popularity";
    public static final String VOTE_COUNT = "vote_count";
    public static final String VIDEO = "video";
    public static final String VOTE_AVERAGE = "vote_average";

    /**
     * Parses a page of movie JSON data and returns the data in a list of structured model class
     * 'Movie' format.
     * @param movieJSONList raw string JSON data for a page of movies.
     * @return list of parsed movies.
     * @throws JSONException if there is an error with type casting to JSONObjects
     */
    public static List<Movie> getMoviesFromJSON(String movieJSONList)
            throws JSONException {

        List<Movie> parsedMovieData = new ArrayList<Movie>(50);

        JSONObject movieJSON = new JSONObject(movieJSONList);

        // first checking json results for errors
        if (!movieJSON.has(RESULTS)) {
            return null;
        }

        JSONArray allMoviesArray = movieJSON.getJSONArray(RESULTS);

        Movie movieToAddEachIteration = null;
        for (int i = 0; i < allMoviesArray.length(); i++) {
            movieToAddEachIteration = parseMovie(allMoviesArray.getJSONObject(i));

            parsedMovieData.add(movieToAddEachIteration);
        }

        return parsedMovieData;
    }

    /**
     * Parses an individual movie from a JSONObject into a Movie object.
     * @param JSONMovie JSONObject structured as a movie object (un-casted).
     * @return parsed Movie object with fields populated.
     */
    public static Movie parseMovie(JSONObject JSONMovie) {

        Movie parsedMovie;

        try {
           String posterPath = JSONMovie.getString(POSTER_PATH);
           boolean adult = JSONMovie.getBoolean(ADULT_BOOL);
           String overview = JSONMovie.getString(OVERVIEW);
           String releaseDate = JSONMovie.getString(RELEASE_DATE);

            // using private helper method to parse integer JSON array
           List<Integer> genreIdArray = parseJSONIntArray(
                                        JSONMovie.getJSONArray(GENRE_ID_INTARRAY));

           int movieID = JSONMovie.getInt(MOVIE_ID);
           String originalTitle = JSONMovie.getString(ORIGINAL_TITLE);
           String originalLanguage = JSONMovie.getString(ORIGINAL_LANGUAGE);
           String title = JSONMovie.getString(TITLE);
           String backdropPath = JSONMovie.getString(BACKDROP_PATH);
           double popularity = JSONMovie.getDouble(POPULARITY);
           int voteCount = JSONMovie.getInt(VOTE_COUNT);
           boolean video = JSONMovie.getBoolean(VIDEO);
           double voteAverage = JSONMovie.getDouble(VOTE_AVERAGE);

           parsedMovie = new Movie(posterPath, adult, overview, releaseDate, genreIdArray,
                                    movieID, originalTitle, originalLanguage, title,
                                    backdropPath, popularity, voteCount, video, voteAverage);

           return parsedMovie;


        } catch (JSONException e) {
            Log.e("JSON Format Error: ", e.getMessage());
            return null;
        }
    }

    /**
     * Parses a JSONArray (int) into a Java List.
     * @param intArray JSONArray of type int
     * @return Java List of type Integer
     */
    private static List<Integer> parseJSONIntArray(JSONArray intArray) {
        List<Integer> parsedIntArray = new ArrayList<Integer>();

        try {
            // checking for valid data type (integer)
            if (intArray.length() != 0 && !(intArray.get(0) instanceof Integer)) {
                throw new JSONException("Invalid Type of Array Passed.");
            }

            for (int i = 0; i < intArray.length(); i++) {
                parsedIntArray.add(intArray.getInt(i));
            }

        } catch (JSONException e) {
            Log.e("JSONArray Exception: ", e.getMessage());
            return null;
        }

        return parsedIntArray;
    }
}
