package es.usal.seriesg.fragments;

import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import es.usal.seriesg.activity.R;
import es.usal.seriesg.adapter.SearchSeriesAdapter;
import es.usal.seriesg.application.SeriesGApplication;
import es.usal.seriesg.service.tmdb.interfaces.TmdbSeriesService;
import es.usal.seriesg.service.tmdb.model.PageResult;
import es.usal.seriesg.service.tmdb.model.TVPageItem;

/**
 * Created by nerko on 30/6/15.
 */
public class SearchTVFragment extends Fragment {

    @InjectView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @InjectView(R.id.no_items_found)
    TextView noItemsFoundText;


    private SearchSeriesAdapter mAdapter;
    private List<TVPageItem> mList;

    protected int page = 1;
    protected int totalPages = 0;
    protected String lastQuery;

    public SearchTVFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.recycler_view, container, false);
        ButterKnife.inject(this, v);
        setupView();
        return v;
    }

    private void setupView() {
        int numCols = 1;
        if(SeriesGApplication.getMResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            numCols = 2;
        }
        GridLayoutManager gridLayoutManager = new GridLayoutManager(SeriesGApplication.getContext(), numCols, OrientationHelper.VERTICAL, false);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mList = new ArrayList<TVPageItem>();
        mAdapter = new SearchSeriesAdapter(mList, getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    search(lastQuery);
                }
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void search(String query) {
        if(!Strings.isNullOrEmpty(query) && SeriesGApplication.isNetworkAvailable()) {
            if (Strings.isNullOrEmpty(lastQuery) || !query.equalsIgnoreCase(lastQuery)) {
                mList.clear();
                page = 1;
                totalPages = 0;
                lastQuery = query;
            }
            new FindSeries().execute(query);
        }
    }

    private class FindSeries extends AsyncTask<String, Void, PageResult<TVPageItem>> {

        private TmdbSeriesService mTmdbSeriesService = SeriesGApplication.getTmdbSeriesService();

        @Override
        protected PageResult<TVPageItem> doInBackground(String... params) {
            if(page == totalPages) {
                return null;
            }
            return mTmdbSeriesService.search(params[0], page);
        }

        @Override
        protected void onPostExecute(PageResult<TVPageItem> tvPageItemPageResult) {
            if(tvPageItemPageResult != null) {
                mList.addAll(tvPageItemPageResult.getResults());
                mAdapter.notifyDataSetChanged();
                totalPages = tvPageItemPageResult.getTotalPages();
                if(page < totalPages) {
                    page = tvPageItemPageResult.getPage() + 1;
                }
            }
            if(mList.isEmpty()) {
                noItemsFoundText.setVisibility(View.VISIBLE);
            } else {
                noItemsFoundText.setVisibility(View.GONE);
            }
        }
    }
}
