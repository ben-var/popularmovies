package viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import database.AppDatabase;
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
