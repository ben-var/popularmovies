package adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import model.Movie;
import utilities.NetworkUtils;

import com.example.android.moviesone.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;

/**
 * Adapter class built for the RecyclerView in the Main Activity.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder>{

    private static final String TAG = MovieAdapter.class.getSimpleName();

    private List<Movie> mMovieData;

    private final MovieAdapterOnClickHandler mClickHandler;

    public interface MovieAdapterOnClickHandler {
        void onClick(Movie movieSelected);
    }

    public MovieAdapter(MovieAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final ImageView mMovieImageView;

        public MovieAdapterViewHolder(View view) {
            super(view);
            mMovieImageView = (ImageView) view.findViewById(R.id.movie_image_poster);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Movie selectedMovie = mMovieData.get(adapterPosition);
            mClickHandler.onClick(selectedMovie);
        }
    }

    @NonNull
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new MovieAdapterViewHolder(view);
    }

    /**
     * Populates an image into each view of the grid.
     */
    @Override
    public void onBindViewHolder(@NonNull MovieAdapterViewHolder holder, int position) {
        Movie movieSelected = mMovieData.get(position);

        final ImageView movieImage = holder.mMovieImageView;
        final Context context = movieImage.getContext();

        URL movieRequestURL = NetworkUtils.getMoviePosterImageURL(movieSelected);

        Picasso.with(context)
                .load(movieRequestURL.toString())
                .into(movieImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        // nothing necessary
                    }

                    @Override
                    public void onError() {
                        movieImage.setVisibility(View.GONE);
                        Toast.makeText(context,
                                "Error loading image", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Simply returns count of active views in adapter.
     * @return the size of the amount of movies stored in memory.
     */
    @Override
    public int getItemCount() {
        if (null == mMovieData) { return 0; }
        return mMovieData.size();
    }

    /**
     * Allows an outside class to replace the movie data stored in the adapter.
     * @param movieData a list of movies already parsed.
     */
    public void setMovieData(List<Movie> movieData) {
        mMovieData = movieData;
        notifyDataSetChanged();
    }

    public List<Movie> getMovieData() {
        return mMovieData;
    }
}
