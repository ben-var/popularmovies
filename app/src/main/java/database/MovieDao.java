package database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import model.Movie;

import java.util.List;

/**
 * Movie DAO for interaction with Room
 */
@Dao
public interface MovieDao {

    @Query("SELECT * FROM favorites ORDER BY favoriteId ASC")
    LiveData<List<Movie>> loadAllMovies();

    @Query("DELETE FROM favorites")
    void nukeTable();

    @Insert
    void insertMovie(Movie movie);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMovie(Movie movie);

    @Query("DELETE FROM favorites WHERE movieId=:movieId")
    void deleteMovieByMovieId(int movieId);

    @Query("SELECT * FROM favorites WHERE movieId = :movieId")
    LiveData<Movie> loadMovieById(int movieId);
}
