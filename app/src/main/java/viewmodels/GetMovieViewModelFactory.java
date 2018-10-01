package database;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

/**
 * Created by Benny on 9/30/2018.
 */

public class GetMovieViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase mDb;
    private final int mMovieId;

    public GetMovieViewModelFactory(AppDatabase database, int movieId) {
        mDb = database;
        mMovieId = movieId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new GetMovieViewModel(mDb, mMovieId);
    }
}
