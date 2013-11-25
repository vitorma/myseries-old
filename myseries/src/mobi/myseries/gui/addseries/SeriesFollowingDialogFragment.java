package mobi.myseries.gui.addseries;

import java.util.Collection;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.following.BaseSeriesFollowingListener;
import mobi.myseries.application.following.SeriesFollowingListener;
import mobi.myseries.domain.model.SearchResult;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.shared.UniversalImageLoader;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class SeriesFollowingDialogFragment extends DialogFragment {

    private static final String TAG_REMOVAL_CONFIRMATION_DIALOG = "SeriesRemovalConfirmationDialog";
    private static final String TAG = "SeriesFollowingDialogFragment";
    private static final String ARGUMENT_SERIES = "series";

    public static void showDialog(SearchResult series, FragmentManager fm) {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag(TAG);

        if (prev != null) {
            ft.remove(prev);
        }

        ft.addToBackStack(null);

        DialogFragment newFragment = SeriesFollowingDialogFragment.newInstance(series);
        newFragment.show(ft, TAG);
    }

    private static SeriesFollowingDialogFragment newInstance(SearchResult series) {
        SeriesFollowingDialogFragment fragment = new SeriesFollowingDialogFragment();
        Bundle args = new Bundle();

        args.putParcelable(ARGUMENT_SERIES, series);
        fragment.setArguments(args);

        return fragment;
    }

    private SearchResult mSeries;
    private Button mAddButton;
    private Button mRemoveButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(STYLE_NO_TITLE, 0);
    }

    @Override
    public void onStart() {
        super.onStart();

        App.seriesFollowingService().register(mSeriesFollowingListener);
    }

    @Override
    public void onStop() {
        super.onStop();

        App.seriesFollowingService().deregister(mSeriesFollowingListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.addseries_series_information, null);

        mSeries = getArguments().getParcelable(ARGUMENT_SERIES);

        ((TextView) layout.findViewById(R.id.title)).setText(mSeries.title());
        ((TextView) layout.findViewById(R.id.genres)).setText(mSeries.genres());
        ((TextView) layout.findViewById(R.id.overview)).setText(mSeries.overview());

        Button dismissButton = (Button) layout.findViewById(R.id.dismissButton);
        dismissButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mAddButton = (Button) layout.findViewById(R.id.addButton);
        mAddButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                App.seriesFollowingService().follow(mSeries);
                dismiss();
            }
        });

        mRemoveButton = (Button) layout.findViewById(R.id.removeButton);
        mRemoveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SeriesRemovalConfirmationDialogFragment.newInstance(mSeries).show(getFragmentManager(), TAG_REMOVAL_CONFIRMATION_DIALOG);
                dismiss();
            }
        });

        mAddButton.setVisibility(shouldHideAddButton() ? View.INVISIBLE : View.VISIBLE);
        mRemoveButton.setVisibility(shouldHideRemoveButton() ? View.INVISIBLE : View.VISIBLE);

        mAddButton.setEnabled(!shouldDisableButtons());
        mRemoveButton.setEnabled(!shouldDisableButtons());

        if (mRemoveButton.isEnabled()) {
            mRemoveButton.setTextColor(App.resources().getColor(R.color.dark_red));
        } else {
            mRemoveButton.setTextColor(App.resources().getColor(R.color.dark_gray));
        }

        if (mSeries.poster() != null) {
            final ImageView posterImageView = (ImageView) layout.findViewById(R.id.poster);

            String posterFilePath = App.imageService().getPosterPath(mSeries.toSeries());
            if(posterFilePath != null) {
                UniversalImageLoader.loader().displayImage(UniversalImageLoader.fileURI(posterFilePath), 
                        posterImageView, 
                        UniversalImageLoader.defaultDisplayBuilder()
                        .showImageOnFail(R.drawable.generic_poster)
                        .build());
            } else {
                UniversalImageLoader.loader().displayImage(UniversalImageLoader.httpURI(mSeries.poster()), 
                        posterImageView, 
                        UniversalImageLoader.defaultDisplayBuilder()
                        .showImageOnFail(R.drawable.generic_poster).build());
            }
        }

        return layout;
    }

    private boolean shouldHideAddButton() {
        return App.seriesFollowingService().follows(mSeries.toSeries());
    }

    private boolean shouldHideRemoveButton() {
        return !shouldHideAddButton();
    }

    private boolean shouldDisableButtons() {
        return App.seriesFollowingService().isTryingToFollowSeries(mSeries.tvdbIdAsInt()) ||
                App.seriesFollowingService().isTryingToUnfollowSeries(mSeries.tvdbIdAsInt());
    }

    private SeriesFollowingListener mSeriesFollowingListener = new BaseSeriesFollowingListener() {
        @Override
        public void onSuccessToFollow(Series followedSeries) {
            if (mSeries.tvdbIdAsInt() == followedSeries.id()) {
                showRemoveButton();
            }
        }

        @Override
        public void onSuccessToUnfollow(Series unfollowedSeries) {
            if (mSeries.tvdbIdAsInt() == unfollowedSeries.id()) {
                showAddButton();
            }
        }

        @Override
        public void onSuccessToUnfollowAll(Collection<Series> allUnfollowedSeries) {
            for (Series s : allUnfollowedSeries) {
                if (s.id() == mSeries.tvdbIdAsInt()) {
                    showAddButton();
                    break;
                }
            }

        }

        @Override
        public void onFailToFollow(SearchResult seriesToFollow, Exception e) {
            if (mSeries.tvdbId().equals(seriesToFollow.tvdbId())) {
                showAddButton();
            }
        }

        @Override
        public void onFailToUnfollow(Series seriesToUnfollow, Exception e) {
            if (mSeries.tvdbIdAsInt() == seriesToUnfollow.id()) {
                showRemoveButton();
            }
        }

        @Override
        public void onFailToUnfollowAll(Collection<Series> allSeriesToUnfollow, Exception e) {
            for (Series s : allSeriesToUnfollow) {
                if (s.id() == mSeries.tvdbIdAsInt()) {
                    showRemoveButton();
                    break;
                }
            }
        }

        private void showAddButton() {
            mAddButton.setVisibility(View.VISIBLE);
            mRemoveButton.setVisibility(View.INVISIBLE);

            mAddButton.setEnabled(true);
        }

        private void showRemoveButton() {
            mAddButton.setVisibility(View.INVISIBLE);
            mRemoveButton.setVisibility(View.VISIBLE);

            mRemoveButton.setEnabled(true);
            mRemoveButton.setTextColor(App.resources().getColor(R.color.dark_red));
        }
    };
}
