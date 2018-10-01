package database;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import model.Movie;

/**
 * Created by Benny on 9/30/2018.
 */

public class DeleteMovieViewModel extends ViewModel {

    private static final String TAG = MainViewModel.class.getSimpleName();

    private LiveData<Movie> movie;

    public DeleteMovieViewModel(AppDatabase database, int movieId) {
        database.movieDao().deleteMovieByMovieId(movieId);
    }

}
