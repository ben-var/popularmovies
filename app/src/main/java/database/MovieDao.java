package database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.android.moviesone.Movie;

import java.util.List;

/**
 * Movie DAO for interaction with Room
 */
@Dao
public interface MovieDao {

    @Query("SELECT * FROM favorites ORDER BY favoriteId ASC")
    List<Movie> loadAllMovies();

    @Query("DELETE FROM favorites")
    void nukeTable();

    @Insert
    void insertMovie(Movie movie);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMovie(Movie movie);

    @Delete
    void deleteMovie(Movie movie);
}
