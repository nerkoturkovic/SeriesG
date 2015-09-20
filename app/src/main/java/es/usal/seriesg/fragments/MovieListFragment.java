package es.usal.seriesg.fragments;

import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import es.usal.seriesg.activity.R;
import es.usal.seriesg.adapter.MovieListAdapter;
import es.usal.seriesg.application.SeriesGApplication;
import es.usal.seriesg.generated.dao.MovieDao;
import es.usal.seriesg.generated.entities.Movie;

/**
 * Created by nerko on 8/7/15.
 */
public class MovieListFragment extends Fragment {

    @InjectView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @InjectView(R.id.no_items_found)
    TextView noItemsFoundText;

    private MovieListAdapter mAdapter;
    private List<Movie> mMovieList;
    private MovieDao mMovieDao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mMovieDao = SeriesGApplication.getMovieDao();
        this.mMovieList = this.mMovieDao.queryBuilder()
                .where(MovieDao.Properties.Seen.eq(true))
                .list();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view, container, false);
        ButterKnife.inject(this, view);

        if(this.mMovieList.isEmpty()) {
            this.noItemsFoundText.setText(R.string.movies_empty);
            this.noItemsFoundText.setVisibility(View.VISIBLE);
        }

        setupView();

        return view;
    }

    private void setupView() {
        int numCols = 1;
        if(SeriesGApplication.getMResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            numCols = 2;
        }
        GridLayoutManager gridLayoutManager = new GridLayoutManager(SeriesGApplication.getContext(), numCols, OrientationHelper.VERTICAL, false);
        mAdapter = new MovieListAdapter(getActivity(), mMovieList);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
    }
}
