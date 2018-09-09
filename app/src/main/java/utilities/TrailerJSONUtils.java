package utilities;

import android.util.Log;

import model.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Benny on 9/8/2018.
 */

public class TrailerJSONUtils {

    public static final String TAG = TrailerJSONUtils.class.getSimpleName();

    /**
     * JSON keys for Trailer JSONObjects returned via themovieDB queries.
     */
    public static final String RESULTS = "results";
    public static final String ID = "id";
    public static final String YOUTUBE_KEY = "key";
    public static final String NAME = "name";
    public static final String SIZE = "size";
    public static final String SITE = "site";

    /**
     * Parses a page of trailer JSON data and returns the data in a list of structured model class
     * 'Trailer' format.
     * @param trailerJSONList raw string JSON data for a page of trailers.
     * @return list of parsed trailers.
     * @throws JSONException if there is an error with type casting to JSONObjects
     */
    public static List<Trailer> getTrailersFromJSON(String trailerJSONList)
            throws JSONException {

        List<Trailer> parsedTrailerData = new ArrayList<Trailer>();

        JSONObject trailerJSON = new JSONObject(trailerJSONList);

        // first checking json results for errors
        if (!trailerJSON.has(RESULTS)) {
            return null;
        }

        JSONArray allTrailersArray = trailerJSON.getJSONArray(RESULTS);

        Trailer trailerToAddEachIteration = null;
        for (int i = 0; i < allTrailersArray.length(); i++) {
            trailerToAddEachIteration = parseTrailer(allTrailersArray.getJSONObject(i));

            parsedTrailerData.add(trailerToAddEachIteration);
        }

        return parsedTrailerData;
    }

    /**
     * Parses an individual trailer from a JSONObject into a Trailer object.
     * @param JSONTrailer JSONObject structured as a trailer object (un-casted).
     * @return parsed Trailer object with fields populated.
     */
    public static Trailer parseTrailer(JSONObject JSONTrailer) {

        Trailer parsedTrailer;

        try {
            String id = JSONTrailer.getString(ID);
            String youTubeKey = JSONTrailer.getString(YOUTUBE_KEY);
            String name = JSONTrailer.getString(NAME);
            int size = JSONTrailer.getInt(SIZE);
            String site = JSONTrailer.getString(SITE);

            parsedTrailer = new Trailer(id, youTubeKey, name, size, site);

            return parsedTrailer;


        } catch (JSONException e) {
            Log.e("JSON Format Error: ", e.getMessage());
            return null;
        }
    }
}
