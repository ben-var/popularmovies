package adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.android.moviesone.R;

import java.util.List;

import model.Trailer;

/**
 * Created by Benny on 9/7/2018.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {
    private static final String TAG = TrailerAdapter.class.getSimpleName();

    private List<Trailer> mTrailerData;

    private final TrailerAdapterOnClickHandler mClickHandler;

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(ViewHolder, int, List)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(ViewHolder, int)
     */
    @NonNull
    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.trailer_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new TrailerAdapterViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
     * position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link ViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p>
     * Override {@link #onBindViewHolder(ViewHolder, int, List)} instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull TrailerAdapterViewHolder holder, int position) {
        Trailer trailerSelected = mTrailerData.get(position);

        final Button trailerButton = holder.mTrailerButton;

        trailerButton.setText(trailerSelected.getName());
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if (null == mTrailerData) {
            return 0;
        }
        return mTrailerData.size();
    }

    public interface TrailerAdapterOnClickHandler {
        void onClick(Trailer trailerSelected);
    }

    public TrailerAdapter(TrailerAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    /**
     * Allows an outside class to replace the trailer data stored in the adapter.
     * @param trailerData a list of trailers already parsed.
     */
    public void setTrailerData(List<Trailer> trailerData) {
        mTrailerData = trailerData;
        notifyDataSetChanged();
    }

    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

       // public final TextView mTrailerTitle;
        public final Button mTrailerButton;

        public TrailerAdapterViewHolder(View view) {
            super(view);
            //mTrailerTitle = (TextView) view.findViewById(R.id.trailer_title_tv);
            mTrailerButton = (Button) view.findViewById(R.id.play_trailer_button);
            mTrailerButton.setOnClickListener(this);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Trailer selectedTrailer = mTrailerData.get(adapterPosition);
            mClickHandler.onClick(selectedTrailer);
        }
    }
}
