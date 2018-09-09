package database;

import android.arch.persistence.room.TypeConverter;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to convert List<Integer> of Genres to a type that Room can use.
 */

public class GenreListConverter {

    public static final String LOG_TAG = GenreListConverter.class.getSimpleName();

    @TypeConverter
    public static List<Integer> toGenreList(String genres) {
        List<Integer> genreList = new ArrayList<Integer>();

        String[] genresArray = null;
        if(genres.length() >= 1 && genres != null) {
            genresArray = genres.split(" ");
        } else {
            return null;
        }

        int parsedInt;
        for (String genre : genresArray) {
            try {
                parsedInt = Integer.parseInt(genre);
            } catch (NumberFormatException e) {
                Log.e(LOG_TAG, genre);
                Log.e(LOG_TAG, "Genre database object is corrupted.");
                throw new RuntimeException("Genre database object is corrupted");
            }

            genreList.add(parsedInt);
        }

        return genreList;
    }

    @NonNull
    @TypeConverter
    public static String toGenreString(List<Integer> genreList) {
        StringBuilder genreString = new StringBuilder();

        if (genreList == null) {
            return null;
        }

        for (Integer genreInt : genreList) {
            genreString.append(genreInt + " ");
        }

        return genreString.toString();
    }
}
