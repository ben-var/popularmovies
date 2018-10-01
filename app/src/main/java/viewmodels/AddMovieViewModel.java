package viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import database.AppDatabase;
import model.Movie;

/**
 * Created by Benny on 9/30/2018.
 */

public class AddMovieViewModel extends ViewModel {

    private static final String TAG = MainViewModel.class.getSimpleName();

    private LiveData<Movie> movie;

    public AddMovieViewModel(AppDatabase database, Movie movie) {
        database.movieDao().insertMovie(movie);
    }

    public LiveData<Movie> getMovie() {
        return movie;
    }
}
