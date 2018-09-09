package utilities;

import android.util.Log;

import model.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Benny on 9/8/2018.
 */

public class ReviewJSONUtils {

    public static final String TAG = ReviewJSONUtils.class.getSimpleName();

    /**
     * JSON keys for Review JSONObjects returned via themovieDB queries.
     */
    public static final String RESULTS = "results";
    public static final String ID = "id";
    public static final String AUTHOR = "author";
    public static final String CONTENT = "content";

    /**
     * Parses a page of movie JSON data and returns the data in a list of structured model class
     * 'Review' format.
     * @param reviewJSONList raw string JSON data for a page of movies.
     * @return list of parsed movies.
     * @throws JSONException if there is an error with type casting to JSONObjects
     */
    public static List<Review> getReviewsFromJSON(String reviewJSONList)
            throws JSONException {

        List<Review> parsedReviewData = new ArrayList<Review>();

        JSONObject reviewJSON = new JSONObject(reviewJSONList);

        // first checking json results for errors
        if (!reviewJSON.has(RESULTS)) {
            return null;
        }

        JSONArray allReviewsArray = reviewJSON.getJSONArray(RESULTS);

        Review reviewToAddEachIteration = null;
        for (int i = 0; i < allReviewsArray.length(); i++) {
            reviewToAddEachIteration = parseReview(allReviewsArray.getJSONObject(i));

            parsedReviewData.add(reviewToAddEachIteration);
        }

        return parsedReviewData;
    }

    /**
     * Parses an individual review from a JSONObject into a Review object.
     * @param JSONReview JSONObject structured as a Review object (un-casted).
     * @return parsed Review object with fields populated.
     */
    public static Review parseReview(JSONObject JSONReview) {

        Review parsedReview;

        try {
            String id = JSONReview.getString(ID);
            String author = JSONReview.getString(AUTHOR);
            String content = JSONReview.getString(CONTENT);

            parsedReview = new Review(id, author, content);

            return parsedReview;

        } catch (JSONException e) {
            Log.e("JSON Format Error: ", e.getMessage());
            return null;
        }
    }
}
