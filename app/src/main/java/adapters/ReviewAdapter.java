package adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.moviesone.R;

import java.util.List;

import model.Review;

/**
 * Created by Benny on 9/7/2018.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {
    private static final String TAG = ReviewAdapter.class.getSimpleName();

    private List<Review> mReviewData;

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
    public ReviewAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.review_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new ReviewAdapterViewHolder(view);
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
    public void onBindViewHolder(@NonNull ReviewAdapterViewHolder holder, int position) {
        Review reviewSelected = mReviewData.get(position);

        final TextView reviewAuthor = holder.mReviewAuthor;
        final TextView reviewContent = holder.mReviewContent;
        SpannableString formattedContent = new SpannableString("Author: " + reviewSelected.getAuthor());
        formattedContent.setSpan(new UnderlineSpan(), 0, 6, 0);

        reviewAuthor.setText(formattedContent);
        reviewContent.setText(reviewSelected.getContent());
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if (null == mReviewData) {
            return 0;
        }
        return mReviewData.size();
    }

    /**
     * Allows an outside class to replace the review data stored in the adapter.
     * @param reviewData a list of movies already parsed.
     */
    public void setReviewData(List<Review> reviewData) {
        mReviewData = reviewData;
        notifyDataSetChanged();
    }

    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder {

        public final TextView mReviewAuthor;
        public final TextView mReviewContent;

        public ReviewAdapterViewHolder(View view) {
            super(view);
            mReviewAuthor = (TextView) view.findViewById(R.id.review_author_tv);
            mReviewContent = (TextView) view.findViewById(R.id.review_content_tv);
        }
    }
}

