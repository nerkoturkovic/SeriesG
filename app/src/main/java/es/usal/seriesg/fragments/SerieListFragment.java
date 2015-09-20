package es.usal.seriesg.fragments;

import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import es.usal.seriesg.activity.R;
import es.usal.seriesg.adapter.SeriesListAdapter;
import es.usal.seriesg.application.SeriesGApplication;
import es.usal.seriesg.generated.dao.TVSerieDao;
import es.usal.seriesg.generated.entities.TVSerie;

/**
 * Created by nerko on 6/7/15.
 */
public class SerieListFragment extends Fragment {

    @InjectView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @InjectView(R.id.no_items_found)
    TextView noItemsFoundText;

    private SeriesListAdapter mAdapter;

    private List<TVSerie> mTvSerieList;
    private TVSerieDao mTvSerieDao;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mTvSerieDao = SeriesGApplication.getTVSerieDao();

        this.mTvSerieList = this.mTvSerieDao.queryBuilder()
                .where(TVSerieDao.Properties.Following.eq(true))
                .orderDesc(TVSerieDao.Properties.Favorite)
                .list();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view, container, false);
        ButterKnife.inject(this, view);


        if(this.mTvSerieList.isEmpty()) {
            this.noItemsFoundText.setText(R.string.series_empty);
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
        mAdapter = new SeriesListAdapter(getActivity(), mTvSerieList);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
    }
}
