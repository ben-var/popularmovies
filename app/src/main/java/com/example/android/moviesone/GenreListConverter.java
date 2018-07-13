package com.example.android.moviesone;

import android.arch.persistence.room.TypeConverter;
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

        String[] genresArray = genres.split(" ");
        int parsedInt;
        for (String genre : genresArray) {
            try {
                parsedInt = Integer.parseInt(genre);
            } catch (NumberFormatException e) {
                Log.e(LOG_TAG, "Genre database object is corrupted.");
                throw new RuntimeException("Genre database object is corrupted");
            }

            genreList.add(parsedInt);
        }

        return genreList;
    }

    @TypeConverter
    public static String toGenreString(List<Integer> genreList) {
        StringBuilder genreString = new StringBuilder();

        for (Integer genreInt : genreList) {
            genreString.append(genreInt);
        }

        return genreString.toString();
    }
}
